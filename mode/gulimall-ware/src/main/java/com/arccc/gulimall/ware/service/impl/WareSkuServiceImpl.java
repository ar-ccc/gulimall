package com.arccc.gulimall.ware.service.impl;

import com.arccc.common.utils.PageUtils;
import com.arccc.common.utils.Query;
import com.arccc.common.utils.R;
import com.arccc.common.vo.mq.OrderTo;
import com.arccc.common.vo.mq.WareOrderTaskTo;
import com.arccc.gulimall.ware.dao.WareSkuDao;
import com.arccc.gulimall.ware.entity.WareOrderTaskDetailEntity;
import com.arccc.gulimall.ware.entity.WareOrderTaskEntity;
import com.arccc.gulimall.ware.entity.WareSkuEntity;
import com.arccc.gulimall.ware.feign.OrderFeignSerivce;
import com.arccc.gulimall.ware.feign.ProductFeignService;
import com.arccc.gulimall.ware.service.WareOrderTaskDetailService;
import com.arccc.gulimall.ware.service.WareOrderTaskService;
import com.arccc.gulimall.ware.service.WareSkuService;
import com.arccc.gulimall.ware.vo.OrderItemVo;
import com.arccc.gulimall.ware.vo.OrderRespVo;
import com.arccc.gulimall.ware.vo.SkuInfoVo;
import com.arccc.gulimall.ware.vo.WareSkuLockVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {
    @Autowired
    ProductFeignService productFeignService;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    WareOrderTaskService wareOrderTaskService;
    @Autowired
    WareOrderTaskDetailService wareOrderTaskDetailService;

    @Autowired
    OrderFeignSerivce orderFeignSerivce;

    @Autowired
    WareSkuDao wareSkuDao;

    /**
     * 解锁库存服务
     */
    public void unLock(WareOrderTaskTo wareOrderTaskTo) {
        /*
        判断是否有工作详情单
            有：根据详情单查询到订单号，并查询订单
                有订单：查询订单状态
                    已支付：无需解锁
                    已取消：解锁库存
                无订单：解锁库存
            无：无需解锁，已经回滚
         */
        Long taskDetailId = wareOrderTaskTo.getTaskDetailId();
        WareOrderTaskDetailEntity entity = wareOrderTaskDetailService.getById(taskDetailId);
        //是否有工作详情单,并且状态为锁定状态
        if (entity != null && entity.getLockStatus() == 1){
            //根据订单号查询订单详情
            WareOrderTaskEntity taskEntity = wareOrderTaskService.getById(entity.getTaskId());
            String orderSn = taskEntity.getOrderSn();
            R order = orderFeignSerivce.getOrderStatus(orderSn);
            //远程是否调用成功
            if (order.getCode()==0){
                OrderRespVo vo = order.getDataObjectByTypeJson(new TypeReference<OrderRespVo>() {
                });
                //订单不存在，或者订单已取消
                if (vo==null||vo.getStatus()==4){
                    //解锁库存
                    unLockStock(entity);
                }
            }else {
                //远程调用失败，抛出异常让消息重新入队列
                throw new RuntimeException("远程调用失败");
            }
        }

    }

    @Override
    public void unLock(OrderTo order) {
        //通过订单号查询库存工作单，并获得未解锁的工作详情单
        String orderSn = order.getOrderSn();
        WareOrderTaskEntity one = wareOrderTaskService.getOne(new QueryWrapper<WareOrderTaskEntity>().eq("order_sn", order));
        if (one != null){
            //获取工作详情单
            List<WareOrderTaskDetailEntity> list = wareOrderTaskDetailService.list(new QueryWrapper<WareOrderTaskDetailEntity>()
                    .eq("task_id", one.getId())
                    .eq("lock_status",1));
            //解锁全部工作单
            for (WareOrderTaskDetailEntity l : list) {
                unLockStock(l);
            }
        }

    }

    /**
     * 解锁库存
     */
    @Transactional
    public void unLockStock(WareOrderTaskDetailEntity entity){
        //解锁库存
        wareSkuDao.unLock(entity.getSkuId(),entity.getWareId(),entity.getSkuNum());
        //修改详情单
        entity.setLockStatus(2);
        wareOrderTaskDetailService.updateById(entity);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareSkuEntity> wrapper = new QueryWrapper<>();
        /*
          wareId:
          skuId:
         */
        String wareId = (String) params.get("wareId");
        String skuId = (String) params.get("skuId");
        if (StringUtils.isNotEmpty(wareId)){
            wrapper.eq("ware_id",wareId);
        }
        if (StringUtils.isNotEmpty(skuId)){
            wrapper.eq("sku_id",skuId);
        }

        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    /**
     * 新增库存
     */
    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        // 查询该仓库的sku是否有库存
        List<WareSkuEntity> wareSkuEntities = baseMapper.selectList(new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId).eq("ware_id", wareId));
        if (wareSkuEntities != null && wareSkuEntities.size()!= 0){
            baseMapper.addStock(skuId, wareId,skuNum);
        }else {
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setSkuId(skuId);
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setStock(skuNum);
            wareSkuEntity.setStockLocked(0);
            // 获取skuname
            try {
                R info = productFeignService.info(36L);
                Integer code = (Integer) info.get("code");
                if (0==code){
                    SkuInfoVo skuInfo = info.getDataObjectByTypeJson(new TypeReference<SkuInfoVo>() {
                    });
                    String  skuName = skuInfo.getSkuName();
                    wareSkuEntity.setSkuName(skuName);
                }
            }catch (Exception ignored){

            }
            baseMapper.insert(wareSkuEntity);

        }
    }

    @Override
    public Map<Long, Boolean> hasStocks(List<Long> skuIds) {
        if (skuIds ==null || skuIds.size()==0){
            return null;
        }
        return skuIds.stream().collect(Collectors.toMap(k -> k, v -> {
            WareSkuEntity sku_id = baseMapper.selectOne(new QueryWrapper<WareSkuEntity>().eq("sku_id", v));
            if (sku_id!=null){
                return sku_id.getStock()>sku_id.getStockLocked();
            }
            return false;
        }));
    }

    /**
     * 为订单锁定库存
     * @param vo
     * @return
     */
    @Transactional
    @Override
    public Boolean lock(WareSkuLockVo vo) {
        //订单sn
        String orderSn = vo.getOrderSn();
        //需要锁定的数据
        List<OrderItemVo> locks = vo.getLocks();

        //保存库存工作单
        WareOrderTaskEntity wareOrderTaskEntity = new WareOrderTaskEntity();
        wareOrderTaskEntity.setOrderSn(orderSn);
        wareOrderTaskService.save(wareOrderTaskEntity);

        for (OrderItemVo lock : locks) {
            List<WareSkuEntity> wareSkuEntities = baseMapper.selectList(new QueryWrapper<WareSkuEntity>().eq("sku_id", lock.getSkuId()).and(obj -> {
                obj.apply("stock - stock_locked >  0");
            }));
            if (wareSkuEntities==null || wareSkuEntities.size()==0) {
                throw new RuntimeException(lock.getSkuId()+"库存不足");
            }
            Long in = 0L;
            for (WareSkuEntity wareSkuEntity : wareSkuEntities) {
                in =  baseMapper.lock(lock.getSkuId(),wareSkuEntity.getWareId(),lock.getCount());
                if (in==1){
                    //TODO 库存锁定成功，告诉MQ
                    WareOrderTaskDetailEntity wareOrderTaskDetailEntity = new WareOrderTaskDetailEntity(null,lock.getSkuId(),lock.getTitle(),lock.getCount(),wareOrderTaskEntity.getId(),wareSkuEntity.getWareId(),1);
                    wareOrderTaskDetailService.save(wareOrderTaskDetailEntity);
                    WareOrderTaskTo wareOrderTaskTo = new WareOrderTaskTo();
                    wareOrderTaskTo.setTaskId(wareOrderTaskEntity.getId());
                    wareOrderTaskTo.setTaskDetailId(wareOrderTaskDetailEntity.getId());
                    rabbitTemplate.convertAndSend("stock-event-exchange","stock.locked",wareOrderTaskTo,new CorrelationData(UUID.randomUUID().toString()));
                    break;
                }
            }
            if (in==0){
                return false;
            }
        }
        return true;
    }


}