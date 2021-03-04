package com.process.redirect;

import java.io.File;
import java.io.IOException;

/**
 * @author yanbdong@cienet.com.cn
 * @since Feb 24, 2021
 */
public class Main {

    public static void main(String[] args) throws IOException, FunctionException {
//        ProcessBuilder builder = new ProcessBuilder(Arrays.asList("echo", "devices", ">", "/Users/mats/Desktop/r1.txt"));
////        builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
//        builder.redirectOutput(ProcessBuilder.Redirect.to(new File("/Users/mats/Desktop", "redirect.txt")));
//        builder.start();
        yes();
    }

    public static void yes() throws FunctionException {
        NodeProcess.startProcessPool();
        try {

            NodeProcess.ProcessInfo info = NodeProcess.build().setCommand("yes", "dlkjsfl;dsjf'ja;ldsfjalkfdjieoajdkljfakdljfalkdjfal;dkfjakl;fjakl;dfja;lkdfja;kldfjakl;dfj")
                    .mergeOutputToFile(new File("/Users/mats/Desktop", "redirect.txt")).execute().getResult(100000);
        } finally {
            NodeProcess.shutdownProcessPool();
        }
    }
}
