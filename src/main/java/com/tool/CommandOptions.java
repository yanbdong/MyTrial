package com.tool;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import io.reactivex.rxjava3.annotations.Nullable;

/**
 * @author yanbdong@cienet.com.cn
 * @since Sep 09, 2020
 */
class CommandOptions {

    private static final Options options = Arrays.stream(Type.values()).map(it -> it.mOption).reduce(new Options(),
            Options::addOption, (l, r) -> l);
    private final LoadingCache<Type, Object> mCache;

    CommandOptions(LoadingCache<Type, Object> cache) {
        mCache = cache;
    }

    public static CommandOptions init(String... args) throws ParseException {
        CommandLine commandLine = new DefaultParser().parse(options, args);
        if (Type.HELP.exist(commandLine)) {
            String header = "Fun\n\n";
            String footer = "\nPlease report issues to CATS";
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("sc", header, options, footer, true);
            System.exit(0);
        }
        return new CommandOptions(CacheBuilder.newBuilder().build(new CacheLoader<Type, Object>() {

            @Override
            public Object load(Type key) throws Exception {
                return key.getValue(commandLine);
            }
        }));
    }

    @Nullable
    @SuppressWarnings("unchecked")
    <T> T getValue(Type type) {
        try {
            return (T) mCache.get(type);
        } catch (ExecutionException e) {
            return null;
        }
    }
}
