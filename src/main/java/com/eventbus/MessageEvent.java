package com.eventbus;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * @author yanbdong@cienet.com.cn
 * @since Aug 07, 2020
 */
@Getter
@Builder
@ToString
public class MessageEvent {

    private Integer id;
    private String name;
}
