package com.arccc.gulimall.cart.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * 购物项
 */
public class CartItem {
    private Long skuId;//商品id
    private Boolean check = true;//选中状态
    private String title;//标题
    private String img;//图片
    private List<String> skuSaleAttr;//销售属性
    private BigDecimal price;//单价
    private Integer count;//数量
    private BigDecimal totalPrice;//总价

    public Long getSkuId() {
        return skuId;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof CartItem && skuId.equals(((CartItem) obj).skuId);
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public Boolean getCheck() {
        return check;
    }

    public void setCheck(Boolean check) {
        this.check = check;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public List<String> getSkuSaleAttr() {
        return skuSaleAttr;
    }

    public void setSkuSaleAttr(List<String> skuSaleAttr) {
        this.skuSaleAttr = skuSaleAttr;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public BigDecimal getTotalPrice() {
        return price.multiply(new BigDecimal(this.count));
    }
}
