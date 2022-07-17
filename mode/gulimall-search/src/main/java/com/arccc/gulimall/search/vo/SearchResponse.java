package com.arccc.gulimall.search.vo;

import com.arccc.gulimall.search.entity.BrandEntity;
import com.arccc.gulimall.search.entity.SkuInfoEntity;
import lombok.Data;

import java.util.List;

@Data
public class SearchResponse {
    // 查询到的所有商品信息
    private List<SkuInfoEntity> products;

    // 分页信息
    private Integer pageNum;//当前页码
    private Long total;//总记录数
    private Integer totalPages;//总页码


    private List<BrandEntity> brands;//查询结果涉及到的品牌
    private List<BrandVo> attrs;//查询结果涉及到的属性
    private List<CatelogVo> catelogs;//查询结果涉及到的分类


    @Data
    public static class BrandVo {
        private Long brandId;
        private String brandName;
        private String brandImg;
    }

    @Data
    public static class AttrVo {
        private Long attrId;
        private String attrName;
        private List<String> attrValue;
    }

    @Data
    public static class CatelogVo{
        private Long catelogId;
        private String catelogName;
    }


}
