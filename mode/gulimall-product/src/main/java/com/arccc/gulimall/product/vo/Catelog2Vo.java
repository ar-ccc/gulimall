package com.arccc.gulimall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 二级菜单vo
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
public class Catelog2Vo {
    private String catalog1Id;//1级父节点id
    private List<Catelog2Vo.Catelog3Vo> catalog3List;//3级子节点集合
    private String id;// 当前节点id
    private String name; //当前节点名称

    /**
     * 三级菜单vo
     */
    @AllArgsConstructor
    @Data
    @NoArgsConstructor
    public static class Catelog3Vo{
        private String catalog2Id;//2级父节点id
        private String id; // 当前三级节点id
        private String name;
    }
}
