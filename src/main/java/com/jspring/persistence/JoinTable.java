package com.jspring.persistence;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({ TYPE })
@Retention(RUNTIME)
@Repeatable(JoinTables.class)
public @interface JoinTable {

	String leftTable() default "";

	String leftColumn() default "";

	/**
	 * INNER, LEFT, RIGHT
	 * 
	 * @return
	 */
	String joinType() default "INNER";

	String database() default "";

	String name() default "";

	String shortName() default "";

	String column() default "";

}
