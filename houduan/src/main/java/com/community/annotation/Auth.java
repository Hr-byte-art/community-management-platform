package com.community.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Auth {
    String value() default "ADMIN";
    String[] permissions() default {};
    boolean requireAllPermissions() default true;
}
