package com.mapper;

import jakarta.validation.constraints.NotNull;
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

    @NotNull
    private String name;
    private int heart;
    private Car car;

}
