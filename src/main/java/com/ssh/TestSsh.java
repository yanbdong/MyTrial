package com.ssh;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.ConnectionInfo;
import ch.ethz.ssh2.InteractiveCallback;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * @author yanbdong@cienet.com.cn
 * @since Jul 28, 2020
 */
public class TestSsh {

    @Getter
    @Setter
    @NonNull
    private String ip;

    @Getter
    @Setter
    private int port;

    public static void main(String[] args) throws IOException {
        TestSsh ssh = new TestSsh();
        Connection connection = new Connection("192.168.211.100");
        ConnectionInfo info = connection.connect();
        String[] methods = connection.getRemainingAuthMethods("root");
        boolean b = connection.authenticateWithKeyboardInteractive("root",  new InteractiveCallback() {

            @Override
            public String[] replyToChallenge(String name, String instruction, int numPrompts, String[] prompt, boolean[] echo) throws Exception {
                return new String[0];
            }
        });
        System.out.println("args = " + b);
        connection.close();
    }
}
