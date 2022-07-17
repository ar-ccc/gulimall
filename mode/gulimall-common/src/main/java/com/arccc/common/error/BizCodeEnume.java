package com.arccc.common.error;

/***
 * 错误码和错误信息定义类
 * 1. 错误码定义规则为 5 为数字
 * 2. 前两位表示业务场景，最后三位表示错误码。例如：100001。10:通用 001:系统未知
 异常
 * 3. 维护错误码后需要维护错误描述，将他们定义为枚举形式
 *      错误码列表：
 *          10: 通用
 *              001：参数格式校验
 *              002：发送验证码频率过高
 *          11: 商品
 *          12: 订单
 *          13: 购物车
 *          14: 物流
 *          15: 用户
 *              001：用户已经存在
 *              002：手机号已经注册
 *              003：用户名或密码错误
 *
 *
 */
public enum BizCodeEnume {
    UNKONW_EXCEPTION(10000,"系统未知异常"),
    SMS_EXCEPTION(10002,"验证码获取频率过高，请稍后再试"),
    VAILD_EXCEPTION(10001,"数据校验失败"),
    PHONE_EXCEPTION(15002,"手机号已经注册"),
    USERNAME_OR_PASSWORD_EXCEPTION(15003,"用户名或密码错误"),
    USER_EXCEPTION(15001,"用户名已存在");
    private final String msg;
    private final int code;
    BizCodeEnume(int code,String msg){
        this.code =code;
        this.msg = msg;
    }
    public int getCode(){
        return code;
    }

    public String getMsg() {
        return msg;
    }
}