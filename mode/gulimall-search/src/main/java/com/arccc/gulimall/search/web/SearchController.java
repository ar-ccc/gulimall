package com.arccc.gulimall.search.web;

import com.arccc.gulimall.search.servie.MallSearchService;
import com.arccc.gulimall.search.vo.SearchResponse;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SearchController {


    @Autowired
    MallSearchService mallSearchService;



    @GetMapping(path = {"/search.html","list.html"})
    public String index(@RequestParam(value = "catalog3Id",required = false)Long catalog3Id,@RequestParam(value = "keyword",required = false)String keyword , Model model){

        SearchResponse result = mallSearchService.search(catalog3Id,keyword);
        model.addAttribute("result",result);
        if (StringUtils.isNotEmpty(keyword)){
            model.addAttribute("keyword",keyword);
        }
        return "list";
    }

    @GetMapping("/hello")
    @ResponseBody
    public String hello(){
        return "hello";
    }

}
