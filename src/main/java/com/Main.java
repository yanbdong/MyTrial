package com;

import java.util.Map;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;

import com.database.MyBatisStarter;
import lombok.extern.slf4j.Slf4j;

/**
 * Main
 *
 * @author yanbodong
 * @date 2021/04/30 19:39
 **/
@SpringBootApplication()
@Slf4j
public class Main implements CommandLineRunner {

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * {@link #run(String...)}
     *
     * @param args
     */
    public static void main(String[] args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(Main.class);
        builder.web(WebApplicationType.NONE);
        ApplicationContext applicationContext = builder.run();
        Map<String, DataSource> map = applicationContext.getBeansOfType(DataSource.class);
        log.debug(map.toString());
    }


    @Override
    public void run(String... args) throws Exception {
        Map<String, DataSource> map = applicationContext.getBeansOfType(DataSource.class);
        log.debug(map.toString());
    }
}
