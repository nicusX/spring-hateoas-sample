package com.opencredo.demo.hateoas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        final ApplicationContext context = SpringApplication.run(Application.class, args);
    }

    @Bean @Primary
    @Profile("extdb")
    @ConfigurationProperties(prefix="remotedb.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }
}
