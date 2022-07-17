package com.arccc.gulimall.coupon.service.impl;

import com.arccc.common.to.MemberPrice;
import com.arccc.common.to.SkuReductionTo;
import com.arccc.common.utils.PageUtils;
import com.arccc.common.utils.Query;
import com.arccc.gulimall.coupon.dao.SkuFullReductionDao;
import com.arccc.gulimall.coupon.entity.MemberPriceEntity;
import com.arccc.gulimall.coupon.entity.SkuFullReductionEntity;
import com.arccc.gulimall.coupon.entity.SkuLadderEntity;
import com.arccc.gulimall.coupon.service.MemberPriceService;
import com.arccc.gulimall.coupon.service.SkuFullReductionService;
import com.arccc.gulimall.coupon.service.SkuLadderService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {

    @Autowired
    SkuLadderService skuLadderService;
    @Autowired
    MemberPriceService memberPriceService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuReduction(SkuReductionTo skuReductionTo) {
        // 保存`sms_sku_ladder` 满几件打几折信息
        if (skuReductionTo.getFullCount() > 0){
            SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
            BeanUtils.copyProperties(skuReductionTo,skuLadderEntity);
            skuLadderEntity.setAddOther(skuReductionTo.getCountStatus());
            skuLadderService.save(skuLadderEntity);
        }

        // 保存`sms_sku_full_reduction` 满多少减多少的优惠信息
        if (skuReductionTo.getFullPrice().compareTo(new BigDecimal(0))>0){
            SkuFullReductionEntity skuFullReductionEntity = new SkuFullReductionEntity();
            BeanUtils.copyProperties(skuReductionTo,skuFullReductionEntity);
            skuFullReductionEntity.setAddOther(skuReductionTo.getPriceStatus());
            save(skuFullReductionEntity);
        }


        // 保存会员价格信息
        List<MemberPrice> memberPrice = skuReductionTo.getMemberPrice();
        Long skuId = skuReductionTo.getSkuId();
        if (memberPrice != null && memberPrice.size() != 0){
            List<MemberPriceEntity> collect = memberPrice.stream()
                    .filter(item ->{
                        return item.getPrice().compareTo(new BigDecimal(0)) >0;
                    })
                    .map(item -> {
                MemberPriceEntity memberPriceEntity = new MemberPriceEntity();
                memberPriceEntity.setMemberLevelName(item.getName());
                memberPriceEntity.setMemberPrice(item.getPrice());
                memberPriceEntity.setSkuId(skuId);
                memberPriceEntity.setMemberLevelId(item.getId());
                memberPriceEntity.setAddOther(0);
                return memberPriceEntity;
            }).collect(Collectors.toList());
            memberPriceService.saveBatch(collect);
        }
    }
}