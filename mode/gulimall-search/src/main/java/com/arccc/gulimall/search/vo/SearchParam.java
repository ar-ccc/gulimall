package com.arccc.gulimall.search.vo;

import lombok.Data;

import java.util.List;

/**
 * 封装页面所有的查询条件
 */
@Data
public class SearchParam {
    private String keywork;//关键字
    private Long catalog3Id;//三级分类id
    /**
     * sort=saleCount_asc/desc
     * sort=skuPrice_asc/desc
     * sort=hotScore_asc/desc
     */
    private String sort;//排序条件
    /**
     * 过滤：hasStock、skuPrice区间、brandId、catalog3Id、attrs
     * hasStock=0/1是否有货
     * skuPrice=1_500/_500/500_
     *
     */
    private Integer hasStock;//是否有货
    private String skuPrice;//价格区间
    private List<Long> brandId;//品牌id，可以多选
    private List<String> attrs;//按照属性筛选
    private Integer pageNumber;//页码

}
