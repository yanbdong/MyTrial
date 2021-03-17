package com.csv;

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
import java.nio.file.Files;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yanbdong@cienet.com.cn
 * @since Mar 16, 2021
 */
@Slf4j
class Intersection {


    static List<FileName> traverse(File folder) throws IOException {
        return Files.list(folder.toPath()).map(path -> path.getFileName().toString()).map(FileName::new).collect(Collectors.toList());
    }

    static List<CSVInfo> parse(File csvFile) throws IOException {
        try (CSVParser parser = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(new BufferedReader(new FileReader(csvFile)))) {
            return parser.getRecords().stream().map(CSVInfo::new).collect(Collectors.toList());
        }
    }

    @SneakyThrows
    public static void main(String[] args) {
        getComplement(parse(new File("/Users/mats/Desktop/lib2.csv")), traverse(new File("/Users/mats/Desktop/node/lib")));
    }

    static void getComplement(List<CSVInfo> left, List<FileName> right) throws IOException {
        log.info("Find csv row {}", left.size());
        log.info("Find libs {}", right.size());
        List<CSVInfo> dependencies = left.stream().filter(it -> right.stream().anyMatch(r -> compare(r, it))).collect(Collectors.toList());
        log.info("Common {}", dependencies.size());
        try (CSVPrinter printer = CSVFormat.DEFAULT.withHeader("OSS Name", "Version", "Source", "License Agreement", "Used By")
                .print(new BufferedWriter(new FileWriter("/Users/mats/Desktop/intersection.csv")))) {
            for (CSVInfo record : dependencies) {
                printer.printRecord(record.getRecords());
            }
        }
        List<FileName> diffLibs = right.stream().filter(it -> dependencies.stream().noneMatch(d -> compare(it, d))).collect(Collectors.toList());
        log.info("Find diffLibs {}", diffLibs.size());
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("/Users/mats/Desktop/libs.txt"))) {
            diffLibs.forEach(it -> {
                try {
                    writer.write(it.getName());
                    writer.newLine();
                } catch (IOException e) {

                }
            });
        }
        List<CSVInfo> diffCsv = left.stream().filter(it -> dependencies.stream().noneMatch(d -> d.equals(it))).collect(Collectors.toList());
        log.info("Find diffCsvs {}", diffCsv.size());
        try (CSVPrinter printer = CSVFormat.DEFAULT.withHeader("OSS Name", "Version", "Source", "License Agreement", "Used By")
                .print(new BufferedWriter(new FileWriter("/Users/mats/Desktop/csv.csv")))) {
            diffCsv.forEach(it -> {
                try {
                    printer.printRecord(it.getRecords());
                } catch (IOException e) {

                }
            });
        }
    }

    static boolean compare(FileName fileName, CSVInfo csvInfo) {
        return fileName.getName().startsWith(csvInfo.getRecords().get(0).split(":")[1]);
    }


    @AllArgsConstructor
    @Setter
    @Getter
    public static class FileName implements Predicate<CSVInfo> {

        String name;


        @Override
        public boolean test(CSVInfo csvInfo) {
            return compare(this, csvInfo);
        }
    }

    @AllArgsConstructor
    @Setter
    @Getter
    public static class CSVInfo implements Predicate<FileName> {

        CSVRecord records;


        @Override
        public boolean test(FileName fileName) {
            return compare(fileName, this);
        }

        @Override
        public boolean equals(Object obj) {
            return super.equals(obj);
        }
    }

}
