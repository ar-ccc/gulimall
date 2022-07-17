package com.arccc.gulimall.cart.service;

import com.arccc.gulimall.cart.vo.Cart;
import com.arccc.gulimall.cart.vo.CartItem;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface CartService {
    CartItem addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException;

    CartItem getCartItem(Long skuId);

    Cart getCart() throws ExecutionException, InterruptedException;
    void clearCartKey(String key);

    void checkItem(Long skuId, Integer check);

    void updateCount(Long skuId, Integer num);

    void deleteItem(Long skuId);

    /**
     * 获取购物车所有选中的购物项
     * @return
     */
    List<CartItem> getUserCartItems() throws ExecutionException, InterruptedException;
}
