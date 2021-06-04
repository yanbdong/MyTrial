package com.alter.db;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Multi-DataSource
 *
 * @author yanbodong
 * @date 2021/05/12 10:48
 **/
@Configuration
public class DataSourceConfig {

    //主数据源配置 ds1数据源
    @Primary
    @Bean(name = "devDataSourceProperties")
    @ConfigurationProperties(prefix = "spring.datasource.lztj")
    public DataSourceProperties devDataSourceProperties() {
        return new DataSourceProperties();
    }

    //主数据源 ds1数据源
    @Primary
    @Bean(name = "devDataSource")
    public DataSource devDataSource(
        @Qualifier("devDataSourceProperties") DataSourceProperties dataSourceProperties) {
        return dataSourceProperties.initializeDataSourceBuilder().build();
    }

//    //第二个ds2数据源配置
//    @Bean(name = "ds2DataSourceProperties")
//    @ConfigurationProperties(prefix = "spring.datasource.ds2")
//    public DataSourceProperties ds2DataSourceProperties() {
//        return new DataSourceProperties();
//    }
//
//    //第二个ds2数据源
//    @Bean("ds2DataSource")
//    public DataSource ds2DataSource(
//        @Qualifier("ds2DataSourceProperties") DataSourceProperties dataSourceProperties) {
//        return dataSourceProperties.initializeDataSourceBuilder().build();
//    }
}
