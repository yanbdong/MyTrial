/*************************************************************************
 *
 * CIeNET CONFIDENTIAL
 * __________________
 *
 *  CIeNET Technologies
 *  All Rights Reserved.
 *
 * NOTICE:  All source codes contained herein are, and remain
 * the property of CIeNET Technologies. The intellectual and technical concepts contained
 * herein are proprietary to CIeNET Technologies
 * and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from CIeNET Technologies.
 *************************************************************************/

package com.process.redirect;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * <ol>
 * <li>Call {@link #startProcessPool()} when Node starts</li>
 * <li>Use {@link #build()} to create {@link Builder}</li>
 * <li>Use {@link Builder#execute()} to start a process and get INSTANCE of {@link NodeProcess}</li>
 * <li>Use {@link NodeProcess#getResult()} to get {@link ProcessInfo}</li>
 * <li>Use {@link NodeProcess#cancel()} ()} to cancel this process</li>
 * <li>Call {@link #shutdownNowProcessPool()} ()} when Node finishes</li>
 * </ol>
 */
public class NodeProcess {

    public static final long COMMAND_TIMEOUT = 10000L;

    private static final int CANCELLED_VALUE = -1;
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(NodeProcess.class);
    private static final boolean IS_DEBUG = false;
    private static final boolean IS_INFO = false;
    private static final int BUFFER_SIZE = 4096;
    /**
     * Each NodeProcess needs at least <b>3</b> thread(to start the process) or at most <b>5</b> threads to execute.<br>
     * However, we learn that the max number of NodeProcess is 20. So we use a cached thread pool here.
     *
     * <h3>Notice:</h3> Remember to call {@link #shutdownNowProcessPool()} when Node quits.
     */
    private static ExecutorService sCachedThreadPool;
    private final ProcessController mProcessController;
    private final DestroyHandler mDestroyHandler;

    private NodeProcess(ProcessController processController, DestroyHandler destroyHandler) {
        mProcessController = processController;
        mDestroyHandler = destroyHandler;
    }

    // For test
    public static void main(String... args) throws Throwable {
        // testNormal();
        // testIgnore();
        // testRedirect();
        testToFile();
        // testToFileAndString();
        // testToDifferentFileAndString();
        // testPull();
        // testCancel();
        // testTimeout();
        // testKillInput();
        // testInputStream();
        // testCloseInputStream();
        // testDestroy();
    }

    private static void testCancel() throws Throwable {
        NodeProcess.startProcessPool();
        String s = "adb logcat";
        NodeProcess.ProcessInfo processInfo;
        try {
            NodeProcess nodeProcess = NodeProcess.build().setCommand(s).setWaitTime(COMMAND_TIMEOUT).execute();
            CompletableFuture.runAsync(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {
                    log.debug("InterruptedException, ignore", ignored);
                    // ignore
                }
                nodeProcess.cancel();
            });
            processInfo = nodeProcess.getResult();
        } finally {
            NodeProcess.shutdownNowProcessPool();
        }
        System.out.println(processInfo.toString());
    }

    public static void startProcessPool() {
        sCachedThreadPool = new ThreadPoolExecutor(3, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS,
                new SynchronousQueue<>());
    }

    public static Builder build() {
        return new Builder();
    }

    /**
     * Someone should ensure that this method is called!!
     */
    public static void shutdownNowProcessPool() {
        if (null != sCachedThreadPool) {
            sCachedThreadPool.shutdownNow();
        }
        sCachedThreadPool = null;
    }

    private static void cancelProcess(Process process) {
        try {
            // Close inputStream, otherwise it will block process to terminal.
            if (null != process.getInputStream()) {
                process.getInputStream().close();
            }
        } catch (Exception ignore) {
            // TODO
            // For cancel purpose, ignore
        }
        try {
            // Close inputStream, otherwise it will block process to terminal.
            if (null != process.getErrorStream()) {
                process.getErrorStream().close();
            }
        } catch (Exception ignore) {
            // TODO
            // For cancel purpose, ignore
        }
        process.destroyForcibly();
    }

    private static void testKillInput() {
        NodeProcess.startProcessPool();
        String s = "adb logcat";
        NodeProcess.ProcessInfo processInfo;
        try {
            NodeProcess nodeProcess = NodeProcess.build().setCommand(s).executeTestInputKill();
            nodeProcess.getResult();
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            NodeProcess.shutdownNowProcessPool();
        }
    }

    private static void testTimeout() throws Throwable {
        NodeProcess.startProcessPool();
        String s = "adb logcat";
        NodeProcess.ProcessInfo processInfo;
        try {
            NodeProcess nodeProcess = NodeProcess.build().setCommand(s).setWaitTime(1000).execute();
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            NodeProcess.shutdownNowProcessPool();
        }
    }

    /**
     * Split command into params.
     *
     * @see Runtime#exec(java.lang.String, java.lang.String[], java.io.File)
     */
    private static List<String> split(String command) {
        StringTokenizer st = new StringTokenizer(command);
        List<String> ret = new LinkedList<>();
        while (st.hasMoreTokens()) {
            ret.add(st.nextToken());
        }
        return ret;
    }

    /**
     * Someone should ensure that this method is called!!
     */
    public static void shutdownProcessPool() {
        if (null != sCachedThreadPool) {
            sCachedThreadPool.shutdown();
        }
        sCachedThreadPool = null;
    }

    private static void testNormal() throws Throwable {
        NodeProcess.startProcessPool();
        String s = "adb install xxx";
        NodeProcess.ProcessInfo processInfo;
        try {
            NodeProcess nodeProcess = NodeProcess.build().setCommand(s).setWaitTime(COMMAND_TIMEOUT).execute();
            processInfo = nodeProcess.getResult();
        } finally {
            NodeProcess.shutdownNowProcessPool();
        }
        System.out.println(processInfo.toString());
    }

    private static void testIgnore() throws Throwable {
        NodeProcess.startProcessPool();
        String s = "adb install xxx";
        NodeProcess.ProcessInfo processInfo;
        try {
            NodeProcess nodeProcess = NodeProcess.build().setCommand(s).ignoreOutput().setWaitTime(COMMAND_TIMEOUT)
                    .execute();
            processInfo = nodeProcess.getResult();
        } finally {
            NodeProcess.shutdownNowProcessPool();
        }
        System.out.println(processInfo.toString());
    }

    private static void testRedirect() throws Throwable {
        NodeProcess.startProcessPool();
        String s = "adb install xxx";
        NodeProcess.ProcessInfo processInfo;
        try {
            NodeProcess nodeProcess = NodeProcess.build().setCommand(s).isRedirectErrorStream(true)
                    .setWaitTime(COMMAND_TIMEOUT).execute();
            processInfo = nodeProcess.getResult();
        } finally {
            NodeProcess.shutdownNowProcessPool();
        }
        System.out.println(processInfo.toString());
    }

    private static void testToFile() throws Throwable {
        NodeProcess.startProcessPool();
        String s = "adb install xxx";
        NodeProcess.ProcessInfo processInfo;
        try {
            NodeProcess nodeProcess = NodeProcess.build().setCommand(s)
                    .recordOutputToStringThenWriteToFile(new File(System.getProperty("user.dir"), "t.txt"))
                    .setWaitTime(COMMAND_TIMEOUT).execute();
            processInfo = nodeProcess.getResult();
        } finally {
            NodeProcess.shutdownNowProcessPool();
        }
        System.out.println(processInfo.toString());
    }

    private static void testToFileAndString() throws Throwable {
        NodeProcess.startProcessPool();
        String s = "adb install xxx";
        NodeProcess.ProcessInfo processInfo;
        try {
            NodeProcess nodeProcess = NodeProcess.build().setCommand(s)
                    .mergeOutputToResultAndFile(new File(System.getProperty("user.dir"), "t.txt"))
                    .setWaitTime(COMMAND_TIMEOUT).execute();
            processInfo = nodeProcess.getResult();
        } finally {
            NodeProcess.shutdownNowProcessPool();
        }
        System.out.println(processInfo.toString());
    }

    private static void testToDifferentFileAndString() throws Throwable {
        NodeProcess.startProcessPool();
        String s = "adb install xxx";
        NodeProcess.ProcessInfo processInfo;
        try {
            NodeProcess nodeProcess = NodeProcess.build().setCommand(s)
                    .recordOutputToStringAndFile(new File(System.getProperty("user.dir"), "t_s.txt"),
                            new File(System.getProperty("user.dir"), "t_e.txt"))
                    .setWaitTime(COMMAND_TIMEOUT).execute();
            processInfo = nodeProcess.getResult();
        } finally {
            NodeProcess.shutdownNowProcessPool();
        }
        System.out.println(processInfo.toString());
    }

    private static void testPull() throws Throwable {
        NodeProcess.startProcessPool();
        String s = "adb install xxx";
        NodeProcess.ProcessInfo processInfo;
        try {
            NodeProcess nodeProcess = NodeProcess.build().setCommand(s).mergeOutputToFile(new File("xx"))
                    .setWaitTime(COMMAND_TIMEOUT).execute();
            processInfo = nodeProcess.getResult();
        } finally {
            NodeProcess.shutdownNowProcessPool();
        }
        System.out.println(processInfo.toString());
    }

    private static void testLineBreak() throws Throwable {
        String s = "bash /home/yanbdong/Desktop/x.sh";
        NodeProcess.ProcessInfo processInfo;
        try {
            NodeProcess nodeProcess = NodeProcess.build().setCommand(s).setWaitTime(COMMAND_TIMEOUT).execute();
            CompletableFuture.runAsync(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {
                    log.debug("InterruptedException, ignore", ignored);
                    // ignore
                }
                try {
                    OutputStream stream = nodeProcess.mProcessController.mProcessTask.join().getOutputStream();
                    stream.write(new byte[]{'a', '\n'});
                    stream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            processInfo = nodeProcess.getResult();
        } finally {
            NodeProcess.shutdownNowProcessPool();
        }
        System.out.println(processInfo.toString());
    }

    // For adb logcat, close InputStream will result process crash, not ErrorStream/OutputStream
    // exit with 141
    // For adb install, close InputStream/ErrorStream will result process crash, not OutputStream
    // exit with 141
    private static void testInputStream() throws Throwable {
        NodeProcess.startProcessPool();
        // Process process = new ProcessBuilder("adb", "logcat").start();
        Process process = new ProcessBuilder("adb", "install",
                "/home/yanbdong/Documents/workbench/nodeM/androidagent/app/build/outputs/apk/app-debug.apk").start();
        debug("process start");
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //
            debug("start to close InputStream");
            try {
                process.getInputStream().close();
            } catch (IOException e) {
                debug(e);
            }
            debug("close down InputStream");
            //
            debug("start to close ErrorStream");
            try {
                process.getErrorStream().close();
            } catch (IOException e) {
                debug(e);
            }
            debug("close down ErrorStream");
            //
            debug("start to close OutputStream");
            try {
                process.getOutputStream().close();
            } catch (IOException e) {
                debug(e);
            }
            debug("close down OutputStream");
        });
        boolean b = process.waitFor(10, TimeUnit.SECONDS);
        debug("process end with " + b + ". Value: " + process.exitValue());
    }

    private static void debug(Object o) {
        if (IS_DEBUG & o != null) {
            log.debug(o.toString());
        }
    }

    private static void info(Object o) {
        if (IS_INFO && o != null) {
            log.info(o.toString());
        }
    }

    private static void debug(Throwable e) {
        if (IS_DEBUG) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            log.debug(sw.toString());
        }
    }

    // Find anything
    private static void testCloseInputStream() throws Throwable {
        NodeProcess.startProcessPool();
        new ProcessBuilder("adb", "uninstall", "cn.com.cienet.mats").start().waitFor();
        Process process = new ProcessBuilder("adb", "install",
                "/home/yanbdong/Documents/workbench/nodeM/androidagent/app/build/outputs/apk/app-debug.apk").start();
        debug("process start");
        CompletableFuture.runAsync(() -> {
            try (BufferedReader outReader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                int i = 1;
                while (true) {
                    debug("to read: " + i);
                    String line = outReader.readLine();
                    debug("read: " + line);
                    i++;
                    if (line == null) {
                        debug("read done");
                        break;
                    }
                }
            } catch (IOException e) {
                debug(e);
            }
        });
        boolean b = process.waitFor(10, TimeUnit.SECONDS);
        debug("process end with " + b + ". Value: " + process.exitValue());
    }

    // Destroy first and then close inputStream
    private static void testDestroy() throws Throwable {
        NodeProcess.startProcessPool();
        new ProcessBuilder("adb", "uninstall", "cn.com.cienet.mats").start().waitFor();
        Process process = new ProcessBuilder("adb", "install",
                "/home/yanbdong/Documents/workbench/nodeM/androidagent/app/build/outputs/apk/app-debug.apk").start();
        debug("process start");
        CompletableFuture.runAsync(() -> {
            try (BufferedReader outReader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                int i = 1;
                while (true) {
                    debug("to read: " + i);
                    String line = outReader.readLine();
                    debug("read: " + line);
                    i++;
                    if (line == null) {
                        debug("read done");
                        break;
                    }
                }
            } catch (IOException e) {
                debug(e);
            }
        });
        Thread.sleep(200);
        process.destroyForcibly();
        debug("process end forcibly");
    }

    @Override
    public String toString() {
        return mProcessController.mBuilder.mExpression;
    }

    /**
     * {@link java.util.concurrent.Future#cancel(boolean)} can interrupt its associated threads. However,
     * {@link CompletableFuture#cancel(boolean)} can't.<br>
     * We call {@link Process#destroy()} directly.
     */
    public ProcessInfo cancel() {
        // Wait for process start done,
        // for we have to get the INSTANCE of process to control the procedure.
        if (!mProcessController.isDone()) {
            try {
                Process process = mProcessController.mProcessTask.join();
                log.debug("Process is to be cancelled: " + this);
                // Find the below error when close Errout
                // Maybe its a windows error.
                // Anyway, lets close process first
                // java.lang.Thread.State: TIMED_WAITING (on object monitor)
                // at java.lang.Object.wait(Native Method)
                // at sun.nio.ch.NativeThreadSet.signalAndWait(NativeThreadSet
                // .java:101)
                // - locked <0x00000000ddd94820> (a sun.nio.ch.NativeThreadSet)
                // at sun.nio.ch.FileChannelImpl.implCloseChannel(FileChannelImpl
                // .java:130)
                // at java.nio.channels.spi.AbstractInterruptibleChannel.close
                // (AbstractInterruptibleChannel.java:115)
                // - locked <0x00000000ddd947a8> (a java.lang.Object)
                mDestroyHandler.destroy(process);
                ReadableByteChannel stdoutChannel = mProcessController.mStdoutChannel.join();
                // Find the issue MATSUP-2665
                // Stream cannot close and the process is hanging here
                if (null != stdoutChannel) {
                    stdoutChannel.close();
                }
                ReadableByteChannel erroutChannel = mProcessController.mErroutChannel.join();
                if (null != erroutChannel) {
                    erroutChannel.close();
                }
                // Cancel again
                // Maybe it's redundant
                cancelProcess(process);
                log.debug("Process cancels done: " + this);
            } catch (Exception ignore) {
                // TODO
                log.warn("Process cancels fails: " + this, ignore);
            }
        }
        // We hope that the process destroy will result below task return soon.
        try {
            return mProcessController.get();
        } catch (Exception e) {
            // To fix MATSUP-1134
            // No exception should be thrown in cancel()
            return new ProcessInfo(mProcessController.mBuilder, CANCELLED_VALUE, "", "");
        }
        /*
         * // Let the below tasks return in advance. // As mProcessWaitTask is the end of the stream, we complete it
         * first. mProcessController.mProcessWaitTask.complete(CANCELLED_VALUE); // Complete the middle stream tasks.
         * mProcessController.mStdoutTask.complete(CANCELLED); mProcessController.mErroutTask.complete(CANCELLED);
         */
    }

    /**
     * Wait for task finish and fetch the result.<br>
     * Any exception thrown during the process execution will cause this method throw the same exception and stop the
     * whole task.
     * <p>
     * <p>
     * If {@link #cancel()} is called, this method will also return immediately with cancellation value.
     * <p>
     * <p>
     * Remember to handle Timeout Exception if {@link Builder#execute()} is used.
     */
    public ProcessInfo getResult() throws FunctionException {
        try {
            return mProcessController.get();
        } catch (CompletionException e) {
            cancel();
            throw new FunctionException(4037, e.getCause(), e.getMessage(), mProcessController.mBuilder.mExpression);
        }
    }

    /**
     * Wait for task finish with given timeout (in ms) and fetch the result.<br>
     * Any exception thrown during the process execution will cause this method throw the same exception and stop the
     * whole task.
     * <p>
     * <p>
     * If {@link #cancel()} is called, this method will also return immediately with cancellation value.
     * <p>
     * <p>
     *
     * @param timeout in {@linkplain TimeUnit#MILLISECONDS}
     */
    public ProcessInfo getResult(long timeout) throws FunctionException {
        try {
            return mProcessController.get(timeout);
        } catch (CompletionException | ExecutionException e) {
            cancel();
            throw new FunctionException(4037, e.getCause(), e.getMessage(), mProcessController.mBuilder.mExpression);
        } catch (InterruptedException | TimeoutException e) {
            cancel();
            throw new FunctionException(4037, e, e.getMessage(), mProcessController.mBuilder.mExpression);
        }
    }

    interface OutputHandler {

        OutputHandler IGNORE = new Ignore();

        void record(String s);

        String get();

        OutputHandler forStdout();

        OutputHandler forErrout();

        default void init() {

        }

        class Ignore implements OutputHandler {

            @Override
            public void record(String s) {
            }

            @Override
            public String get() {
                return "";
            }

            @Override
            public OutputHandler forStdout() {
                return this;
            }

            @Override
            public OutputHandler forErrout() {
                return this;
            }
        }

        class ToString implements OutputHandler {

            private final StringBuilder mStringBuilder = new StringBuilder();

            @Override
            public void record(String s) {
                mStringBuilder.append(s);
            }

            @Override
            public String get() {
                return mStringBuilder.toString();
            }

            @Override
            public OutputHandler forStdout() {
                return this;
            }

            @Override
            public OutputHandler forErrout() {
                return new ToString();
            }
        }

        class ToFile extends ToString {

            private boolean mIsRecordOutputToString = false;
            private File mFile;
            private File mStdoutFile;
            private File mErroutFile;
            private String mFilePath;
            private PrintWriter mWriter;

            public ToFile setStdoutFile(File stdoutFile) {
                mStdoutFile = stdoutFile;
                return this;
            }

            public ToFile setErroutFile(File erroutFile) {
                mErroutFile = erroutFile;
                return this;
            }

            public ToFile setFile(File file) {
                mFile = file;
                return this;
            }

            ToFile isRecordOutputToString(boolean b) {
                mIsRecordOutputToString = b;
                return this;
            }

            @Override
            public void init() {
                PrintWriter printWriter;
                String filePath;
                try {
                    printWriter = new PrintWriter(new FileWriter(mFile.getAbsoluteFile(), true));
                    filePath = mFile.getCanonicalPath();
                } catch (IOException e) {
                    printWriter = null;
                    filePath = "Error file operation: " + e;
                }
                mWriter = printWriter;
                mFilePath = filePath;
            }

            @Override
            public void record(String s) {
                if (mIsRecordOutputToString) {
                    super.record(s);
                }
                if (null != mWriter) {
                    mWriter.println(s);
                    mWriter.flush();
                }
            }

            @Override
            public String get() {
                if (null != mWriter) {
                    mWriter.close();
                }
                if (mIsRecordOutputToString) {
                    return super.get();
                } else {
                    return mFilePath;
                }
            }

            @Override
            public OutputHandler forStdout() {
                return new ToFile().setFile(mStdoutFile).isRecordOutputToString(mIsRecordOutputToString);
            }

            @Override
            public OutputHandler forErrout() {
                return new ToFile().setFile(mErroutFile).isRecordOutputToString(mIsRecordOutputToString);
            }
        }
    }

    public static class Builder {

        public static final long WAIT_FOREVER = Long.MAX_VALUE;
        private final static DestroyHandler DEFAULT_DESTROY_HANDLER = new DestroyHandler() {

            @Override
            public void destroy(Process process) {
                process.destroyForcibly();
            }

            @Override
            public boolean isProcessAlive(Process process) {
                return process.isAlive();
            }
        };
        private List<String> mCommands;
        private Charset mCharset = Charset.forName("UTF-8");
        private long mWaitTime = COMMAND_TIMEOUT;
        /**
         * To release the resource when coming with something unexpected. The terminate time takes longer than normal
         * wait time.
         */
        private long mTerminateTime = WAIT_FOREVER;
        private boolean mEnableFlush = false;
        private boolean mIsRedirectErrorStream = false;
        private File mWorkDirectory;
        private Map<String, String> mEnv;
        private OutputHandler mHandlerType;
        private String mExpression = "";
        private File mWrittenFile;
        private DestroyHandler mDestroyHandler;

        Builder() {
        }

        /*
         * "pool-2-thread-2" #28 prio=5 os_prio=0 tid=0x0000000023b21800 nid=0x1110 runnable [0x000000002627e000]
         * java.lang.Thread.State: RUNNABLE at java.io.FileInputStream.readBytes(Native Method) at
         * java.io.FileInputStream.read(FileInputStream.java:255) at
         * java.io.BufferedInputStream.fill(BufferedInputStream.java:246) at
         * java.io.BufferedInputStream.read1(BufferedInputStream.java:286) at
         * java.io.BufferedInputStream.read(BufferedInputStream.java:345) - locked <0x000000077cff8bb0> (a
         * java.io.BufferedInputStream) at java.nio.channels.Channels$ReadableByteChannelImpl.read(Channels.java:385) -
         * locked <0x000000077cffad50> (a java.lang.Object) at
         * com.cienet.mats.node.task.NodeProcess$ProcessStrategy.recordInputStream(NodeProcess .java:550) at
         * com.cienet.mats.node.task.NodeProcess$ProcessStrategy.recordStdout(NodeProcess.java:527) at
         * com.cienet.mats.node.task.NodeProcess$ProcessStrategy.access$200(NodeProcess.java:388) at
         * com.cienet.mats.node.task.NodeProcess$ProcessStrategy$4.lambda$generateProcess$88 (NodeProcess.java:459) at
         * com.cienet.mats.node.task.NodeProcess$ProcessStrategy$4$$Lambda$30/1702481339.apply (Unknown Source) at
         * java.util.concurrent.CompletableFuture.uniApply(CompletableFuture.java:602) at
         * java.util.concurrent.CompletableFuture$UniApply.tryFire(CompletableFuture.java:577) at
         * java.util.concurrent.CompletableFuture.postComplete(CompletableFuture.java:474) at
         * java.util.concurrent.CompletableFuture$AsyncSupply.run(CompletableFuture.java:1595) at
         * java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1142) at
         * java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:617) at
         * java.lang.Thread.run(Thread.java:745)
         */
        private static void killInputStreamThread(Thread thread, InputStream inputStream, long time) {
            debug("killInputStreamThread starts");
            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
                debug("killInputStreamThread is interrupted");
            }
            debug("killInputStreamThread wakes up to kill");
            if (thread.isAlive()) {
                // It may cause ClosedByInterruptException
                thread.interrupt();
                debug("killInputStreamThread interrupt");
                try {
                    // It may cause ClosedChannelException
                    // TODO
                    // Someone can test whether ClosedByInterruptException can interrupt #read
                    // successfully or not . If yes, no need to close the inputStream any more.
                    inputStream.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
        }

        /**
         * @see ProcessBuilder#directory(File)
         */
        public Builder setWorkDirectory(File workDirectory) {
            mWorkDirectory = workDirectory;
            return this;
        }

        /**
         * @see ProcessBuilder#environment()
         */
        public Builder setEnv(Map<String, String> env) {
            mEnv = env;
            return this;
        }

        public Builder setCommand(String... commands) {
            return setCommand(Arrays.asList(commands));
        }

        public Builder setCommand(List<String> commands) {
            mCommands = commands;
            mExpression = String.join(" ", commands);
            return this;
        }

        public Builder setCommand(String commands) {
            return setCommand(split(commands));
        }

        /**
         * The decode way from byte to char.<br>
         * Default is UTF-8
         *
         * @see Charset#forName(String)
         */
        public Builder setDecode(String charsetName) {
            mCharset = Charset.forName(charsetName);
            return this;
        }

        public Builder setDestroyHandler(DestroyHandler destroyHandler) {
            mDestroyHandler = destroyHandler;
            return this;
        }

        /**
         * Flash all the stream
         *
         * @param flush
         * @return
         */
        public Builder enableFlush(boolean flush) {
            mEnableFlush = flush;
            return this;
        }

        /**
         * If this property is {@code true}, then any error output generated by subprocesses subsequently will be merged
         * with the standard output.
         *
         * @see ProcessBuilder#redirectErrorStream(boolean)
         */
        private Builder isRedirectErrorStream(boolean b) {
            mIsRedirectErrorStream = b;
            return this;
        }

        /**
         * Ignore the stdout and errout. Thus empty string will be got from {@link ProcessInfo}.<br>
         * It's a way to reduce system resources consumption.
         * <p>
         * This method will set {@link #isRedirectErrorStream(boolean)} to true.
         */
        public Builder ignoreOutput() {
            mHandlerType = OutputHandler.IGNORE;
            mIsRedirectErrorStream = true;
            return this;
        }

        /**
         * You can get all the stdout and errout from {@link ProcessInfo}.
         */
        public Builder recordOutputToResult() {
            mHandlerType = new OutputHandler.ToString();
            mIsRedirectErrorStream = false;
            return this;
        }

        /**
         * Output to both the file and {@link ProcessInfo}.<br>
         * This method will set {@link #isRedirectErrorStream(boolean)} to false. Thus you can get splitted stdout and
         * errout.
         *
         * @see #mergeOutputToFile(File)
         */
        public Builder recordOutputToStringAndFile(File stdoutFile, File erroutFile) {
            mHandlerType = new OutputHandler.ToFile().setStdoutFile(stdoutFile).setErroutFile(erroutFile)
                    .isRecordOutputToString(true);
            mIsRedirectErrorStream = false;
            return this;
        }

        /**
         * Output to both the file and {@link ProcessInfo}.<br>
         * This method will set {@link #isRedirectErrorStream(boolean)} to false. Thus you can get splitted stdout and
         * errout from {@link ProcessInfo}. However, you may find the content in file are disordered.
         *
         * @see #mergeOutputToFile(File)
         */
        public Builder recordOutputToStringAndSameFile(File file) {
            mHandlerType = new OutputHandler.ToFile().setStdoutFile(file).setErroutFile(file)
                    .isRecordOutputToString(true);
            mIsRedirectErrorStream = false;
            return this;
        }

        /**
         * Same behavior with {@link #recordOutputToResult}. However, write the {@link ProcessInfo} into file after this
         * command is successfully executed.
         * <p>
         * Notice that, it will cache all the output of this command into {@link ProcessInfo} till done. If your
         * command's output is huge, please use {@link #recordOutputToStringAndFile(File, File)} or
         * {@link #recordOutputToStringAndSameFile(File)} instead.
         */
        public Builder recordOutputToStringThenWriteToFile(File file) {
            mWrittenFile = file;
            return recordOutputToResult();
        }

        /**
         * Output to file and {@link ProcessInfo}.<br>
         * This method will set {@link #isRedirectErrorStream(boolean)} to true. Thus you will find stdout and errout
         * appear in the same file with arbitrary sequence.<br>
         * And you will get the same content as shown in file from {@link ProcessInfo#getStdout()} and nothing from
         * {@link ProcessInfo#getErrout()}
         *
         * @see #recordOutputToStringAndFile(File, File)
         */
        public Builder mergeOutputToResultAndFile(File file) {
            mHandlerType = new OutputHandler.ToFile().setFile(file).isRecordOutputToString(true);
            mIsRedirectErrorStream = true;
            return this;
        }

        /**
         * When recording output to file, you can get nothing from {@link ProcessInfo}.
         * <p>
         * This method will set {@link #isRedirectErrorStream(boolean)} to true. Thus you will find stdout and errout
         * appear in the same file with arbitrary sequence.
         */
        public Builder mergeOutputToFile(File file) {
            mHandlerType = new OutputHandler.ToFile().setFile(file);
            mIsRedirectErrorStream = true;
            return this;
        }

        NodeProcess executeTestInputKill() {
            mWaitTime = 10000L;
            mTerminateTime = 5000L;
            mIsRedirectErrorStream = false;
            ProcessController pc = new ProcessController(this);
            pc.mProcessTask = CompletableFuture.supplyAsync(() -> {
                try {
                    return generateProcessTask();
                } catch (FunctionException e) {
                    throw new CompletionException(e);
                }
            }, sCachedThreadPool);
            return new NodeProcess(appendFollowingJobs(pc), DEFAULT_DESTROY_HANDLER);
        }

        private ProcessController appendFollowingJobs(ProcessController pc) {
            if (mEnableFlush) {
                pc.mProcessTask.thenAcceptAsync(this::flush, sCachedThreadPool);
            }
            pc.mStdoutChannel = pc.mProcessTask.thenApplyAsync(it -> {
                info(it + " start to record stdout");
                return getChannel(it.getInputStream());
            }, sCachedThreadPool);
            if (null == mHandlerType) {
                mHandlerType = new OutputHandler.ToString();
            }
            if (mIsRedirectErrorStream) {
                pc.mStdoutTask = pc.mStdoutChannel.thenApply(it -> recordInputStream(it, mHandlerType));
            } else {
                pc.mStdoutTask = pc.mStdoutChannel.thenApply(it -> recordInputStream(it, mHandlerType.forStdout()));
                pc.mErroutChannel = pc.mProcessTask.thenApplyAsync(it -> {
                    info(it + " start to record errout");
                    return getChannel(it.getErrorStream());
                }, sCachedThreadPool);
                pc.mErroutTask = pc.mErroutChannel.thenApply(it -> recordInputStream(it, mHandlerType.forErrout()));
            }
            // It should be run async way.
            // Otherwise, mStdoutTask/mErroutTask will be blocked ...
            // I don't know why this below function will block the async threads ...
            pc.mProcessWaitTask = pc.mProcessTask.thenApplyAsync(this::waitForProcess, sCachedThreadPool);
            return pc;
        }

        /**
         * Default wait time is {@link NodeProcess#COMMAND_TIMEOUT}
         */
        public Builder setWaitTime(long waitTime) {
            mWaitTime = waitTime;
            return this;
        }

        private void init() {
            if (null == sCachedThreadPool) {
                // It should not happen!!!
                throw new RuntimeException("Forget to call NodeProcess.startProcessPool first");
            }
            mTerminateTime = mWaitTime + 100L;
        }

        /**
         * Not allow forever waiting
         */
        public NodeProcess execute() {
            init();
            ProcessController pc = new ProcessController(this);
            pc.mProcessTask = CompletableFuture.supplyAsync(() -> {
                try {
                    return generateProcessTask();
                } catch (FunctionException e) {
                    throw new CompletionException(e);
                }
            }, sCachedThreadPool);
            return new NodeProcess(appendFollowingJobs(pc),
                    mDestroyHandler == null ? DEFAULT_DESTROY_HANDLER : mDestroyHandler);
        }

        /**
         * To fix CNTMAT-8678 Throw system error when to create such a process.
         */
        public NodeProcess executeWithSystemExecutionErrorCheck() throws FunctionException {
            init();
            ProcessController pc = new ProcessController(this);
            Process process = generateProcessTask();
            pc.mProcessTask = CompletableFuture.supplyAsync(() -> process, sCachedThreadPool);
            return new NodeProcess(appendFollowingJobs(pc),
                    mDestroyHandler == null ? DEFAULT_DESTROY_HANDLER : mDestroyHandler);
        }

        private Process generateProcessTask() throws FunctionException {
            info("startProcess: " + mCommands);
            ProcessBuilder builder = new ProcessBuilder(mCommands).redirectErrorStream(mIsRedirectErrorStream);
            if (null != mWorkDirectory) {
                builder.directory(mWorkDirectory);
            }
            if (null != mEnv) {
                builder.environment().putAll(mEnv);
            }
            Process process;
            try {
                process = builder.start();
            } catch (IOException e) {
                throw new FunctionException(3501, e, e.getMessage(), null);
            }
            info(process + ": (" + mCommands + ") has been started successfully.");
            return process;
        }

        private void flush(Process process) {
            long startTime = System.nanoTime();
            long rem = TimeUnit.MILLISECONDS.toNanos(mWaitTime);
            do {
                if (!process.isAlive()) {
                    return;
                }
                OutputStream outputStream = process.getOutputStream();
                try {
                    outputStream.write("\r\n".getBytes());
                    outputStream.flush();
                } catch (IOException ignore) {
                    log.debug("InterruptedException, ignore", ignore);
                    // ignore
                }
                if (rem > 0) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ignore) {
                        log.debug("InterruptedException, ignore", ignore);
                        // ignore
                    }
                }
            } while (rem - (System.nanoTime() - startTime) > 0);
        }

        /**
         * {@link Process#waitFor(long, TimeUnit)} can response to {@link Thread#interrupt()} and thrown
         * {@link InterruptedException}
         */
        private int waitForProcess(Process process) {
            boolean wait;
            info(process + ": is to wait at time: " + mWaitTime);
            try {
                if (Builder.WAIT_FOREVER == mWaitTime) {
                    process.waitFor();
                    wait = true;
                } else {
                    wait = process.waitFor(mWaitTime, TimeUnit.MILLISECONDS);
                }
            } catch (InterruptedException e) {
                info(process + ": is interrupted.");
                return CANCELLED_VALUE;
            }
            if (wait) {
                info(process + ": is finished with exitValue " + process.exitValue());
                return process.exitValue();
            } else {
                info(process + ": is timeout " + mWaitTime);
                throw new CompletionException(new Exception("Timeout: " + mWaitTime));
            }
        }

        private ReadableByteChannel getChannel(InputStream inputStream) {
            // Use direct buffer to provide more effective I/O.
            // However, care about the release of native memory.
            // ByteBuffer directBuffer = ByteBuffer.allocateDirect(256);
            // ((DirectBuffer)directBuffer).cleaner().clean()
            /*
             * The buffer size for caching inputStream.
             */
            return Channels.newChannel(inputStream);
        }

        /**
         * {@link ReadableByteChannel#read(ByteBuffer)} can response to {@link Thread#interrupt()} and thrown
         * {@link ClosedByInterruptException}
         */
        private String recordInputStream(ReadableByteChannel channel, OutputHandler handler) {
            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
            handler.init();
            int readEnd;
            // Find that read will block the thread.
            // So we have to interrupt it with another thread
            // final Thread thread = Thread.currentThread();
            // CompletableFuture.runAsync(
            // () -> killInputStreamThread(thread, inputStream, mTerminateTime),
            // sCachedThreadPool);
            // It turns out to be a fool strategy which this thread may sleep for a long time.
            // We use channel#close instead.
            final long startTime = System.nanoTime();
            final long rem = TimeUnit.MILLISECONDS.toNanos(mTerminateTime);
            do {
                try {
                    readEnd = channel.read(buffer);
                } catch (ClosedByInterruptException e) {
                    // It's caused by intended cancellation
                    debug("inputStream come with: " + e);
                    break;
                } catch (ClosedChannelException e) {
                    // It's caused by intended close
                    debug("inputStream come with: " + e);
                    break;
                } catch (Exception e) {
                    // throw new CompletionException(e);
                    // Ignore the output error
                    debug("inputStream come with: " + e);
                    break;
                }
                if (-1 == readEnd) {
                    break;
                }
                // Flip to read mode to decode
                buffer.flip();
                handler.record(mCharset.decode(buffer).toString());
                // Clear
                buffer.clear();
            } while (rem - (System.nanoTime() - startTime) >= 0 && !Thread.currentThread().isInterrupted());
            try {
                channel.close();
            } catch (IOException ignore) {
                log.debug("IOException, ignore", ignore);
                // ignore
            }
            return handler.get();
        }
    }

    /**
     * {@link #mProcessWaitTask} always runs after {@link #mStdoutTask} and {@link #mErroutTask} complete.<br>
     * {@link #mProcessTask} will throw IOException.<br>
     * {@link #mStdoutTask} and {@link #mErroutTask} will NOT throw exception.<br>
     * {@link #mProcessWaitTask} will throw timeout exception.
     */
    private static class ProcessController {

        private static final CompletableFuture<String> sDefault = CompletableFuture.completedFuture("");
        private static final CompletableFuture<ReadableByteChannel> sNull = CompletableFuture.completedFuture(null);
        final Builder mBuilder;
        CompletableFuture<Process> mProcessTask;
        CompletableFuture<Integer> mProcessWaitTask;
        CompletableFuture<String> mStdoutTask = sDefault;
        CompletableFuture<ReadableByteChannel> mStdoutChannel = sNull;
        CompletableFuture<String> mErroutTask = sDefault;
        CompletableFuture<ReadableByteChannel> mErroutChannel = sNull;

        ProcessController(Builder builder) {
            mBuilder = builder;
        }

        /**
         * We don't cancel task in our code. So only {@link CompletionException} will be thrown.
         */
        ProcessInfo get() {
            ProcessInfo processInfo = new ProcessInfo(mBuilder, mProcessWaitTask.join(), mStdoutTask.join(),
                    mErroutTask.join());
            if (mBuilder.mWrittenFile != null) {
                try (Writer fileWriter = new BufferedWriter(new FileWriter(mBuilder.mWrittenFile))) {
                    fileWriter.write(processInfo.toString());
                    fileWriter.flush();
                } catch (IOException e) {
                    log.warn("Can not write process result into file: " + mBuilder.mWrittenFile, e);
                }
            }
            return processInfo;
        }

        /**
         * Wait given timeout (in ms).
         */
        ProcessInfo get(long timeout) throws InterruptedException, ExecutionException, TimeoutException {
            ProcessInfo processInfo = new ProcessInfo(mBuilder, mProcessWaitTask.get(timeout, TimeUnit.MILLISECONDS),
                    mStdoutTask.get(timeout, TimeUnit.MILLISECONDS), mErroutTask.get(timeout, TimeUnit.MILLISECONDS));
            if (mBuilder.mWrittenFile != null) {
                try (Writer fileWriter = new BufferedWriter(new FileWriter(mBuilder.mWrittenFile))) {
                    fileWriter.write(processInfo.toString());
                    fileWriter.flush();
                } catch (IOException e) {
                    log.warn("Can not write process result into file: " + mBuilder.mWrittenFile, e);
                }
            }
            return processInfo;
        }

        boolean isDone() {
            return mProcessWaitTask.isDone() && mStdoutTask.isDone() && mErroutTask.isDone();
        }
    }

    /**
     * You can always get {@link ProcessInfo#getExitVal()}.<br>
     * You will get {@code ""} from {@link ProcessInfo#getErrout()} if {@link Builder#isRedirectErrorStream(boolean)} is
     * used.<br>
     * You will get {@code ""} both from {@link ProcessInfo#getStdout()} and {@link ProcessInfo#getErrout()} if
     * {@link OutputHandler#IGNORE} is used.<br>
     */
    public static class ProcessInfo {

        private final Builder mBuilder;
        private final int mExitVal;
        private final String mStdout;
        private final String mErrout;
        private final String mExpression;

        ProcessInfo(Builder builder, int exitVal, String stdout, String errout) {
            mBuilder = builder;
            mExitVal = exitVal;
            mStdout = stdout;
            mErrout = errout;
            StringBuilder b = new StringBuilder();
            b.append("<<Command>>: ").append(builder.mExpression).append(System.lineSeparator());
            b.append("<<ExitVal>>: ").append(exitVal).append(System.lineSeparator());
            b.append("<<StdOut>>: ").append(System.lineSeparator()).append(stdout.trim())
                    .append(System.lineSeparator());
            b.append("<<ErrOut>>: ").append(System.lineSeparator()).append(errout.trim());
            mExpression = b.toString();
        }

        @Override
        public String toString() {
            return mExpression;
        }

        public int getExitVal() {
            return mExitVal;
        }

        public String getStdout() {
            return mStdout;
        }

        public String getErrout() {
            return mErrout;
        }

        public String getRawCommand() {
            return mBuilder.mExpression;
        }

        public boolean hasRecordToFile() {
            return mBuilder.mHandlerType instanceof OutputHandler.ToFile || mBuilder.mWrittenFile != null;
        }
    }
}
