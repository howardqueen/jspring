package com.jspring.persistence.sql;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import com.jspring.persistence.sql.mysql.MysqlBuilders;

@ComponentScan(value = { "com.jspring.persistence" })
public class PersistenceConfig {

	@Bean
	public ISqlBuilders sqlBuilders() {
		return new MysqlBuilders();
	}

}
