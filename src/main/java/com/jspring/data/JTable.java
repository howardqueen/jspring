package com.jspring.data;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.persistence.Table;

import org.springframework.core.annotation.AliasFor;

@Target({ TYPE })
@Retention(RUNTIME)
public @interface JTable {

	/**
	 * (Optional) The name of the table.
	 * <p>
	 * Defaults to the entity name.
	 */
	@AliasFor(annotation = Table.class, attribute = "name")
	String name() default "";

	/**
	 * (Optional) The schema of the table.
	 * <p>
	 * Defaults to the default schema for user.
	 */
	@AliasFor(annotation = Table.class, attribute = "schema")
	String schema() default "";

	//////////////////
	/// MORE
	//////////////////
	String title() default "";

	String width() default "600px";

	String height() default "";

	String partitionColumn() default "";

}
