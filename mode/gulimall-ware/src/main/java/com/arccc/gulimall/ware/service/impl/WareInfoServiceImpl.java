package com.arccc.gulimall.ware.service.impl;

import com.arccc.common.utils.PageUtils;
import com.arccc.common.utils.Query;
import com.arccc.gulimall.ware.dao.WareInfoDao;
import com.arccc.gulimall.ware.entity.WareInfoEntity;
import com.arccc.gulimall.ware.service.WareInfoService;
import com.arccc.gulimall.ware.service.vo.WareResponseVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareInfoEntity> wrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if (StringUtils.isNotEmpty(key)){
            wrapper.and(obj ->{
                obj.eq("id",key)
                        .or().like("name",key)
                        .or().like("address",key)
                        .or().like("areacode",key);
            });
        }
        IPage<WareInfoEntity> page = this.page(
                new Query<WareInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public List<WareResponseVO> getAllWare() {
        List<WareInfoEntity> wareInfoEntities = baseMapper.selectList(new QueryWrapper<>());
        List<WareResponseVO> collect = wareInfoEntities.stream().map(item -> {
            WareResponseVO wareResponseVO = new WareResponseVO();
            BeanUtils.copyProperties(item, wareResponseVO);
            return wareResponseVO;
        }).collect(Collectors.toList());
        return collect;
    }

    @Override
    public BigDecimal getFare(Long addrId) {
        if (addrId%2==0){
            return new BigDecimal("12.00");
        }else {
            return new BigDecimal("11.00");
        }
    }

}