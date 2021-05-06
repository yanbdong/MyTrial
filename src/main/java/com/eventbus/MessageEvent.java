package com.eventbus;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author yanbdong@cienet.com.cn
 * @since Aug 07, 2020
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@NoArgsConstructor
@Getter
@Setter
//@Builder(access = AccessLevel.PACKAGE)
@ToString
@Accessors(fluent = true, chain = true)
public class MessageEvent {

    private Integer id;
    private String name;

    public int getSb() {
        return 1;
    }

    public int getSb(String s) {
        return 1;
    }
}
