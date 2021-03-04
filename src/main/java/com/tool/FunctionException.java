package com.tool;

/**
 * @author yanbdong@cienet.com.cn
 * @since Nov 26, 2020
 */
class FunctionException extends Exception {

    public FunctionException(int i, Object... objects) {
    }

    public FunctionException(Exception e) {
        super(e);
    }
}
