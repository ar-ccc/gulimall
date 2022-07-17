package com.arccc.gulimall.cart.service.impl;

import com.alibaba.nacos.common.utils.JacksonUtils;
import com.alibaba.nacos.common.utils.StringUtils;
import com.arccc.common.utils.R;
import com.arccc.gulimall.cart.feign.ProductFeignService;
import com.arccc.gulimall.cart.interceptor.CartInterceptor;
import com.arccc.gulimall.cart.service.CartService;
import com.arccc.gulimall.cart.vo.Cart;
import com.arccc.gulimall.cart.vo.CartItem;
import com.arccc.gulimall.cart.vo.SkuInfoVo;
import com.arccc.gulimall.cart.vo.UserInfoVo;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CartServiceImpl implements CartService {
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    ProductFeignService productFeignService;
    @Autowired
    ThreadPoolExecutor executor;
    private final String CART_PREFIX = "gulimall:cart:";

    /**
     * 给购物车添加数据
     *
     * @param skuId
     * @param num
     * @return
     */
    @Override
    public CartItem addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();

        String o = (String) cartOps.get(skuId.toString());
        CartItem cartItem;
        // 购物车内没有该商品
        if (StringUtils.isEmpty(o)) {
            cartItem = new CartItem();
            // 1、远程查询商品信息
            CompletableFuture<Void> r1 = CompletableFuture.runAsync(() -> {
                R r = productFeignService.getSkuInfo(skuId);
                SkuInfoVo skuInfo = r.getDataObjectByTypeJson("skuInfo", new TypeReference<SkuInfoVo>() {
                });
                // 2、商品添加购物车

                cartItem.setCheck(true);
                cartItem.setImg(skuInfo.getSkuDefaultImg());
                cartItem.setCount(num);
                cartItem.setPrice(skuInfo.getPrice());
                cartItem.setSkuId(skuId);
                cartItem.setTitle(skuInfo.getSkuTitle());
            }, executor);
            // 3、sku组合信息
            CompletableFuture<Void> r2 = CompletableFuture.runAsync(() -> {
                List<String> skuSale = productFeignService.getSkuSale(skuId);
                cartItem.setSkuSaleAttr(skuSale);
            }, executor);
            //等待执行完成
            CompletableFuture.allOf(r1, r2).get();
            String s = JacksonUtils.toJson(cartItem);
            cartOps.put(skuId.toString(), s);
        } else {
            //购物车有该商品
            cartItem = JacksonUtils.toObj(o, new TypeReference<CartItem>() {
            });
            cartItem.setCount(cartItem.getCount() + num);
            cartOps.put(skuId.toString(), JacksonUtils.toJson(cartItem));
        }

        return cartItem;

    }

    /**
     * 获取购物车中的某个购物项
     *
     * @param skuId
     * @return
     */
    @Override
    public CartItem getCartItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        Object o = cartOps.get(skuId.toString());
        if (o instanceof String) {
            return JacksonUtils.toObj((String) o, new TypeReference<CartItem>() {
            });
        }
        return null;
    }

    /**
     * 查询合并购物车
     *
     * @return
     */
    @Override
    public Cart getCart() throws ExecutionException, InterruptedException {
        Cart cart = new Cart();
//        cart.setItems(new ArrayList<>());
        UserInfoVo userInfoVo = CartInterceptor.userInfoVoThreadLocal.get();
        //临时购物车
        String tempCartKey = CART_PREFIX + userInfoVo.getUserKey();
        List<CartItem> allCartItem = getAllCartItem(tempCartKey);
        // 1、查询是否登录
        if (userInfoVo.getUserId() != null) {//登录了
            //2、合并临时购物车
            if (allCartItem!=null){
                for (CartItem item : allCartItem) {
                    addToCart(item.getSkuId(),item.getCount());
                }
                //清空购物车
                clearCartKey(tempCartKey);
            }
            //3、获取用户购物车(包含合并后的临时购物车数据)
            List<CartItem> allCartItem1 = getAllCartItem(CART_PREFIX + userInfoVo.getUserId());
            cart.setItems(allCartItem1);
        } else {//未登录
            //6、添加临时购物车
            cart.setItems(allCartItem);
        }

        return cart;
    }

    /**
     * 清空购物车
     * @param key
     */
    @Override
    public void clearCartKey(String key) {
        stringRedisTemplate.delete(key);
    }

    /**
     * 勾选购物项
     * @param skuId
     * @param check
     */
    @Override
    public void checkItem(Long skuId, Integer check) {
        CartItem cartItem = getCartItem(skuId);
        cartItem.setCheck(check==1);
        String s = JacksonUtils.toJson(cartItem);
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.put(skuId.toString(),s);
    }

    /**
     * 修改商品数量
     * @param skuId
     * @param num
     */
    @Override
    public void updateCount(Long skuId, Integer num) {
        CartItem cartItem = getCartItem(skuId);
        cartItem.setCount(num);
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.put(skuId.toString(),JacksonUtils.toJson(cartItem));
    }

    /**
     * 删除商品
     * @param skuId
     */
    @Override
    public void deleteItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.delete(skuId.toString());
    }

    /**
     * 获取所有选中的购物项
     * @return
     */
    @Override
    public List<CartItem> getUserCartItems() throws ExecutionException, InterruptedException {
        UserInfoVo userInfoVo = CartInterceptor.userInfoVoThreadLocal.get();
        if (userInfoVo.getUserId()==null){
            return null;
        }else {
            //返回并更新最新价格
            List<CartItem> collect = getCart().getItems().stream().filter(CartItem::getCheck).collect(Collectors.toList());

            List<Long> ids = collect.stream().map(CartItem::getSkuId).collect(Collectors.toList());
            Map<Long, BigDecimal> price = productFeignService.getPrice(ids);
            return collect.stream().peek(item -> item.setPrice(price.get(item.getSkuId()))).collect(Collectors.toList());
        }
    }

    private List<CartItem> getAllCartItem(String key) {
        BoundHashOperations<String, Object, Object> hashOps = stringRedisTemplate.boundHashOps(key);
        if (hashOps.size() > 0L) {
            List<Object> values = hashOps.values();
            if (values != null && values.size() > 0) {
                return values.stream().map(obj -> JacksonUtils.toObj((String) obj, new TypeReference<CartItem>() {
                })).collect(Collectors.toList());
            }
        }
        return null;
    }

    /**
     * 获取到要操作的购物车
     *
     * @return
     */
    private BoundHashOperations<String, Object, Object> getCartOps() {
        UserInfoVo userInfoVo = CartInterceptor.userInfoVoThreadLocal.get();
        //存储key以cart开头，未登录则cart:user-Kye,登录了则:cart:UserId
        String key;
        if (userInfoVo.getUserId() != null) {
            key = CART_PREFIX + userInfoVo.getUserId();
        } else {
            key = CART_PREFIX + userInfoVo.getUserKey();
        }
        return stringRedisTemplate.boundHashOps(key);
    }
}
