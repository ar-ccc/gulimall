package com.arccc.gulimall.order.service.impl;

import com.arccc.common.utils.PageUtils;
import com.arccc.common.utils.Query;
import com.arccc.common.utils.R;
import com.arccc.common.vo.MemberRespVo;
import com.arccc.common.vo.OrderItemProductRespVo;
import com.arccc.common.vo.mq.OrderTo;
import com.arccc.gulimall.order.constant.OrderConstant;
import com.arccc.gulimall.order.dao.OrderDao;
import com.arccc.gulimall.order.entity.OrderEntity;
import com.arccc.gulimall.order.entity.OrderItemEntity;
import com.arccc.gulimall.order.entity.OrderReturnReasonEntity;
import com.arccc.gulimall.order.entity.PaymentInfoEntity;
import com.arccc.gulimall.order.enume.OrderStatusEnum;
import com.arccc.gulimall.order.feign.*;
import com.arccc.gulimall.order.interceptor.UserLoginInterceptor;
import com.arccc.gulimall.order.service.OrderItemService;
import com.arccc.gulimall.order.service.OrderService;
import com.arccc.gulimall.order.service.PaymentInfoService;
import com.arccc.gulimall.order.to.OrderCreateTo;
import com.arccc.gulimall.order.vo.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service("orderService")
@RabbitListener(queues = {"hello_java_queue"})
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private MemberFeignService memberFeignService;
    @Autowired
    private CartFeignService cartFeignService;
    @Autowired
    private ThreadPoolExecutor executor;
    @Autowired
    private WareFeignService wareFeignService;

    @Autowired
    private ProductFeignService productFeignService;


    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private CouponFeignService couponFeignService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private PaymentInfoService paymentInfoService;


    @Autowired
    public void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException {
        OrderConfirmVo orderConfirmVo = new OrderConfirmVo();
        MemberRespVo memberRespVo = UserLoginInterceptor.memberVo.get();
        //获取请求头数据
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();

        //1、远程查询用户所有收货地址
        CompletableFuture<Void> c1 = CompletableFuture.runAsync(() -> {
            //共享原生请求数据
            RequestContextHolder.setRequestAttributes(attributes);
            orderConfirmVo.setAddress(memberFeignService.getList(memberRespVo.getId()));
        }, executor);

        //2、远程查询购物车所有选中的购物项
        CompletableFuture<Void> c2 = CompletableFuture.runAsync(() -> {
            //共享原生请求数据
            RequestContextHolder.setRequestAttributes(attributes);
            orderConfirmVo.setItems(cartFeignService.getItems());
        }, executor).thenRunAsync(() -> {
            //查询是否有库存
            List<OrderItemVo> items = orderConfirmVo.getItems();
            List<Long> ids = items.stream().map(OrderItemVo::getSkuId).collect(Collectors.toList());
            Map<Long, Boolean> hasStock = wareFeignService.getHasStock(ids);
            List<OrderItemVo> collect = items.stream().peek(item -> item.setHasStock(hasStock.get(item.getSkuId()))).collect(Collectors.toList());
            orderConfirmVo.setItems(collect);
        }, executor);

        //3、查询用户积分
        Integer integration = memberRespVo.getIntegration();
        orderConfirmVo.setIntegration(integration);
        //4、等待线程执行完毕
        CompletableFuture.allOf(c1, c2).get();

        //todo 5、防重令牌
        String token = UUID.randomUUID().toString().replace("-", "");
        stringRedisTemplate.opsForValue().set(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberRespVo.getId(), token, 30, TimeUnit.MINUTES);
        orderConfirmVo.setOrderToken(token);
        return orderConfirmVo;

    }

    /**
     * 下单
     *
     * @param vo
     * @return
     */
