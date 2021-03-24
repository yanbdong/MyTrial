package com.bytecode;

/**
 * @author yanbdong@cienet.com.cn
 * @since Mar 17, 2021
 */
class TryString {

    private static String SS = "1";
    private final String SSS = "1";
    private final Object O = new Object();
    private String s1 = new String("1");
    private String s2 = "1";
    private String s3 = "1".intern();

    public String m() {

        String s1 = new String("1").intern();
        String s2 = "1";
        String s3 = "1".intern();
        StringBuilder sb = new StringBuilder().append(s1).append(s2).append(s3);
        return sb.toString().intern();
    }

}
