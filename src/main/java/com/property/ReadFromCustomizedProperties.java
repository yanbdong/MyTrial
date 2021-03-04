package com.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import lombok.Setter;
import lombok.ToString;

/**
 * @author yanbdong@cienet.com.cn
 * @since Nov 18, 2020
 */
@Component
@ConfigurationProperties(value = "hh")
@PropertySource(value = "classpath:config/b.yml", factory = YamlPropertySourceFactory.class)
@ToString
@Setter
public class ReadFromCustomizedProperties {

    private int a;
    private String b;
    private String c;
}
