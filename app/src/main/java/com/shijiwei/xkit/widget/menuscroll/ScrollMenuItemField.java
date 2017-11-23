package com.shijiwei.xkit.widget.menuscroll;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by shijiwei on 2016/10/6.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ScrollMenuItemField {

    /* 标签  lable */
    String lableFiled() default "";

    /* 图标资源文件地址  iconResourceId */
    String iconResourceIdFiled() default "";

    /* 图标地址 iconURL */
    String iconURLFiled() default "";
}
