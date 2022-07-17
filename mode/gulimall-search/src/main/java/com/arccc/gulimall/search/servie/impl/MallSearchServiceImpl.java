package com.arccc.gulimall.search.servie.impl;

import com.arccc.gulimall.search.entity.SkuInfoEntity;
import com.arccc.gulimall.search.feign.ProductFeignService;
import com.arccc.gulimall.search.servie.MallSearchService;
import com.arccc.gulimall.search.vo.SearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MallSearchServiceImpl implements MallSearchService {
    @Autowired
    ProductFeignService productFeignService;
    @Override
    public SearchResponse search(Long catelog3Id,String keyword ) {
        List<SkuInfoEntity> skuByCatelogIdAndKeyword = productFeignService.getSkuByCatelogIdAndKeyword(catelog3Id,keyword);
        SearchResponse searchResponse = new SearchResponse();
        searchResponse.setProducts(skuByCatelogIdAndKeyword);
        return searchResponse;
    }
}
