package com.bytecode;

/**
 * @author yanbdong@cienet.com.cn
 * @since Mar 15, 2021
 */
class Singleton {

    private static volatile Singleton s;
    private int value = 1;
    public static final int f = 0x101;
    public static final int f1 = 1;

    private Singleton() {}

    public static Singleton getInstance() {
        if (null == s) {
            synchronized (Singleton.class) {
                if (null == s) {
                    s = new Singleton();
                }
            }
        }
        final int tmp= 3;
        s.value = tmp + f;
        return s;
    }

    public void s(int v) {
        double s = v + 126 +127 +128 + 32934023948D + 10D +342342342;
        String ss = "adc" + "efg";
    }

}
