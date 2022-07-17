package com.arccc.gulimall.ware.service.vo;

import lombok.Data;

import java.util.List;

@Data
public class MergeVo {
    private List<Long> items;
    private Long purchaseId;
}
