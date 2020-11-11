package com.csv;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;

import io.reactivex.rxjava3.core.Observable;
import lombok.Setter;

/**
 * @author yanbdong@cienet.com.cn
 * @since Sep 08, 2020
 */
public class Tool {

    @Setter
    private File file;

    public static void main(String... args) throws IOException {
        File file = new File("/Volumes/NO NAME/yanbdong/0155_ff_result/result");

        try (CSVPrinter printer = CSVFormat.DEFAULT.withHeader("Local File", "Screenshot Time Cost", "Pull Time Cost")
                .print(new BufferedWriter(new FileWriter(new File(file, "0155.csv"))))) {

            Observable.fromArray(file.listFiles()).filter(it -> !it.isHidden())
                    .sorted(Comparator.comparingLong(File::lastModified)).concatMap(it -> {
                        try (CSVParser parser = CSVFormat.DEFAULT.withFirstRecordAsHeader()
                                .parse(new BufferedReader(new FileReader(it)))) {
                            return Observable.fromIterable(parser.getRecords());
                        }
                    }).forEach(printer::printRecord);

        }
    }

    public void print() throws IOException {
        try (CSVPrinter printer = CSVFormat.DEFAULT.withHeader("Local File", "Time Cost")
                .print(new BufferedWriter(new FileWriter(file)))) {
            printer.printRecords();
        }
    }

}
