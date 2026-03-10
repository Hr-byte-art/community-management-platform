package com.community.annotation;

import java.lang.annotation.*;

/**
 * 权限验证注解
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Auth {
    /**
     * 需要的角色，默认为 ADMIN
     */
    String value() default "ADMIN";
}