//    @GlobalTransactional
    @Transactional
    @Override
    public SubmitRespVO placeAnOrder(OrderSubmitVo vo) {
        SubmitRespVO submitRespVO = new SubmitRespVO();
        MemberRespVo memberRespVo = UserLoginInterceptor.memberVo.get();
        String key = OrderConstant.USER_ORDER_TOKEN_PREFIX + memberRespVo.getId();
        //验证token，下订单，锁定库存
        //1、验证token，返回0删除失败，1删除成功
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        Long result = stringRedisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class), Arrays.asList(key), vo.getOrderToken());
        if (result == null || result == 0) {
            //token验证失败
            submitRespVO.setCode(1);
            return submitRespVO;
        } else {
            //验证成功
            submitRespVO.setCode(0);
            // 生成订单
            OrderCreateTo order = createOrder(vo);

            // 保存订单
            saveOrder(order);

            // 锁定库存
            WareSkuLockVo wareSkuLockVo = new WareSkuLockVo();
            wareSkuLockVo.setOrderSn(order.getOrderEntity().getOrderSn());
            List<OrderItemVo> itemVos = order.getItems().stream().map(item -> {
                OrderItemVo orderItemVo = new OrderItemVo();
                orderItemVo.setSkuId(item.getSkuId());
                orderItemVo.setCount(item.getSkuQuantity());
                orderItemVo.setTitle(item.getSkuName());
                return orderItemVo;
            }).collect(Collectors.toList());
            wareSkuLockVo.setLocks(itemVos);

            R r = wareFeignService.orderLockStock(wareSkuLockVo);
            if (r.getCode()!=0) {
                //失败
                String error = (String) r.get("data");
                submitRespVO.setCode(2);
                throw new RuntimeException(error);
            }
            //发送消息
            rabbitTemplate.convertAndSend("order-event-exchange","order.create.order",order.getOrderEntity());
            submitRespVO.setOrderEntity(order.getOrderEntity());
            return submitRespVO;
        }
    }

    /**
     * 关闭订单
     * @param orderEntity
     */
    @Override
    public void closeOrder(OrderEntity orderEntity) {
        //获取最新订单状态
        OrderEntity byId = this.getById(orderEntity.getId());
        //查看订单状态，如果为没有支付就关闭订单
        if (byId.getStatus().equals(OrderStatusEnum.CREATE_NEW.getCode())){
            OrderEntity entity = new OrderEntity();
            entity.setId(orderEntity.getId());
            entity.setStatus(OrderStatusEnum.CANCLED.getCode());
            this.updateById(entity);
            OrderTo orderTo = new OrderTo();
            BeanUtils.copyProperties(byId,orderTo);
            //TODO 保证消息一定发送成功，
            /*
            1、使用日志将消息存到数据库
            2、开启成功发布确认和手动确认
             */
            rabbitTemplate.convertAndSend("stock-event-exchange","order.release.other",orderTo);
        }
    }

    @Override
    public OrderEntity getOrderByOrderSn(String orderSn) {
        return baseMapper.selectOne(new QueryWrapper<OrderEntity>().eq("order_sn",orderSn));
    }

    @Override
    public PayVo getPayVoByOrderSn(String orderSn) {
        OrderEntity orderEntity = getOrderByOrderSn(orderSn);
        if (orderEntity==null || Objects.equals(orderEntity.getStatus(), OrderStatusEnum.CANCLED.getCode())){
            throw new RuntimeException("订单号已经过期");
        }
        PayVo payVo = new PayVo();
        //设置过期时间
        long time = orderEntity.getCreateTime().getTime();
        Date date = new Date(time + 1000 * 60*30);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = simpleDateFormat.format(date);
        payVo.setOrderTimoOut(format);


        String payAmount = orderEntity.getPayAmount().setScale(2, RoundingMode.UP).toString();
        List<OrderItemEntity> list = orderItemService.list(new QueryWrapper<OrderItemEntity>().eq("order_sn", orderEntity));
        payVo.setTotal_amount(payAmount);
        payVo.setOut_trade_no(orderSn);
        if (list==null || list.size()==0){
            payVo.setSubject("谷粒商城商品");
        }else {
            OrderItemEntity orderItemEntity = list.get(0);
            payVo.setSubject(orderItemEntity.getSkuName()+" ：商品");
            payVo.setBody(orderItemEntity.getSkuAttrsVals());
        }


        return payVo;
    }

    /**
     * 根据用用户id查询所有订单详情
     * @param params
     * @return
     */
    @Override
    public PageUtils getOrderListByUserId(Map<String, Object> params) {
        MemberRespVo memberRespVo = UserLoginInterceptor.memberVo.get();
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>().eq("member_id",memberRespVo.getId()).orderByDesc("id")
        );
        List<OrderEntity> orderEntityList = page.getRecords().stream().peek(item ->
                item.setItemEntities(orderItemService.list(new QueryWrapper<OrderItemEntity>().eq("order_sn",item.getOrderSn())))).collect(Collectors.toList());
        page.setRecords(orderEntityList);
        return new PageUtils(page);
    }

    /**
     * 处理支付宝的成功回调
     * @param vo
     * @return
     */
    @Override
    public String handlePayResult(PayAsyncVo vo) {
        //获取支付信息
        PaymentInfoEntity paymentInfoEntity = new PaymentInfoEntity();
        paymentInfoEntity.setAlipayTradeNo(vo.getTrade_no());
        paymentInfoEntity.setOrderSn(vo.getOut_trade_no());
        paymentInfoEntity.setPaymentStatus(vo.getTrade_status());
        paymentInfoEntity.setCreateTime(new Date());

        //保存支付信息
        paymentInfoService.save(paymentInfoEntity);


        if ("TRADE_SUCCESS".equals(vo.getTrade_status())||"TRADE_FINISHED".equals(vo.getTrade_status())){
            //支付成功
            //修改订单状态
            updateOrderStatus(vo.getOut_trade_no(),OrderStatusEnum.PAYED);
        }


        return null;
    }

    /**
     * 修改订单状态
     */
    public void updateOrderStatus(String orderSn,OrderStatusEnum orderStatusEnum){
        baseMapper.updateOrderStatus(orderSn,orderStatusEnum.getCode());
    }

    /**
     * 保存订单和订单项方法
     * @param order
     */
    private void saveOrder(OrderCreateTo order) {
        OrderEntity orderEntity = order.getOrderEntity();
        MemberRespVo memberRespVo = UserLoginInterceptor.memberVo.get();
        orderEntity.setMemberId(memberRespVo.getId());
        //保存订单
        baseMapper.insert(orderEntity);
        //给订单项设置订单id
        List<OrderItemEntity> items = order.getItems();
        for (OrderItemEntity item : items) {
            item.setOrderId(orderEntity.getId());
        }
        //保存订单项
        orderItemService.saveBatch(items);
    }

    /**
     * 创建订单模型
     *
     * @return
     */
    private OrderCreateTo createOrder(OrderSubmitVo vo) {
        OrderCreateTo orderCreateTo = new OrderCreateTo();
        //创建订单
        OrderEntity orderEntity = buildOrderEntity(vo, orderCreateTo);
        orderCreateTo.setOrderEntity(orderEntity);
        //获取商品项
        List<OrderItemEntity> orderItemEntities = buildOrderItems(orderEntity.getOrderSn());
        orderCreateTo.setItems(orderItemEntities);

        // 计算总价
        BigDecimal totalPrice = getTotalPrice(orderItemEntities);
        orderEntity.setTotalAmount(totalPrice);
        orderEntity.setPayAmount(totalPrice.add(orderEntity.getFreightAmount()));
        //设置邮费和总价
        BigDecimal freightAmount = orderEntity.getFreightAmount();
        orderCreateTo.setFare(freightAmount);
        orderCreateTo.setPayPrice(totalPrice.add(freightAmount));


        //设置订单积分与成长值
        setBounds(orderEntity,orderItemEntities);

        return orderCreateTo;
    }

    /**
     * 设置订单积分与成长值
     * @param orderEntity
     * @param orderItemEntities
     */
    private void setBounds(OrderEntity orderEntity, List<OrderItemEntity> orderItemEntities) {
        Integer integration=0;
        Integer growth= 0;
        if (orderItemEntities!= null && orderItemEntities.size() > 0){
            for (OrderItemEntity item : orderItemEntities) {
                integration+=item.getGiftIntegration();
                growth+=item.getGiftGrowth();
            }
        }
        orderEntity.setGrowth(growth);
        orderEntity.setIntegration(integration);
    }

    /**
     * 计算总价
     * @param orderItemEntities
     * @return
     */
    private BigDecimal getTotalPrice(List<OrderItemEntity> orderItemEntities) {
        BigDecimal bigDecimal = new BigDecimal("0");
        if (orderItemEntities!= null && orderItemEntities.size() > 0){
            for (OrderItemEntity item : orderItemEntities) {
                BigDecimal temp = item.getSkuPrice().multiply(new BigDecimal(item.getSkuQuantity()));
                bigDecimal = bigDecimal.add(temp);
                item.setIntegrationAmount(temp);
            }
        }
        return bigDecimal;
    }

    /**
     * 构建订单商品列表
     *
     * @return
     */
    private List<OrderItemEntity> buildOrderItems(String sn) {
        List<OrderItemVo> items = cartFeignService.getItems();
        if (items != null && items.size() > 0) {
            List<Long> skuIds = items.stream().filter(OrderItemVo::getCheck).map(OrderItemVo::getSkuId).collect(Collectors.toList());
            //获取商品模块相关的全部信息
            R orderItemBySkuIds = productFeignService.getOrderItemBySkuIds(skuIds);
            //商品模块需要的全部信息(spu信息、sku信息)
            List<OrderItemProductRespVo> productRespVos = orderItemBySkuIds.getDataObjectByTypeJson(new TypeReference<List<OrderItemProductRespVo>>() {
            });


            //获取商品积分与成长值
            //获取所有spuid
            List<Long> spuIds = productRespVos.stream().map(OrderItemProductRespVo::getSpuId).collect(Collectors.toList());
            R r = couponFeignService.listByIds(spuIds);
            List<SpuBoundsVo> spuBoundsVos = r.getDataObjectByTypeJson(new TypeReference<List<SpuBoundsVo>>() {
            });
            //将积分成长值转为map<spuId,[growBounds,buyBounds]>
            Map<Long, BigDecimal[]> bounds = spuBoundsVos.stream().collect(Collectors.toMap(SpuBoundsVo::getSpuId, item -> new BigDecimal[]{item.getGrowBounds(), item.getBuyBounds()}));

            //获取每一项商品数量
            Map<Long, Integer> counts = items.stream().collect(Collectors.toMap(OrderItemVo::getSkuId, OrderItemVo::getCount));
            // 构建后返回
            List<OrderItemEntity> collect = productRespVos.stream().map(item -> buildOrderItem(item, bounds, sn, counts)).collect(Collectors.toList());
            return collect;

        }
        return null;
    }

    /**
     * 构建订单商品项
     *
     * @param item   商品模块数据
     * @param bounds 积分系统数据map<spuId,[growBounds,buyBounds]>
     * @param sn     订单sn
     * @param counts 商品数量对应<skuId,count>
     * @return 订单商品项
     */
    private OrderItemEntity buildOrderItem(OrderItemProductRespVo item, Map<Long, BigDecimal[]> bounds, String sn, Map<Long, Integer> counts) {
        OrderItemEntity orderItemEntity = new OrderItemEntity();
        //sn
        orderItemEntity.setOrderSn(sn);
        //商品模块数据
        BeanUtils.copyProperties(item,orderItemEntity);

        //商品数量
        orderItemEntity.setSkuQuantity(counts.get(orderItemEntity.getSkuId()));
        //成长值与积分
        orderItemEntity.setGiftGrowth(bounds.get(orderItemEntity.getSpuId())[0].intValue()*orderItemEntity.getSkuQuantity());
        orderItemEntity.setGiftIntegration(bounds.get(orderItemEntity.getSpuId())[1].intValue()*orderItemEntity.getSkuQuantity());
        return orderItemEntity;
    }

    /**
     * 构建订单
     *
     * @param vo
     * @param orderCreateTo
     * @return
     */
    private OrderEntity buildOrderEntity(OrderSubmitVo vo, OrderCreateTo orderCreateTo) {
        OrderEntity entity = new OrderEntity();

        //1、生成订单号
        String orderSn = IdWorker.getTimeId();
        entity.setOrderSn(orderSn);
        // 计算运费
        R fare = wareFeignService.getFare(vo.getAddrId());
        if (fare.getCode() == 0) {
            BigDecimal dataObjectByTypeJson = fare.getDataObjectByTypeJson(new TypeReference<BigDecimal>() {
            });
            orderCreateTo.setFare(dataObjectByTypeJson);
            entity.setFreightAmount(dataObjectByTypeJson);
        }
        // 收货人收货地址信息
        R addr = memberFeignService.getAddr(vo.getAddrId());
        if (addr.getCode() == 0) {
            MemberAddressVo address = addr.getDataObjectByTypeJson("memberReceiveAddress", new TypeReference<MemberAddressVo>() {
            });
            entity.setReceiverName(address.getName());
            entity.setReceiverPhone(address.getPhone());
            entity.setReceiverCity(address.getCity());
            entity.setReceiverProvince(address.getProvince());
            entity.setReceiverRegion(address.getRegion());
            entity.setReceiverDetailAddress(address.getDetailAddress());
            //创建时间
            entity.setCreateTime(new Date());
            //订单状态
            entity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
            //自动确认时间
            entity.setAutoConfirmDay(7);
            entity.setDeleteStatus(0);
        }
        return entity;
    }


    /**
     * @RabbitListener 监听消息队列
     * 监听队列接收消息
     * 1、被标注方法能够接收的参数为
     * args1：Message message: 原生消息（头+体）
     * args2：T t : 消息发送的消息时，消息的类型
     * args3：Channel channel: 连接通道
     * 2、一条消息只能被一个服务接收到
     * @RabbitHandler 只能标注在方法上，上面的Listener可以标注在类和方法上
     * Handler 标注在不同方法上表示重载不同消息类型
     */

    @RabbitHandler
    public void getMessage(Message message, OrderReturnReasonEntity entity, Channel channel) throws InterruptedException {
        Thread.sleep(3000);
        try {
            System.out.println("收到消息名：" + entity.getName());
            System.out.println("消息内容\n\n" + message + "\n\n");
            /**
             * 消息确认
             *  multiple 的 true 和 false 代表不同意思
             *      true 代表批量应答 channel 上未应答的消息
             *      比如说 channel 上有传送 tag 的消息 5,6,7,8 当前 tag 是 8 那么此时
             *      5-8 的这些还未应答的消息都会被确认收到消息应答false 同上面相比
             *      只会应答 tag=8 的消息 5,6,7 这三个消息依然不会被确认收到消息应答
             *  A.Channel.basicAck(用于肯定确认)
             *      RabbitMQ 已知道该消息并且成功的处理消息，可以将其丢弃了
             *  B.Channel.basicNack(用于否定确认)
             *  C.Channel.basicReject(用于否定确认)
             *      与 Channel.basicNack 相比少一个参数
             *      不处理该消息了直接拒绝，可以将其丢弃了
             */
            //肯定确认
//            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
            //否定确认
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}