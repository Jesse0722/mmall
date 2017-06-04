package com.mmall.common;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * Created by jesse on 2017/5/29.
 */
public class Const {
    public static final  String CURRENT_USER = "current_user";

    public static final String EMAIL = "email";

    public static final String USERNAME = "username";

    public interface Role{
        int ROLE_CUSTOMER = 0;
        int ROLE_ADMIN = 1;
    }

    public interface ProductListOrderBy{
        Set<String> PRICE_ASC_DESC = Sets.newHashSet("price_asc","price_desc");
    }

    public enum ProductStatusEnum{
        ON_SALE("在线",1);
        private String value;
        private int code;

        ProductStatusEnum(String value, int code) {
            this.value = value;
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }
    }
}
