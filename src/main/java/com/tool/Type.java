package com.tool;

import com.google.common.base.Strings;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

/**
 * @author yanbdong@cienet.com.cn
 * @since Sep 09, 2020
 */
enum Type {
    /**
     *
     */
    USER(Option.builder("user").desc("user name").hasArg(), s -> Strings.isNullOrEmpty(s) ? "root" : s),
    /**
     *
     */
    PASSWORD(Option.builder("password").desc("password").hasArg(), s -> Strings.isNullOrEmpty(s) ? "" : s),
    /**
     *
     */
    IP(Option.builder("ip").desc("ip").hasArg(), s -> Strings.isNullOrEmpty(s) ? "172.16.4.101" : s),
    /**
     *
     */
    PORT(Option.builder("port").desc("port").hasArg(), s -> Strings.isNullOrEmpty(s) ? 22 : Integer.parseInt(s)),
    /**
     *
     */
    NUMBER(Option.builder("number").desc("number of images to capture").hasArg(),
            s -> Strings.isNullOrEmpty(s) ? 5 : Integer.parseInt(s)),
    /**
     *
     */
    ROUND(Option.builder("round").hasArg().desc("Round to run"),
            s -> Strings.isNullOrEmpty(s) ? 1 : Integer.parseInt(s)),
    /**
     *
     */
    REMOTE_FOLDER(Option.builder("remoteFolder").desc("folder to save image on QNX").hasArg(),
            s -> Strings.isNullOrEmpty(s) ? "/usr/share/misc/mats" : s.endsWith("/") ? s + "mats" : s + "/mats"),
    /**
     *
     */
    LOCAL_FOLDER(Option.builder("localFolder").desc("folder to save image on local").hasArg(), s -> {
        String p = Strings.isNullOrEmpty(s) ? System.getProperty("user.dir") : s;
        return Paths.get(p, "mats", DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss").format(LocalDateTime.now()))
                .toFile();
    }),
    /**
     *
     */
    DISPLAY_ID(Option.builder("displayId").desc("Display Id").hasArg()),
    /**
     *
     */
    X(Option.builder("X").desc("Display Id").hasArg(), s -> Strings.isNullOrEmpty(s) ? 0 : Integer.parseInt(s)),
    /**
     *
     */
    Y(Option.builder("Y").desc("Display Id").hasArg(), s -> Strings.isNullOrEmpty(s) ? 0 : Integer.parseInt(s)),
    /**
     *
     */
    CSV_ID(Option.builder("csvId").desc("The identity of CSV").hasArg(), s -> Strings.isNullOrEmpty(s) ? "Default" : s),
    /**
     *
     */
    SV(Option.builder("sv").desc("The software version of CSV").hasArg(),
            s -> Strings.isNullOrEmpty(s) ? "Default" : s),
    /**
     *
     */
    DELETE(Option.builder("delete").desc("Delete local image")),
    /**
     *
     */
    HELP(Option.builder("h").desc("Display this message"));

    final Option mOption;
    final Function<String, ?> mParser;

    Type(Option.Builder builder, Function<String, ?> parser) {
        mOption = builder.build();
        mParser = parser;
    }

    Type(Option.Builder builder) {
        this(builder, it -> "");
    }

    boolean exist(CommandLine commandLine) {
        return commandLine.hasOption(mOption.getOpt());
    }

    Object getValue(CommandLine commandLine) {
        if (!mOption.hasArg()) {
            return exist(commandLine);
        }
        return mParser.apply(commandLine.getOptionValue(mOption.getOpt()));
    }

}
