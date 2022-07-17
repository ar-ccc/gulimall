package com.arccc.gulimall.cart.vo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 整个购物车
 */
public class Cart {
    List<CartItem> items = new ArrayList<>();
    private Integer countNum;//商品总数量
    private Integer countType;//商品类型数量
    private BigDecimal totalAmount;//商品总价
    private BigDecimal reduce=new BigDecimal("0");//商品优惠

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }
//    public void addItem(List<CartItem> newItems){
//        if (newItems==null || newItems.size()==0) {
//            return;
//        }
//        if (this.items.size()==0){
//            items=newItems;
//            return;
//        }
//
//        for (CartItem item : this.items) {
//
//        }
//
//
//    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    public Integer getCountNum() {
        countNum=0;
        if (items!=null && items.size()>0) {

            for (CartItem item : items) {
                countNum+=item.getCount();
            }
        }
        return countNum;
    }

    public Integer getCountType() {
        countType=items.size();
        return countType;
    }


    /**
     * 总价
     * @return 总价格
     */
    public BigDecimal getTotalAmount() {
        totalAmount=new BigDecimal("0");
        if (items!=null && items.size()>0) {
            for (CartItem item : items) {
                if (item.getCheck()){
                    totalAmount = totalAmount.add(item.getTotalPrice());
                }
            }
        }
        totalAmount = totalAmount.subtract(reduce);
        return totalAmount;
    }

    public BigDecimal getReduce() {
        return reduce;
    }

    public void setReduce(BigDecimal reduce) {
        this.reduce = reduce;
    }
}
