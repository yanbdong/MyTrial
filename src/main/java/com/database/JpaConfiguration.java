package com.database;

import org.springframework.context.annotation.Configuration;

/**
 * @author yanbdong@cienet.com.cn
 * @since Aug 31, 2020
 */
//@Order(Ordered.HIGHEST_PRECEDENCE)
@Configuration
//@EnableTransactionManagement(proxyTargetClass = true)
//@EnableJpaRepositories("com.database.repo")
//@EntityScan("com.database.module")
public class JpaConfiguration {

//    @Bean
//    ServletRegistrationBean h2servletRegistration(){
//        ServletRegistrationBean registrationBean = new ServletRegistrationBean( new WebServlet());
//        registrationBean.addUrlMappings("/h2-console/*");
//        return registrationBean;
//    }

//    PersistenceExceptionTranslationPostProcessor persistenceExceptionTranslationPostProcessor() {
//        return new PersistenceExceptionTranslationPostProcessor();
//    }

//    public DataSource dataSource() {
//        DriverManagerDataSource dataSource = new DriverManagerDataSource();
//        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
//        dataSource.setUrl("jdbc:mysql://localhost:3306/test?characterEncoding=utf8");
//        dataSource.setUsername("root");
//        dataSource.setPassword("sql159159");
//        return dataSource;
//    }
//
//    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
//        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
//        entityManagerFactoryBean.setDataSource(dataSource());
//        entityManagerFactoryBean.setPackagesToScan("com.database.entity");
//        entityManagerFactoryBean.setJpaProperties(buildHibernateProperties());
//        entityManagerFactoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter() {
//            {
//                setDatabase(Database.MYSQL);
//            }
//        });
//        return entityManagerFactoryBean;
//    }
//
//    protected Properties buildHibernateProperties() {
//        Properties hibernateProperties = new Properties();
//
//        hibernateProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");
//        hibernateProperties.setProperty("hibernate.show_sql", "true");
//        hibernateProperties.setProperty("hibernate.use_sql_comments", "false");
//        hibernateProperties.setProperty("hibernate.format_sql", "true");
//        hibernateProperties.setProperty("hibernate.hbm2ddl.auto", "update");
//        hibernateProperties.setProperty("hibernate.generate_statistics", "false");
//        hibernateProperties.setProperty("javax.persistence.validation.mode", "none");
//
//        // Audit History flags
//        hibernateProperties.setProperty("org.hibernate.envers.store_data_at_delete", "true");
//        hibernateProperties.setProperty("org.hibernate.envers.global_with_modified_flag", "true");
//
//        return hibernateProperties;
//    }
}
