package com.jspring.data;

import org.springframework.jdbc.core.JdbcTemplate;

public class Database {
    public final String name;
    public final JdbcTemplate jdbcTemplate;

    public Database(String name, JdbcTemplate jdbcTemplate) {
        this.name = name;
        this.jdbcTemplate = jdbcTemplate;
    }

}