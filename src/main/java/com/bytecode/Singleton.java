package com.bytecode;

/**
 * @author yanbdong@cienet.com.cn
 * @since Mar 15, 2021
 */
class Singleton {

    private static volatile Singleton s;

    private Singleton() {}

    public static Singleton getInstance() {
        if (null == s) {
            synchronized (Singleton.class) {
                if (null == s) {
                    s = new Singleton();
                }
            }
        }
        return s;
    }

}
