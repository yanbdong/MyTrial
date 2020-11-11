package com.tool;

import com.google.common.base.Strings;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import ch.ethz.ssh2.ChannelCondition;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;
import ch.ethz.ssh2.SCPInputStream;
import ch.ethz.ssh2.SCPOutputStream;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import lombok.extern.slf4j.Slf4j;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuple4;
import reactor.util.function.Tuples;

@Slf4j
public class SshUtil {

    private static final boolean debug = true;
    private static final String SSH_AUTH_PASSWORD = "password";
    private static final String SSH_AUTH_KEYBOARD_INTERACTIVE = "keyboard-interactive";

    public static Connection sshConnect(CommandOptions commandOptions) throws IOException, ExecutionException {
        Connection conn = new Connection(commandOptions.getValue(Type.IP), commandOptions.getValue(Type.PORT));
        if (debug) {
            return conn;
        }
        conn.connect();
        boolean isAuthenticated = false;
        // conduct identity authentication
        // publickey password keyboard-interactive
        String name = commandOptions.getValue(Type.USER);
        String password = commandOptions.getValue(Type.PASSWORD);
        if (conn.isAuthMethodAvailable(name, SSH_AUTH_PASSWORD)) {
            isAuthenticated = conn.authenticateWithPassword(name, password);
        }
        if (!isAuthenticated && conn.isAuthMethodAvailable(name, SSH_AUTH_KEYBOARD_INTERACTIVE)) {
            conn.authenticateWithKeyboardInteractive(name, (n, instruction, numPrompts, prompt, echo) -> {
                if (Strings.isNullOrEmpty(password)) {
                    return new String[0];
                } else {
                    return new String[] { password };
                }
            });
        }
        return conn;
    }

    public static Tuple4<Integer, String, String, Float> execCommand(Connection connection, String command)
            throws Exception {
        log.info("execute cmd: {}", command);
        final long startTime = System.nanoTime();
        Tuple3<Integer, String, String> r = execCommand0(connection, command);
        final float timeCost = ((float) (System.nanoTime() - startTime)) / 1000000;
        log.info("Done in {}ms with result {}", timeCost, r);
        return Tuples.of(r.getT1(), r.getT2(), r.getT3(), timeCost);
    }

    private static Tuple3<Integer, String, String> execCommand0(Connection connection, String command)
            throws Exception {
        if (debug) {
            return Tuples.of(0, "", "");
        }
        Session session = connection.openSession();
        try {
            session.execCommand(command);
            // Collect std/err
            try (BufferedReader stdout = new BufferedReader(
                    new InputStreamReader(new StreamGobbler(session.getStdout())));
                    BufferedReader stderr = new BufferedReader(
                            new InputStreamReader(new StreamGobbler(session.getStderr())))) {
                // BLock till command done or timeout
                session.waitForCondition(ChannelCondition.CLOSED | ChannelCondition.TIMEOUT
                        | ChannelCondition.EXIT_STATUS | ChannelCondition.EXIT_SIGNAL, 5 * 60 * 1000L);
                return Tuples.of(session.getExitStatus(),
                        stdout.lines().collect(Collectors.joining(System.lineSeparator())),
                        stderr.lines().collect(Collectors.joining(System.lineSeparator())));
            }
        } finally {
            session.close();
        }
    }

    public static Boolean scpPut(Connection connect, String localFile, String remoteDirectory) throws IOException {
        File file = new File(localFile);
        SCPClient scpClient = connect.createSCPClient();
        try (SCPOutputStream os = scpClient.put(file.getName(), file.length(), remoteDirectory, "0600");
                FileInputStream fis = new FileInputStream(file)) {
            byte[] b = new byte[4096];
            int i;
            while ((i = fis.read(b)) != -1) {
                os.write(b, 0, i);
            }
            os.flush();
        }
        return true;
    }

    public static float scpGet(Connection connection, String remoteDirectory, String localDirectory)
            throws IOException {
        log.info("Copy from {} into {}", remoteDirectory, localDirectory);
        final long startTime = System.nanoTime();
        scpGet0(connection, remoteDirectory, localDirectory);
        float r = ((float) (System.nanoTime() - startTime)) / 1000000;
        log.info("Done in {}ms", r);
        return r;
    }

    private static Boolean scpGet0(Connection connection, String remoteDirectory, String localDirectory)
            throws IOException {
        if (debug) {
            return true;
        }
        SCPClient scpClient = connection.createSCPClient();
        try (SCPInputStream is = scpClient.get(remoteDirectory);
                FileOutputStream os = new FileOutputStream(new File(localDirectory))) {
            byte[] b = new byte[4096];
            int i;
            while ((i = is.read(b)) != -1) {
                os.write(b, 0, i);
            }
            os.flush();
        }
        return true;
    }

}
