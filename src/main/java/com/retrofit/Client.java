package com.retrofit;

import com.property.ReadFromCustomizedProperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;

import lombok.extern.slf4j.Slf4j;

/**
 * @author yanbdong@cienet.com.cn
 * @since Nov 17, 2020
 */
//@ComponentScan(basePackageClasses = ReadFromCustomizedProperties.class, basePackages = "com.retrofit")
//@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@ConfigurationProperties("yanbdong")
@EnableConfigurationProperties(ReadFromCustomizedProperties.class)
@Slf4j
public class Client {

    @Autowired
    private ReadFromCustomizedProperties mReadFromCustomizedProperties;

    @Value("${yanbdong.yy}")
    private int a;
    private int yy;

    @Autowired
    private Environment mEnvironment;

    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(Client.class, args);
        Thread.sleep(10000);
    }

    public int getYy() {
        return yy;
    }

    public Client setYy(int yy) {
        this.yy = yy;
        return this;
    }

    @EventListener(Object.class)
    public void myTry(Object event) {
        // log.info("event: {}", event);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void hh(ApplicationReadyEvent event) {
        log.info("ApplicationReadyEvent: {}", mReadFromCustomizedProperties);
        log.info("yy: {}", yy);
    }
}
