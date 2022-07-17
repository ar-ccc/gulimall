package com.arccc.gulimall.cart.controller;

import com.arccc.gulimall.cart.interceptor.CartInterceptor;
import com.arccc.gulimall.cart.service.CartService;
import com.arccc.gulimall.cart.vo.Cart;
import com.arccc.gulimall.cart.vo.CartItem;
import com.arccc.gulimall.cart.vo.UserInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Controller
public class CartController {
    @Autowired
    CartService cartService;


    @GetMapping("/cart.html")
    public String cartListPage(Model model) throws ExecutionException, InterruptedException {
        UserInfoVo userInfoVo = CartInterceptor.userInfoVoThreadLocal.get();

        Cart cart = cartService.getCart();
        model.addAttribute("cart",cart);
        return "cartList";
    }
    @GetMapping("/addToCart")
    public String addToCart(@RequestParam("skuId") Long skuId, @RequestParam("num") Integer num, RedirectAttributes redirectAttributes) throws ExecutionException, InterruptedException {
        CartItem cartItem = cartService.addToCart(skuId,num);
        redirectAttributes.addAttribute("skuId",skuId);
        return "redirect:http://cart.gulimall.com/success.html";
    }
    @GetMapping("/deleteItem")
    public String deleteItem(@RequestParam("skuId") Long skuId) {
        cartService.deleteItem(skuId);
        return "redirect:http://cart.gulimall.com/cart.html";
    }

    /**
     * 跳转到成功页
     * @param skuId
     * @param model
     * @return
     */
    @GetMapping("/success.html")
    public String addToCartSuccess(@RequestParam("skuId")Long skuId,Model model){
        CartItem cartItem =  cartService.getCartItem(skuId);
        model.addAttribute("cartItem",cartItem);
        return "success";
    }

    /**
     * 修改商品选中状态
     * @param skuId 商品id
     * @param check 选中状态：0=false，1=true
     * @return
     */
//    @GetMapping("/checkItem")
//    public String checkItem(@RequestParam("skuId") Long skuId,@RequestParam("check") Integer check){
//        cartService.checkItem(skuId,check);
//        return "redirect:http://cart.gulimall.com/cart.html";
//    }
    @GetMapping("/checkItem")
    @ResponseBody
    public String checkItem(@RequestParam("skuId") Long skuId,@RequestParam("check") Integer check){
        cartService.checkItem(skuId,check);
        return "ok";
    }
    @GetMapping("/updateCount")
    public String updateCount(@RequestParam("skuId") Long skuId,@RequestParam("num") Integer num){
        cartService.updateCount(skuId,num);
        return "redirect:http://cart.gulimall.com/cart.html";
    }

    @GetMapping("/currentUserCartItems")
    @ResponseBody
    public List<CartItem> getItems() throws ExecutionException, InterruptedException {
        return cartService.getUserCartItems();
    }
}
