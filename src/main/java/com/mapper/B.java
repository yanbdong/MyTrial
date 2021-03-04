package com.mapper;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yanbdong@cienet.com.cn
 * @since Jul 28, 2020
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class B {

    private String sb;
    private String you;
    private Person person;
    private int hh;

}
