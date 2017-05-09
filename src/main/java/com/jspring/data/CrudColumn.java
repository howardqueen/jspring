package com.jspring.data;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({ FIELD })
@Retention(RUNTIME)
public @interface CrudColumn {
	// 基础信息
	String title() default "";

	String width() default "";

	String height() default "";

	// 列表
	String header() default "";

	boolean sortable() default false;

	// 过滤
	boolean filterable() default true;

	// 创建
	boolean createable() default true;

	boolean required() default false;

	// 更新
	boolean updateable() default true;

	boolean readonly() default false;

}
