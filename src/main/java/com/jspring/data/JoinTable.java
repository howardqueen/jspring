package com.jspring.data;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({ TYPE })
@Retention(RUNTIME)
public @interface JoinTable {

	String joinType() default "INNER";

	String schema() default "";

	String name() default "";

	String nickName() default "";

	String joinColumn() default "";

	String referencedColumn() default "";

}
