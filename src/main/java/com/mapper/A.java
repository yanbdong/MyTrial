package com.mapper;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yanbdong@cienet.com.cn
 * @since Jul 28, 2020
 */
@Data
//@NoArgsConstructor
@Builder
public class A {

    private String name;
    private int heart;
    private Car car;

}
