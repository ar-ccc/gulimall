package com.arccc.gulimall.order.web;

import com.arccc.gulimall.order.service.OrderService;
import com.arccc.gulimall.order.vo.OrderConfirmVo;
import com.arccc.gulimall.order.vo.OrderSubmitVo;
import com.arccc.gulimall.order.vo.SubmitRespVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.concurrent.ExecutionException;

@Controller
public class OrderWebController {
    @Autowired
    private OrderService orderService;

    /**
     *  订单确认页
     * @param model
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @GetMapping("/toTrade")
    public String toTrade(Model model) throws ExecutionException, InterruptedException {
        OrderConfirmVo orderConfirmVo = orderService.confirmOrder();
        model.addAttribute("orderConfirmData",orderConfirmVo);
        return "confirm";
    }

    /**
     *  下单
     * @param vo
     * @return
     */
    @PostMapping("orderSubmit")
    public String submit(OrderSubmitVo vo, Model model, RedirectAttributes redirectAttributes){
        System.out.println(vo);
        //下单

        SubmitRespVO submitRespVO = null;
        try {
            submitRespVO = orderService.placeAnOrder(vo);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("msg","下单失败，库存不足");
            return "redirect:http://order.gulimall.com/toTrade";
        }
        if (submitRespVO.getCode()==0){
            model.addAttribute("submitRespVo",submitRespVO);
//            redirectAttributes.addAttribute("submitRespVo",submitRespVO);
            //成功，去支付页
//            return "redirect:http://order.gulimall.com/pay.html";
            return "pay";
        }else {
            //失败：重新去订单确认页
            redirectAttributes.addFlashAttribute("msg","下单失败，页面超时");
            return "redirect:http://order.gulimall.com/toTrade";
        }


    }
}
