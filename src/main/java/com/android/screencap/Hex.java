package com.android.screencap;

import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.util.zip.GZIPInputStream;

/**
 * @author yanbdong@cienet.com.cn
 * @since Sep 18, 2020
 */
class Hex {

    /**
     * 16K
     */
    private static final int BUFFER_SIZE = 16 * 1024;

    static TimeRecord sTimeRecord;

    public static void main(String[] args) throws IOException {
        // String file = "/Users/mats/Desktop/SB/png/png/1.raw.gz";
        String file = "/Users/mats/Desktop/SB/png/png/try.raw";
        sTimeRecord = new TimeRecord();
        // To compare
        // ImageIO.read(new File("/Users/mats/Desktop/SB/png/png/try.png"));
        ParsedImageData parsedImage = parseRawFile(new File(file));
        // ParsedImageData parsedImage = parseZipFile(new File(file));
        sTimeRecord.print("Parse raw");
        BufferedImage bufferedImage = createRGBARawImage(parsedImage);
        sTimeRecord.print("Create Image");
        // ImageIO.write(bufferedImage, "png", new File("/Users/mats/Desktop/SB/myTry.png"));
        sTimeRecord.print("Write to png");
    }

    public static ParsedImageData parseRawFile(File file) throws IOException {
        try (FileChannel channel = FileChannel.open(file.toPath(), StandardOpenOption.READ)) {
            ByteBuffer header = ByteBuffer.allocate(16).order(ByteOrder.LITTLE_ENDIAN);
            channel.read(header);
            header.flip();
            ParsedImageData parsedImage = new ParsedImageData();
            parsedImage.mWidth = header.getInt();
            parsedImage.mHeight = header.getInt();
            parsedImage.mDataSize = parsedImage.mWidth * parsedImage.mHeight * 4;
            parsedImage.mData = new byte[parsedImage.mDataSize];
            // Data
            int index = 0;
            while (index < parsedImage.mDataSize) {
                int length = Math.min(BUFFER_SIZE, parsedImage.mDataSize - index);
                int l = channel.read(ByteBuffer.wrap(parsedImage.mData, index, length));
                index += l;
            }
            return parsedImage;
        }
    }

    public static ParsedImageData parseZipFile(File file) throws IOException {
        try (GZIPInputStream inputStream = new GZIPInputStream(new BufferedInputStream(new FileInputStream(file)),
                BUFFER_SIZE)) {
            // header
            byte[] header = new byte[16];
            inputStream.read(header);
            ParsedImageData parsedImage = new ParsedImageData();
            parsedImage.mWidth = byteLittleEndToInt(header, 0);
            parsedImage.mHeight = byteLittleEndToInt(header, 4);
            parsedImage.mDataSize = parsedImage.mWidth * parsedImage.mHeight * 4;
            // data
            parsedImage.mData = new byte[parsedImage.mDataSize];
            int index = 0;
            while (index < parsedImage.mDataSize) {
                int length = Math.min(BUFFER_SIZE, parsedImage.mDataSize - index);
                int l = inputStream.read(parsedImage.mData, index, length);
                index += l;
            }
            return parsedImage;
        }
    }

    private static int byteLittleEndToInt(byte[] bytes, int offset) {
        return (bytes[offset] & 0xff) | ((bytes[offset + 1] & 0xff) << 8) | ((bytes[offset + 2] & 0xff) << 16)
                | ((bytes[offset + 3]) << 24);
    }

    /**
     * A raw data, which sample takes units as {@linkplain DataBuffer#TYPE_BYTE}, each pixel ranges from
     * r->g->b->alpha(thus has 4 components), pixel stores in interleaved favor.
     * 
     * @param parsedImage
     * @return
     */
    public static BufferedImage createRGBARawImage(ParsedImageData parsedImage) {
        // A simple data structure only to save data in someone order
        TimeRecord timeRecord = new TimeRecord();
        DataBufferByte dataBuffer = new DataBufferByte(parsedImage.mData, parsedImage.mDataSize);
        timeRecord.print("DataBufferByte");
        WritableRaster raster = Raster.createInterleavedRaster(dataBuffer, parsedImage.mWidth, parsedImage.mHeight,
                // To read next row, how many data in data-buffer should be skipped
                parsedImage.mWidth * 4,
                // To read next pixel, how many data in data-buffer should be skipped
                4,
                // The required bandOffset is r->g->b->a
                new int[] { 0, 1, 2, 3 }, null);
        timeRecord.print("WritableRaster");
        ComponentColorModel colorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB),
                // The effective bits
                new int[] { 8, 8, 8, 8 }, true, false, Transparency.TRANSLUCENT, DataBuffer.TYPE_BYTE);
        timeRecord.print("ComponentColorModel");
        BufferedImage bufferedImage = new BufferedImage(colorModel, raster, false, null);
        timeRecord.print("BufferedImage");
        return bufferedImage;

    }

    public static class ParsedImageData {
        int mWidth;
        int mHeight;
        byte[] mData;
        int mDataSize;
    }

    public static class TimeRecord {
        private final long startTime;
        private long lastPoint;

        public TimeRecord() {
            startTime = System.currentTimeMillis();
            lastPoint = startTime;
        }

        public void print(String tag) {
            long temp = System.currentTimeMillis();
            System.out.println(tag + " :" + (temp - lastPoint));
            lastPoint = temp;
        }
    }
}
