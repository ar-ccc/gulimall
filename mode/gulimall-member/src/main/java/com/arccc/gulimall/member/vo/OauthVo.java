package com.arccc.gulimall.member.vo;

import lombok.Data;

/**
 * 用于通过token查询的用户信息
 */
public class OauthVo {
    @Data
    public static class GiteeVo{
        private Integer id;
        private String login;
        private String name;

    }
}
