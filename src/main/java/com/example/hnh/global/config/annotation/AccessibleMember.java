package com.example.hnh.global.config.annotation;

import com.example.hnh.member.MemberRole;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AccessibleMember {
    MemberRole[] requiredRoles() default {};
}
