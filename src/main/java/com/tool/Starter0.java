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

import ch.ethz.ssh2.Connection;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.functions.BiFunction;
import io.reactivex.rxjava3.functions.Function3;
import io.reactivex.rxjava3.functions.Function4;
import io.vavr.CheckedFunction2;
import lombok.extern.slf4j.Slf4j;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple4;
import reactor.util.function.Tuples;

/**
 * @author yanbdong@cienet.com.cn
 * @since Sep 03, 2020
 */
@Slf4j
public class Starter0 {
    /**
     * Return remote file base name and screenshot time cost
     */
    static CheckedFunction2<Connection, CommandOptions, Tuple2<String, Float>> screenshotFunction = (connection,
            commandOptions) -> {
        String fileName = DateTimeFormatter.ofPattern("HH_mm_ss_SSS").format(LocalDateTime.now()) + ".png";
        String cmd = "screenshot -file=" + commandOptions.getValue(Type.REMOTE_FOLDER) + fileName
                + (Strings.isNullOrEmpty(commandOptions.getValue(Type.DISPLAY_ID)) ? ""
                        : (" -display=" + commandOptions.getValue(Type.DISPLAY_ID)))
                + (0 == (int) commandOptions.getValue(Type.X) ? ""
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

    static BiFunction<CommandOptions, File, CSVPrinter> createCSVPrinter = ((commandOptions,
            localFolder) -> CSVFormat.DEFAULT.withHeader("Local File", "Screenshot Time Cost", "Pull Time Cost")
                    .print(new BufferedWriter(new FileWriter(new File(localFolder,
                            commandOptions.getValue(Type.CSV_ID) + "_" + commandOptions.getValue(Type.SV) + ".csv")))));

    private static Function4<Connection, CommandOptions, File, CSVPrinter, Single<Tuple2<CollectData, CollectData>>> executeCapture = (
            connection, commandOptions, localFile,
            globalCsvPrinter) -> Observable.range(0, commandOptions.getValue(Type.NUMBER))
                    .doOnSubscribe(ignore -> SshUtil.execCommand(connection,
                            "mkdir -p " + commandOptions.getValue(Type.REMOTE_FOLDER)))
                    .doAfterTerminate(() -> SshUtil.execCommand(connection,
                            "rm -r " + commandOptions.getValue(Type.REMOTE_FOLDER)))
                    .map(ignore -> screenshotFunction.apply(connection, commandOptions))
                    // Hold till capture done
                    .window((int) commandOptions.getValue(Type.NUMBER)).map(ob -> ob.map(tuple -> {
                        Tuple2<File, Float> t2 = pullFunction.apply(connection, commandOptions, localFile,
                                tuple.getT1());
                        return Tuples.of(t2.getT1(), tuple.getT2(), t2.getT2());
                    }))
                    .map(it -> Single.using(() -> createCSVPrinter.apply(commandOptions, localFile),
                            csvPrinter -> it.doOnNext(date -> csvPrinter.printRecord(date.toArray()))
                                    .doOnNext(date -> globalCsvPrinter.printRecord(date.toArray()))
                                    .reduce(Tuples.of(new CollectData(), new CollectData()),
                                            (collector, data) -> collector
                                                    .mapT1(captureTimeCost -> captureTimeCost.accept(data.getT2()))
                                                    .mapT2(pullTimeCost -> pullTimeCost.accept(data.getT3())))
                                    .doOnSuccess(collectData -> {
                                        csvPrinter.printRecord("Average", collectData.getT1().average(),
                                                collectData.getT2().average());
                                        csvPrinter.printRecord("Min", collectData.getT1().min, collectData.getT2().min);
                                        csvPrinter.printRecord("Max", collectData.getT1().max, collectData.getT2().max);
                                    }).doOnTerminate(() -> log.info("Record to file {}", localFile)),
                            CSVPrinter::close))
                    .blockingFirst();

    private static Function3<Connection, CommandOptions, CSVPrinter, Single<?>> m = (connection, commandOptions,
            globalCsvPrinter) -> Observable.range(1, commandOptions.getValue(Type.ROUND)).doOnSubscribe(ignore -> {
                log.info("Clean workspace on remote");
                SshUtil.execCommand(connection, "rm -r " + commandOptions.getValue(Type.REMOTE_FOLDER));
            }).doOnNext(round -> log.info("Round {}", round))
                    // Create sub-folder
                    .map(round -> new File((File) commandOptions.getValue(Type.LOCAL_FOLDER), "" + round))
                    .doOnNext(File::mkdirs)
                    .map(file -> executeCapture.apply(connection, commandOptions, file, globalCsvPrinter).blockingGet())
                    .reduce(Tuples.of(new CollectData(), new CollectData()),
                            (collector, data) -> collector
                                    .mapT1(captureTimeCost -> captureTimeCost.accept(data.getT1().min, data.getT1().max,
                                            data.getT1().average()))
                                    .mapT2(pullTimeCost -> pullTimeCost.accept(data.getT2().min, data.getT2().max,
                                            data.getT2().average())))
                    .doOnSuccess(collectData -> {
                        globalCsvPrinter.printRecord("Average", collectData.getT1().average(),
                                collectData.getT2().average());
                        globalCsvPrinter.printRecord("Min", collectData.getT1().min, collectData.getT2().min);
                        globalCsvPrinter.printRecord("Max", collectData.getT1().max, collectData.getT2().max);
                    }).doOnTerminate(
                            () -> log.info("Record to file {}", (File) commandOptions.getValue(Type.LOCAL_FOLDER)));

    public static void main(String[] args) throws ParseException {
        CommandOptions commandOptions = CommandOptions.init(args);
        ((File) commandOptions.getValue(Type.LOCAL_FOLDER)).mkdirs();
        Single.using(() -> SshUtil.sshConnect(commandOptions),
                connection -> Single.using(
                        () -> createCSVPrinter.apply(commandOptions, commandOptions.getValue(Type.LOCAL_FOLDER)),
                        it -> m.apply(connection, commandOptions, it), CSVPrinter::close),
                Connection::close).subscribe();
    }

    public static void main1(String[] args) throws ParseException {
        CommandOptions commandOptions = CommandOptions.init(args);
        Single.using(() -> SshUtil.sshConnect(commandOptions),
                connection -> Single
                        .using(() -> createCSVPrinter.apply(commandOptions, commandOptions.getValue(Type.LOCAL_FOLDER)),
                                it -> m.apply(connection, commandOptions, it), CSVPrinter::close)
                        .doOnSubscribe(ignore -> ((File) commandOptions.getValue(Type.LOCAL_FOLDER)).mkdirs()),
                Connection::close).subscribe();
    }

    static class CollectData {
        long count = 0L;
        float sum = 0F;
        float min = Float.MIN_VALUE;
        float max = Float.MAX_VALUE;

        CollectData accept(float data) {
            count++;
            sum += data;
            min = Math.min(data, min);
            max = Math.max(data, max);
            return this;
        }

        CollectData accept(float min, float max, float average) {
            count++;
            sum += average;
            this.min = Math.min(min, this.min);
            this.max = Math.max(max, this.max);
            return this;
        }

        float average() {
            return sum / count;
        }
    }
}
