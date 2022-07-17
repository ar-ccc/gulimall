package com.arccc.gulimall.ware.service.impl;

import com.arccc.common.constant.WareConstant;
import com.arccc.common.utils.PageUtils;
import com.arccc.common.utils.Query;
import com.arccc.gulimall.ware.dao.PurchaseDao;
import com.arccc.gulimall.ware.entity.PurchaseDetailEntity;
import com.arccc.gulimall.ware.entity.PurchaseEntity;
import com.arccc.gulimall.ware.service.PurchaseDetailService;
import com.arccc.gulimall.ware.service.PurchaseService;
import com.arccc.gulimall.ware.service.WareSkuService;
import com.arccc.gulimall.ware.service.vo.MergeVo;
import com.arccc.gulimall.ware.service.vo.PurchaseDoneItemVo;
import com.arccc.gulimall.ware.service.vo.PurchaseDoneVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    @Autowired
    PurchaseDetailService purchaseDetailService;
    @Autowired
    WareSkuService wareSkuService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageUnreceive(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>().eq("status",0).or().eq("status",1)
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void mergePurchase(MergeVo mergeVo) {
        Long purchaseId = mergeVo.getPurchaseId();
        if (purchaseId == null){
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setStatus(WareConstant.PruchaseStatusEnum.CREATED.getCode());
            purchaseEntity.setCreateTime(new Date());
            purchaseEntity.setUpdateTime(new Date());
            baseMapper.insert(purchaseEntity);
            purchaseId = purchaseEntity.getId();

        }
        PurchaseEntity purchaseEntity1 = baseMapper.selectById(purchaseId);
        if (purchaseEntity1==null || purchaseEntity1.getStatus() > WareConstant.PruchaseDetailStatusEnum.ASSIGNED.getCode()){
            throw new RuntimeException("采购单已经无法分配了");
        }
        List<Long> items = mergeVo.getItems();
        if (items != null && items.size() != 0){
            Long finalPurchaseId = purchaseId;
            List<PurchaseDetailEntity> collect = items.stream().map(item -> {
                PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
                purchaseDetailEntity.setId(item);
                purchaseDetailEntity.setPurchaseId(finalPurchaseId);
                purchaseDetailEntity.setStatus(WareConstant.PruchaseDetailStatusEnum.ASSIGNED.getCode());

                return purchaseDetailEntity;
            }).collect(Collectors.toList());
            purchaseDetailService.updateBatchById(collect);

            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setId(purchaseId);
            purchaseEntity.setUpdateTime(new Date());
            this.updateById(purchaseEntity);
        }


    }

    @Transactional
    @Override
    public void received(List<Long> ids) {
        //1、确认当前采购单的状态
        List<PurchaseEntity> collect = ids.stream().map(id -> {
            PurchaseEntity byId = baseMapper.selectById(id);
            return byId;
        }).filter(item -> {
            return item.getStatus() < WareConstant.PruchaseStatusEnum.RECEIVE.getCode();
        }).map(item -> {
            item.setStatus(WareConstant.PruchaseStatusEnum.RECEIVE.getCode());
            item.setUpdateTime(new Date());
            return item;
        }).collect(Collectors.toList());

        //2、改变采购单的状态
        this.updateBatchById(collect);

        //3、改变采购项的状态
        collect.forEach(item -> {
            List<PurchaseDetailEntity> details = purchaseDetailService.getByPurchaseId(item.getId());
            List<PurchaseDetailEntity> collect1 = details.stream().map(entity -> {
                PurchaseDetailEntity detail = new PurchaseDetailEntity();
                detail.setId(entity.getId());
                detail.setStatus(WareConstant.PruchaseDetailStatusEnum.BUYING.getCode());
                return detail;
            }).collect(Collectors.toList());
            purchaseDetailService.updateBatchById(collect1);
        });
    }

    @Transactional
    @Override
    public void done(PurchaseDoneVo purchaseDoneVo) {
        //1、获取基本信息
        // 1.1、 得到purchaseID
        Long purchaseId = purchaseDoneVo.getId();

        // 1.2、 获取提交的采购需求状态
        List<PurchaseDoneItemVo> items = purchaseDoneVo.getItems();
        // 用于判断采购需求是否有失败的状态
        boolean blag = true;
        //2、修改采购需求状态
        // 2.1、 创建采购需求修改状态
        List<PurchaseDetailEntity> updates = new ArrayList<>();
        for (PurchaseDoneItemVo item : items) {
            PurchaseDetailEntity detail = new PurchaseDetailEntity();
            // 采购失败的情况
            if (item.getStatus() == WareConstant.PruchaseDetailStatusEnum.HASERROR.getCode()){
                //如果失败就需要将采购单状态改为有异常
                blag = false;
            }else {
                //4、采购完成入库
                PurchaseDetailEntity entity = purchaseDetailService.getById(item.getItemId());

                wareSkuService.addStock(entity.getSkuId(),entity.getWareId(),entity.getSkuNum());
            }
            // 修改要保存的状态
            detail.setStatus(item.getStatus());
            detail.setId(item.getItemId());
            detail.setPurchaseId(purchaseId);
            updates.add(detail);
        }
        // 2.2修改采购项状态
        purchaseDetailService.updateBatchById(updates);

        //3、修改采购单状态
        //3.1、创建采购单
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setUpdateTime(new Date());
        //3.2、修改状态
        purchaseEntity.setId(purchaseId);
        purchaseEntity.setStatus(blag?WareConstant.PruchaseStatusEnum.FINISH.getCode()
                                      :WareConstant.PruchaseStatusEnum.HASERROR.getCode());
        //3.3、完成修改
        baseMapper.updateById(purchaseEntity);


    }

}