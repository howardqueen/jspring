package com.jspring.data;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({ TYPE })
@Retention(RUNTIME)
public @interface CrudTable {
	// 导出
	String title() default "";

	//
	String width() default "600px";

	String height() default "";

	// 创建
	boolean createable() default true;

	boolean createCheckNull() default false;

	// 更新
	boolean updateable() default true;

	boolean updateCheckNull() default false;

	//
	boolean exportable() default false;

	//
	String partitionDateColumn() default "";
}
