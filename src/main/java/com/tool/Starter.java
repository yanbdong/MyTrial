package com.tool;

import com.google.common.base.Strings;

import org.apache.commons.cli.ParseException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import ch.ethz.ssh2.Connection;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleSource;
import io.reactivex.rxjava3.functions.BiConsumer;
import io.reactivex.rxjava3.functions.BiFunction;
import io.reactivex.rxjava3.functions.Function3;
import io.reactivex.rxjava3.functions.Function4;
import io.vavr.CheckedFunction2;
import lombok.extern.slf4j.Slf4j;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuple4;
import reactor.util.function.Tuples;

/**
 * @author yanbdong@cienet.com.cn
 * @since Sep 03, 2020
 */
@Slf4j
public class Starter {
    /**
     * Return remote file base name and screenshot time cost
     */
    static CheckedFunction2<Connection, CommandOptions, Tuple2<String, Float>> screenshotFunction = (connection,
            commandOptions) -> {
        String fileName = DateTimeFormatter.ofPattern("HH_mm_ss_SSS").format(LocalDateTime.now()) + ".png";
        String cmd = "screenshot -file=" + commandOptions.getValue(Type.REMOTE_FOLDER) + fileName
                + (Strings.isNullOrEmpty(commandOptions.getValue(Type.DISPLAY_ID)) ? ""
                        : (" -display=" + commandOptions.getValue(Type.DISPLAY_ID)))
                + (Integer.compare(0, commandOptions.getValue(Type.X)) == 0 ? ""
                        : (" -size=" + commandOptions.getValue(Type.X) + "X" + commandOptions.getValue(Type.Y)));
        Tuple4<Integer, String, String, Float> result = SshUtil.execCommand(connection, cmd);
        return Tuples.of(fileName, result.getT4());
    };
    /**
     * Return local file name and pull time cost. Delete it if need
     */
    static Function4<Connection, CommandOptions, File, String, Tuple2<File, Float>> pullFunction = (connection,
            commandOptions, localSaveFolder, remoteFileBaseName) -> {
        File localFilePath = new File(localSaveFolder, remoteFileBaseName);
        float timeCost = SshUtil.scpGet(connection, commandOptions.getValue(Type.REMOTE_FOLDER) + remoteFileBaseName,
                localFilePath.toString());
        if (commandOptions.getValue(Type.DELETE)) {
            localFilePath.delete();
        }
        return Tuples.of(localFilePath, timeCost);
    };
    /**
     * Record data to file
     */
    static BiConsumer<File, Iterable<Tuple3<String, Float, Float>>> recordFunction = (localSaveFile, source) -> {
        try (CSVPrinter printer = CSVFormat.DEFAULT.withHeader("Local File", "Screenshot Time Cost", "Pull Time Cost")
                .print(new BufferedWriter(new FileWriter(localSaveFile)))) {
            List<Float> screenshotTimeCost = new LinkedList<>();
            List<Float> pullTimeCost = new LinkedList<>();
            for (Tuple3<String, Float, Float> tuple : source) {
                printer.printRecord(tuple.toArray());
                screenshotTimeCost.add(tuple.getT2());
                pullTimeCost.add(tuple.getT3());
            }
            printer.printRecord("Average",
                    screenshotTimeCost.stream().collect(Collectors.averagingDouble(Float::doubleValue)),
                    pullTimeCost.stream().collect(Collectors.averagingDouble(Float::doubleValue)));
            printer.printRecord("Min", screenshotTimeCost.stream().min(Float::compareTo).get(),
                    pullTimeCost.stream().min(Float::compareTo).get());
            printer.printRecord("Max", screenshotTimeCost.stream().max(Float::compareTo).get(),
                    pullTimeCost.stream().max(Float::compareTo).get());
        } finally {
            log.info("Record to file {}", localSaveFile);
        }
    };
    /**
     * Record data to file
     */
    static BiConsumer<File, Observable<Tuple3<String, Float, Float>>> recordFunction1 = (localSaveFile,
            source) -> Single.using(
                    () -> CSVFormat.DEFAULT.withHeader("Local File", "Screenshot Time Cost", "Pull Time Cost")
                            .print(new BufferedWriter(new FileWriter(localSaveFile))),
                    printer -> source.doOnNext(it -> printer.printRecord(it.toArray()))
                            .reduce(Tuples.of(new CollectData(), new CollectData()), (collector, data) -> collector
                                    .mapT1(it -> it.accept(data.getT2())).mapT2(it -> it.accept(data.getT3())))
                            .doOnSuccess(it -> {
                                printer.printRecord("Average", it.getT1().average(), it.getT2().average());
                                printer.printRecord("Min", it.getT1().min, it.getT2().min);
                                printer.printRecord("Max", it.getT1().max, it.getT2().max);
                            }).doOnTerminate(() -> log.info("Record to file {}", localSaveFile)),
                    CSVPrinter::close).subscribe();

