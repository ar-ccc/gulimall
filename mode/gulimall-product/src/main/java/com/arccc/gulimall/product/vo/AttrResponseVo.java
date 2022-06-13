package com.arccc.gulimall.product.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * attr响应的实体类
 * 需要所属分组和所属分类的名字
 */
@Data
public class AttrResponseVo extends AttrVo {
    /**
     * catelogName 分类名
     * groupName 分组名
     */
    private String catelogName;
    private String groupName;

    private Long[] catelogPath;

}
