package com.arccc.common.constant;

public class MemberConstant {
    public enum OauthLoginType{
        GITEE("gitee");

        private final String type;
        public String getType(){
            return type;
        }
        OauthLoginType(String type){
            this.type=type;
        }
    }
}
