package com.arccc.gulimall.auth.vo;

import lombok.Data;

@Data
public class SociaUser {
    private String access_token;
    private String token_type;
    private Integer expires_in;
    private String refresh_token;
    private String scope;
    private String created_at;
}
