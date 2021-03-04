package com.csv;

import com.http.Crawler;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.vavr.Tuple2;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yanbdong@cienet.com.cn
 * @since Sep 08, 2020
 */
@Slf4j
public class Tool {

    @Setter
    private File file;

    public static void main(String... args) throws IOException {
        File file = new File("/Users/mats/Desktop/lib.csv");
//        Path path = Paths.get(file.getAbsolutePath(), "ht lsd/sldkf");
//
//        try (CSVPrinter printer = CSVFormat.DEFAULT.withHeader("Local File", "Screenshot Time Cost", "Pull Time Cost")
//                .print(new BufferedWriter(new FileWriter(new File(file, "0155.csv"))))) {
//
//            Observable.fromArray(file.listFiles()).filter(it -> !it.isHidden())
//                    .sorted(Comparator.comparingLong(File::lastModified)).concatMap(it -> {
//                        try (CSVParser parser = CSVFormat.DEFAULT.withFirstRecordAsHeader()
//                                .parse(new BufferedReader(new FileReader(it)))) {
//                            return Observable.fromIterable(parser.getRecords());
//                        }
//                    }).forEach(printer::printRecord);
//
//        }
        read0(new File("/Users/mats/Desktop/lib1.csv"), new File("/Users/mats/Desktop/lib2.csv"));
    }

    public static void read(File file, File out) throws IOException {
        List<CSVRecord> records = new ArrayList<>();
        CSVRecord temp = null;
        try (CSVParser parser = CSVFormat.DEFAULT.parse(new BufferedReader(new FileReader(file)))) {
            for (CSVRecord record : parser.getRecords()) {
                if (temp == null) {
                } else {
                    if (Objects.equals(record.get(1), temp.get(1))) {
                        continue;
                    } else {
                    }
                }
                temp = record;
                records.add(record);
            }
        }
        try (CSVPrinter printer = CSVFormat.DEFAULT.withHeader("OSS Name", "Version", "Source", "License Agreement", "Used By")
                .print(new BufferedWriter(new FileWriter(out)))) {
            for (CSVRecord record : records) {
                printer.printRecord(record.get(0) + ":" + record.get(1), "https://mvnrepository.com", record.get(2), "Apache 2.0", "Node");
            }
        }

    }

    public static void read0(File file, File out) throws IOException {
        List<CSVRecord> records = new ArrayList<>();
        CSVRecord temp = null;
        try (CSVParser parser = CSVFormat.DEFAULT.withHeader("OSS Name", "Version", "Source", "License Agreement", "Used By").withSkipHeaderRecord().parse(new BufferedReader(new FileReader(file)))) {
            records = parser.getRecords();
        }
        Crawler crawler = new Crawler();
        try (CSVPrinter printer = CSVFormat.DEFAULT.withHeader("OSS Name", "Version", "Source", "License Agreement", "Used By")
                .print(new BufferedWriter(new FileWriter(out)))) {
            for (CSVRecord record : records) {
                try {
                    String[] ossName = record.get(0).split(":");
                    URL url = new URL("https", "mvnrepository.com", "/artifact/" + String.join("/", ossName[0], ossName[1], record.get(2)));
                    Tuple2<String, String> tuple2 = crawler.get(url);
                    printer.printRecord(record.get(0), record.get(2), tuple2._2(), tuple2._1(), "Node");
                } catch (Exception e) {
                    log.info("Error: " + record, e);
                }
            }
        }

    }

    public void print() throws IOException {
        try (CSVPrinter printer = CSVFormat.DEFAULT.withHeader("Local File", "Time Cost")
                .print(new BufferedWriter(new FileWriter(file)))) {
            printer.printRecords();
        }
    }

}
