package com.arccc.gulimall.ware.service;

import com.arccc.common.utils.PageUtils;
import com.arccc.gulimall.ware.entity.WareInfoEntity;
import com.arccc.gulimall.ware.service.vo.WareResponseVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 仓库信息
 *
 * @author arccc
 * @email c23zxcvbnm@163.com
 * @date 2022-06-03 12:43:40
 */
public interface WareInfoService extends IService<WareInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<WareResponseVO> getAllWare();

    /**
     * 根据地址计算运费
     * @param addrId
     * @return
     */
    BigDecimal getFare(Long addrId);
}

