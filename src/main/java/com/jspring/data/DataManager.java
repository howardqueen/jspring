package com.jspring.data;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.jspring.Exceptions;
import com.jspring.Strings;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Component
public class DataManager {

	@Autowired
	Environment environment;

	private final ArrayList<Database> items = new ArrayList<>();

	public Database getDatabase(String name) {
		if (Strings.isNullOrEmpty(name)) {
			name = "spring";
		}
		for (Database d : items) {
			if (d.name.equals(name)) {
				return d;
			}
		}
		synchronized (this) {
			for (Database d : items) {
				if (d.name.equals(name)) {
					return d;
				}
			}
			String url = environment.getProperty(name + ".datasource.url");
			if (Strings.isNullOrEmpty(url)) {
				throw Exceptions.newNullArgumentException("[Properties]" + name + ".datasource.url");
			}
			String username = environment.getProperty(name + ".datasource.username");
			String password = environment.getProperty(name + ".datasource.password");
			DriverManagerDataSource dataSource = new DriverManagerDataSource();
			dataSource.setDriverClassName("com.mysql.jdbc.Driver");
			dataSource.setUrl(url);
			dataSource.setUsername(username);
			dataSource.setPassword(password);
			JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
			Database db = new Database(name, jdbcTemplate);
			items.add(db);
			return db;
		}
	}

	public Database getSpringDatabase() {
		return getDatabase("spring");
	}

}
