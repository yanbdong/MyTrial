package com.process.redirect;

/**
 * @author yanbdong@cienet.com.cn
 * @since Feb 25, 2021
 */
public interface DestroyHandler {
    void destroy(Process process);

    boolean isProcessAlive(Process process);
}
