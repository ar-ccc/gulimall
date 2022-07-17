package com.arccc.gulimall.product.web;

import com.arccc.gulimall.product.service.SkuInfoService;
import com.arccc.gulimall.product.vo.SkuItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.concurrent.ExecutionException;

@Controller
public class ItemController {
    @Autowired
    SkuInfoService skuInfoService;

    @GetMapping("/{skuId}.html")
    public String item(@PathVariable("skuId")Long skuId, Model model) throws ExecutionException, InterruptedException {
        SkuItemVo vo = skuInfoService.item(skuId);
        model.addAttribute("item",vo);
        System.out.println(vo);
        return "item";
    }
}
