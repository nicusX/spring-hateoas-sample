package com.opencredo.demo.hateoas;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@SpringBootApplication
public class Application {
    private final static Logger LOG = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        final ApplicationContext context = SpringApplication.run(Application.class, args);
    }

    @Bean @Primary
    @ConditionalOnProperty("externaldb")
    @ConfigurationProperties(prefix="extdb.datasource")
    public DataSource dataSource() {
        LOG.info("Using external database");
        return DataSourceBuilder.create().build();
    }
}
