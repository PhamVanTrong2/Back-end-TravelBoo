package com.bootravel.common.database.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfiguration {
    @Bean(name = "dataSource")
    @Primary
    @ConfigurationProperties("spring.datasource.hikari")
    public DataSource customDataSource() {
        return DataSourceBuilder.create().build();
    }
}