    static Function3<Connection, CommandOptions, File, Single<List<Tuple3<String, Float, Float>>>> executeCapture = (
            connection, commandOptions,
            localSaveFolder) -> Observable.range(1, commandOptions.getValue(Type.NUMBER)).doOnSubscribe(ignore -> {
                SshUtil.execCommand(connection, "rm -r " + commandOptions.getValue(Type.REMOTE_FOLDER));
                SshUtil.execCommand(connection, "mkdir -p " + commandOptions.getValue(Type.REMOTE_FOLDER));
            }).map(ignore -> screenshotFunction.apply(connection, commandOptions)).collect(Collectors.toList())
                    .flatMapObservable(Observable::fromIterable).map(it -> {
                        Tuple2<File, Float> tuple2 = pullFunction.apply(connection, commandOptions, localSaveFolder,
                                it.getT1());
                        return Tuples.of(tuple2.getT1().getAbsolutePath(), it.getT2(), tuple2.getT2());
                    })
                    .doOnTerminate(() -> SshUtil
                            .execCommand(connection, "rm -r " + commandOptions.getValue(Type.REMOTE_FOLDER)))
                    .collect(Collectors.toList())
                    .doOnSuccess(it -> recordFunction.accept(new File(localSaveFolder,
                            commandOptions.getValue(Type.CSV_ID) + "_" + commandOptions.getValue(Type.SV) + ".csv"),
                            it));

    static BiFunction<Connection, CommandOptions, SingleSource<?>> executeRound = (connection,
            commandOptions) -> Observable.range(1, commandOptions.getValue(Type.ROUND))
                    .doOnNext(it -> log.info("Round {}", it))
                    .map(it -> new File((File) commandOptions.getValue(Type.LOCAL_FOLDER), "" + it))
                    .doOnNext(File::mkdirs)
                    .concatMap(file -> executeCapture.apply(connection, commandOptions, file)
                            .flatMapObservable(Observable::fromIterable))
                    .collect(Collectors.toList()).doOnSuccess(ignore -> log.info("Finish"))
                    .doOnSuccess(it -> recordFunction.accept(new File((File) commandOptions.getValue(Type.LOCAL_FOLDER),
                            commandOptions.getValue(Type.CSV_ID) + "_" + commandOptions.getValue(Type.SV) + ".csv"),
                            it));

    public static void main(String[] args) throws ParseException {
        CommandOptions commandOptions = CommandOptions.init(args);
        Single.using(() -> SshUtil.sshConnect(commandOptions), it -> executeRound.apply(it, commandOptions),
                Connection::close, false)
                .doOnSubscribe(ignore -> ((File) commandOptions.getValue(Type.LOCAL_FOLDER)).mkdirs()).subscribe();
    }

    static class CollectData {
        long count = 0L;
        float sum = 0F;
        float min = Float.MIN_VALUE;
        float max = Float.MAX_VALUE;

        CollectData accept(float data) {
            count++;
            sum += data;
            min = data < min ? data : min;
            max = data > max ? data : max;
            return this;
        }

        float average() {
            return sum / count;
        }
    }
}
