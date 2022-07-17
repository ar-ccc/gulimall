package com.arccc.gulimall.ware.service.impl;

import com.arccc.common.utils.PageUtils;
import com.arccc.common.utils.Query;
import com.arccc.gulimall.ware.dao.PurchaseDetailDao;
import com.arccc.gulimall.ware.entity.PurchaseDetailEntity;
import com.arccc.gulimall.ware.service.PurchaseDetailService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service("purchaseDetailService")
public class PurchaseDetailServiceImpl extends ServiceImpl<PurchaseDetailDao, PurchaseDetailEntity> implements PurchaseDetailService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<PurchaseDetailEntity> wrapper = new QueryWrapper<>();
        /**
         *    key: '华为',//检索关键字
         *    status: 0,//状态
         *    wareId: 1,//仓库id
         */
        String key = (String) params.get("key");
        String status = (String) params.get("status");
        String wareId = (String) params.get("wareId");
        if (StringUtils.isNotEmpty(key)){
            wrapper.and(obj -> {
                obj.eq("purchase_id",key).or().eq("sku_id",key);
            });
        }
        if (StringUtils.isNotEmpty(status)){
            wrapper.eq("status",status);
        }
        if (StringUtils.isNotEmpty(wareId)){
            wrapper.eq("ware_id",wareId);
        }


        IPage<PurchaseDetailEntity> page = this.page(
                new Query<PurchaseDetailEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public List<PurchaseDetailEntity> getByPurchaseId(Long id) {
        QueryWrapper<PurchaseDetailEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("purchase_id",id);

        return baseMapper.selectList(wrapper);
    }

}