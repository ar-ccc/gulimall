package com.arccc.gulimall.thirdparty.controller;

import com.arccc.common.utils.R;
import com.arccc.gulimall.thirdparty.component.SmsComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sms")
public class SendCodeController {

    @Autowired
    SmsComponent smsComponent;

    /**
     * 提供给别人调用
     * @param phone 手机号
     * @param code 验证码
     * @return
     */
    @GetMapping("/send")
    public R sendCode(@RequestParam("phone") String phone,
                      @RequestParam("code") String code){
        smsComponent.sendCode(phone,code);
        return R.ok();
    }

}
