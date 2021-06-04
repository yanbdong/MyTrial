package com;

import java.util.Map;
import java.util.Objects;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cglib.core.DebuggingClassWriter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

import com.auto.wire.AutoWireImpl;
import com.auto.wire.IAutoWire;
import lombok.extern.slf4j.Slf4j;

/**
 * Main
 *
 * @author yanbodong
 * @date 2021/04/30 19:39
 **/
@SpringBootApplication()
@Slf4j
public class Main implements CommandLineRunner, ApplicationListener<ApplicationEvent> {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private IAutoWire autoWire;
    @Autowired
    private AutoWireImpl autoWireImpl;

    /**
     * {@link #run(String...)}
     *
     * @param args
     */
    public static void main(String[] args) {
        System.setProperty(DebuggingClassWriter.DEBUG_LOCATION_PROPERTY,
            "/Users/yanbodong/Documents/workspace/MyTrial/com/sun/proxy");

        SpringApplicationBuilder builder = new SpringApplicationBuilder(Main.class);
        builder.web(WebApplicationType.NONE);
        ApplicationContext applicationContext = builder.run();
        Map<String, DataSource> map = applicationContext.getBeansOfType(DataSource.class);
        log.debug(map.toString());
    }

    private void boxAndUnbox() {
        Integer I = 1;
        int i = I;
    }

    private void compare() {
//        Integer I1 = 1;
//        Integer I2 = 1000000;
//        boolean b1 = I1 == I2;
//        boolean b2 = I1 == 1;
//        long l1 = 11111111111111111L;
//        long l2 = 111111111111111112L;
//        boolean b3 = l1 == l2;
        boolean b4 = null == null;
    }

    private void compare1() {
        Objects a = null;
        boolean b4 = null == a;
    }

    private void compare2() {
        Objects a = null;
        boolean b4 = a == null;
    }

    private void compare3(String s) {
        boolean b4 = s + "1" == "s1";
    }


    @Override
    public void run(String... args) throws Exception {
        Map<String, DataSource> map = applicationContext.getBeansOfType(DataSource.class);
        log.info(map.toString());
    }

    @Override
    public void onApplicationEvent(final ApplicationEvent event) {
        log.info("ApplicationEvent: {}", event);
    }
}
