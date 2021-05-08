package com.tool;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.SneakyThrows;

/**
 * @author lcx
 * @date 2021/3/18 10:48
 * @description
 * @lastChange lcx
 */
public class ReSnowFlake {

    //开始时间截 (2015-01-01)
    private final long twepoch = 1489111610226L;
    //机器ID所占位置
    private final long workerIdBits = 5L;
    //数据标识所占位数
    private final long dataCenterIdBits = 5L;
    //支持的最大机器id，结果是31 (这个移位算法可以很快的计算出几位二进制数所能表示的最大十进制数)
    private final long maxWorkerId = -1L ^ (-1L << workerIdBits);
    //支持的最大数据标识id，结果是31
    private final long maxdataCenterId = -1L ^ (-1L << dataCenterIdBits);
    //序列在id中占的位数
    private final long sequenceBits = 12L;
    //机器ID向左移12位
    private final long workerIdShift = sequenceBits;
    //数据标识id向左移17位(12+5)
    private final long dataCenterIdShift = sequenceBits + workerIdBits;
    //时间截向左移22位(5+5+12)
    private final long timestampLeftShift = sequenceBits + workerIdBits + dataCenterIdBits;
    //生成序列的掩码，这里为4095 (0b111111111111=0xfff=4095)
    private final long sequenceMask = -1L ^ (-1L << sequenceBits);

    //工作机器ID(0~31)
    private long workerId;

    //数据中心ID(0~31)
    private long dataCenterId;

    //毫秒内序列(0~4095)
    private long sequence = 0L;

    //上次生成ID的时间截
    private long lastTimestamp = -1L;

    /**
     * @param workerId     工作机器ID(0~31)
     * @param dataCenterId 数据中心ID(0~31)
     */
    public ReSnowFlake(long workerId, long dataCenterId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format(
                "worker Id can't be greater than %d or less than 0",
                maxWorkerId));
        }
        if (dataCenterId > maxdataCenterId || dataCenterId < 0) {
            throw new IllegalArgumentException(String.format(
                "datacenter Id can't be greater than %d or less than 0",
                maxdataCenterId));
        }
        this.workerId = workerId;
        this.dataCenterId = dataCenterId;
    }

    /**
     * 获得下一个ID (该方法是线程安全的)
     *
     * @return
     */
    public synchronized long nextId() {
        long timestamp = timeGen();

        //如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(String.format(
                "Clock moved backwards.  Refusing to generate id for %d milliseconds",
                lastTimestamp - timestamp));
        }

        //如果是同一时间生成的，则进行毫秒内序列
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            //毫秒内序列溢出
            if (sequence == 0) {
                //阻塞到下一个毫秒,获得新的时间戳
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {//时间戳改变，毫秒内序列重置
            sequence = 0L;
        }

        //上次生成ID的时间截
        lastTimestamp = timestamp;

        //移位并通过或运算拼到一起组成64位的ID
        return ((timestamp - twepoch) << timestampLeftShift)
            | (dataCenterId << dataCenterIdShift)
            | (workerId << workerIdShift)
            | sequence;
    }

    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     *
     * @param lastTimestamp
     * @return
     */
    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }


    /**
     * 返回以毫秒为单位的当前时间
     *
     * @return
     */
    protected long timeGen() {
        return System.currentTimeMillis();
    }

    @SneakyThrows
    public static void main(String[] args) {
        List<Data> list;
        try (BufferedReader reader = new BufferedReader(new FileReader("/Users/yanbodong/Library"
            + "/Application Support/JetBrains/IdeaIC2021.1/scratches/t1.txt"))) {
            list =
                reader.lines().map(it->it.substring(7)).map(it->new Data(Long.parseLong(it))).collect(
                Collectors.toList());
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("/Users/yanbodong/Desktop"
            + "/ttt1.txt"))) {
            list.forEach(it-> {
                try {
                    writer.write(it.toString());
                    writer.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    @lombok.Data
    public static class Data {

        LocalDateTime timestamp;
        long dwId;
        long workerId;
        long dataCenterId;
        long sequence;

        public Data(long s) {
            timestamp =
                LocalDateTime.ofInstant(Instant.ofEpochMilli((s >> 22)),
                    ZoneId
                        .systemDefault());
            sequence = (s) & ~(-1L << 12);
            dwId = (s >> (12)) & ~(-1L << (10));
            workerId = (s >> 12) & ~(-1L << 5);
            dataCenterId = (s >> 17) & ~(-1L << 5);
        }
    }

}