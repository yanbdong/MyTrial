package com.qidian;

import com.google.common.collect.Lists;

import java.util.Collections;

/**
 * @author yanbdong@cienet.com.cn
 * @since Mar 16, 2021
 */
public class Main {

    public static void main(String[] args) {
        System.out.println(new Question2().apply(Lists.newArrayList(1,-2,0,3)));
        System.out.println(new Question2().apply(Lists.newArrayList(1,-2,-2,3)));
    }

}
