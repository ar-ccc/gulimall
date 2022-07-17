package com.arccc.gulimall.order.config;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.arccc.gulimall.order.vo.PayVo;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "alipay")
@Component
@Data
public class AlipayTemplate {

    //在支付宝创建的应用的id
    public static String app_id = "2021000121626699";

    // 商户私钥，您的PKCS8格式RSA2私钥
    private  String merchant_private_key = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCcQStBrRde0LN4DwYg4piKnFfRTIsDKeNS8lPBRehvPVMC/RIlxpsyfZWwj3UQJuF9LPU1O/iDBFQHVw2VFc/Jzc0wM75Wyab5xofDUDEz4kzN1z3CqhrqXfycL7QlbFBKD4wYA67izaYQfgxlqhP/m+YLnbHYpgZ8b4ftmYcJFscnFayvkhDs/v05zcDZAAqzI1uAHvuchhXbWTKy4ujVV1/T4kjQw+umnId116X8Pn49X1M6isMaJf/km5yHbPSiy45Tc3zizovb1ZTRnpwgGRauPrA2YGirbFYgQ1qOVkbF8pDfre5eTWcK5TIqY6VUpBvX3xlP04t+Y7YtXz5pAgMBAAECggEAXGjyUzIyOyIEsgF4p2Q7wOY7zY2OPT29sqWcQRa+I2PKP186J1sxGAhLyhLdscK84tH+5w/jpmCd7YVfhAdVT01Gh7ZiYGN8PAZc7nbz0nKI58ffP8xohKk3J4vS57sKNPLC8MTk3j+vcduKK2oNz8L8p2wI7U8rUNaJNkzxHr8MIakUqLsgmP0GvEriKzoc6knVIe0rjtUQlHyXpQcw0zapxfow+aBpmaMylXbnw7+bMAkPnQ2uizqXEUBIzUa37yeRSSUfi722KQDdYmhjCxEEy+ujKiR/qill2DE4inAU+Iq1ZeUcSO74vF8bPEx+JEy7CMi00pbnFrCCQPxlHQKBgQDQGIahj2dFOwjm0ujm+YikKkXxpJpCwVKhhX2/L1Tu6dScoQ+FuyU9/NVLO5oSbE+6n2k36mxuTsKkTKldOlwZeRNy3xZXn6myXg9jq6xXza69nCEXljDj1xDIq8odgJkrHihP/cXGQFMoM/GaKlk7Q4J6tZsDOoyQICMykPvQewKBgQDAOYrLXO8PAv/fB+J9dMgrzsKCRwgC7i3uFIbf1/kfSAQbT30mQHuyfbYqrIJMj8jwLRn69hkdsmTr04AgjStz5Vrei7/JEl1r3WFVE8IHN4Nf93mg6OUQGmHu0G1D+lYAqDHwFkzhaUI4sC6E0Rnwh/zZ3d+uT7FnB/EM+7nhawKBgQCXEycyxaSIu1o0Eayqe0C7RkFvNDwV+Lofr4ViNl+n77S+XaOiSAK6pQLI0qWBeIbNiwvdX2CHGMDwfQN8PnE8wQzgmZtj0/NkHit4BdtiB140I9RWhmBKGQoDVcyvbHxGtGc545gg3PRCPKvLa7FKeDGTGqNGTcxcmihTfrSCXwKBgBfzk42/aLIwJ32818XWFVMQEkB1ZVQoJWTy/dNKb+or6QcWInvvsPoOm/I5SIsNp3X1yLTykTKYFecklWR48p8gRvBP7Xe6aBWWohgdvCF2E1KT3X3lRxV4lBdTtSmxOaIfUwmdXbsAiJXTrhgyjc7gwurKsKJhn7M81BZQD1s9AoGAaEKZrqMODOTlEr8+ijJ+048j+3C1qtqo/4YYdO+7la20CEmV1x2721zWfDC/Y+wTbD6I6m1W4XAs/nBw9Wf9pKwJtoDNVAopa1j2TfUj38VWM4p8zGp45zEcdiJy8DsB1nk7ot4URFzn+ZbpSeqiJ5/FHF89RKCN3Jv/fZcmYmk=";
    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    private  String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgEmjvVZI6CAwmMUFZS8fcb0PfimIAQgKxGxlccGwm5zOqPtJE2B13qSNcE4zt2grqNk6omenZQw6/LB5B7Glx3lu4Vdg+6YVqpcqgqpmf6w3iNCV0JgZ9yFYLpoJWw2OPMWs/FrcorcsdCL0hWEP+KX5ARnTsY2iS8cbcsUXouqOEVUxyuwFZU6xr1wforXCO2eRB/9dBlbYWK90d2kcevUryg6lJk4xKOYocLre5iohLarXvHgtCZgvhroqbaDcNB4aSM309d0GnOOtZBoCRiwTUkW61Vbf0+2o5Qam0XDZwSJOaOZgYBHhzUntsglRldBNWjpkjIeMxDsoQz2ONwIDAQAB";
    // 服务器[异步通知]页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    // 支付宝会悄悄的给我们发送一个请求，告诉我们支付成功的信息
    private  String notify_url = "http://arccc.vaiwan.cn/payed/notify";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    //同步通知，支付成功，一般跳转到成功页
    private  String return_url = "http://order.gulimall.com/list.html";

    // 签名方式
    private  String sign_type = "RSA2";

    // 字符编码格式
    private  String charset = "utf-8";

    // 支付宝网关； https://openapi.alipaydev.com/gateway.do
    private  String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    public  String pay(PayVo vo) throws AlipayApiException {

        //AlipayClient alipayClient = new DefaultAlipayClient(AlipayTemplate.gatewayUrl, AlipayTemplate.app_id, AlipayTemplate.merchant_private_key, "json", AlipayTemplate.charset, AlipayTemplate.alipay_public_key, AlipayTemplate.sign_type);
        //1、根据支付宝的配置生成一个支付客户端
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,
                app_id, merchant_private_key, "json",
                charset, alipay_public_key, sign_type);

        //2、创建一个支付请求 //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(return_url);
        alipayRequest.setNotifyUrl(notify_url);

        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = vo.getOut_trade_no();
        //付款金额，必填
        String total_amount = vo.getTotal_amount();
        //订单名称，必填
        String subject = vo.getSubject();
        //商品描述，可空
        String body = vo.getBody();
        //订单超时时间
        String orderTimeOut = vo.getOrderTimoOut();

        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                + "\"total_amount\":\""+ total_amount +"\","
                + "\"subject\":\""+ subject +"\","
                + "\"body\":\""+ body +"\","
                + "\"time_expire\":\""+ orderTimeOut +"\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        String result = alipayClient.pageExecute(alipayRequest).getBody();

        //会收到支付宝的响应，响应的是一个页面，只要浏览器显示这个页面，就会自动来到支付宝的收银台页面
        System.out.println("支付宝的响应："+result);

        return result;

    }
}
