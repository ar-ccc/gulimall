package com.arccc.common.exception;

/**
 * 没有库存
 */
public class NoStockException extends RuntimeException {
    public NoStockException(String str){
        super(str+"没有库存");
    }
}
