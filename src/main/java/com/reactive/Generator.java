package com.reactive;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleSource;
import io.reactivex.rxjava3.functions.Function3;
import io.reactivex.rxjava3.functions.Function4;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yanbdong@cienet.com.cn
 * @since Sep 04, 2020
 */
@Slf4j
class Generator {

    @SneakyThrows
    public static void main(String[] args) {
        // Observable.create(it -> {
        // Array.range(1, 10).forEach(i -> {
        // log.info("emit");
        // it.onNext(it);
        // });
        // it.onComplete();
        // }).timestamp(Schedulers.computation()).subscribe(it -> {
        // log.info(it.toString());
        // Thread.sleep(1000L);
        // });
        // Thread.sleep(100000L);
        t();
    }

    private static void t() {
        String folder = "/";
        int number = 10;
        String localFolder = "local";
        Function3<H, String, Integer, Single<List<String>>> screenshotFunction = (session, baseFolder,
                numberToCapture) -> Observable
                        .range(1, numberToCapture).timestamp(Schedulers.io()).map(it -> baseFolder
                                + DateTimeFormatter.ISO_INSTANT.format(Instant.ofEpochMilli(it.time())) + ".bmp")
                        .doOnNext(it -> {
                            log.info("screenshot -file=" + it);
                            Thread.sleep(1000L);
                        }).collect(Collectors.toList());
        Function4<H, String, Integer, String, SingleSource<?>> c = (connection, baseFolder, numberToCapture,
                localBaseFolder) -> Single.using(() -> connection,
                        it -> screenshotFunction.apply(it, baseFolder, numberToCapture).doOnSuccess(list -> {
                            for (String remoteFile : list) {
                                log.info("copy");
                            }
                        }), H::close, false);
        Single.using(H::new, it -> c.apply(it, folder, number, localFolder), H::close, false).subscribe();
    }

    public static class H implements AutoCloseable {

        @Override
        public void close() throws Exception {
            log.warn("Close " + this);
        }
    }
}
