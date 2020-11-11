package com.ssh;

import java.io.IOException;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.ConnectionInfo;
import ch.ethz.ssh2.InteractiveCallback;
import ch.ethz.ssh2.Session;
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
        Connection connection = new Connection("172.16.4.101");
        ConnectionInfo info = connection.connect();
        String[] methods = connection.getRemainingAuthMethods("root");
        boolean b = connection.authenticateWithKeyboardInteractive("root", new InteractiveCallback() {

            @Override
            public String[] replyToChallenge(String name, String instruction, int numPrompts, String[] prompt,
                    boolean[] echo) throws Exception {
                return new String[0];
            }
        });
        Session session = connection.openSession();
        session.execCommand("ls");
        session.close();
        Session session1 = connection.openSession();
        session1.execCommand("ls");
        connection.close();
    }
}
