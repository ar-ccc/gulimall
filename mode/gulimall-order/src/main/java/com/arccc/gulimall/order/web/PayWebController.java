package com.arccc.gulimall.order.web;

import com.alipay.api.AlipayApiException;
import com.arccc.gulimall.order.config.AlipayTemplate;
import com.arccc.gulimall.order.service.OrderService;
import com.arccc.gulimall.order.vo.PayVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PayWebController {

    @Autowired
    OrderService orderService;
    @Autowired
    AlipayTemplate alipayTemplate;
    @GetMapping(value = "/payOrder",produces = "text/html")
    @ResponseBody
    public String payOrder(@RequestParam("orderSn") String orderSn) throws AlipayApiException {


        PayVo payVo = orderService.getPayVoByOrderSn(orderSn);
        String pay = alipayTemplate.pay(payVo);
        System.out.println(pay);

        return pay;
    }
}
