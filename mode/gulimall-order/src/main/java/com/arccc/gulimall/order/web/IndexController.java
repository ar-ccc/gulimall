package com.arccc.gulimall.order.web;

import com.arccc.common.utils.PageUtils;
import com.arccc.gulimall.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
public class IndexController {
    @Autowired
    OrderService orderService;
    @GetMapping("/{url}.html")
    public String confirm(@PathVariable("url")String path, Model model, @RequestParam Map<String, Object> params){
        if ("list".equals(path)){
            PageUtils page = orderService.getOrderListByUserId(params);
            model.addAttribute("orders",page);
            return "list";
        }
        return path;
    }

}
