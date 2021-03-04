package com.mapper;

/**
 * @author yanbdong@cienet.com.cn
 * @since Jul 28, 2020
 */
public class MainTest {

    public static void main(String[] args) {
        A a = A.builder().build();
        a.setName("sb");
        a.setHeart(1);
        B b = M.Instance.mm(a);
    }
}
