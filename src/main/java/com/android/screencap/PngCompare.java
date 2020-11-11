package com.android.screencap;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 */
class PngCompare {

    public static void main(String[] args) throws Exception {
        File baseFolder = new File("/Users/mats/Desktop/SB/png/again");
        String png = "try.png";
        String raw = "try.raw";
        String gzipraw = "try.raw.gz";
        BufferedImage pngImage = ImageIO.read(new File(baseFolder, png));
        BufferedImage gzipImage = Hex.createRGBARawImage(Hex.parseZipFile(new File(baseFolder, gzipraw)));
        BufferedImage rawImage = Hex.createRGBARawImage(Hex.parseRawFile(new File(baseFolder, raw)));
        int width = pngImage.getWidth();
        int height = pngImage.getHeight();

        WriteDiff writeDiff = new WriteDiff(baseFolder, width, height);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                writeDiff.writeRGBA(pngImage.getRGB(j, i), gzipImage.getRGB(j, i));
            }
        }
        ImageIO.write(rawImage, "png", new File(baseFolder, "try_s.png"));
        writeDiff.finish();
    }

    static class WriteDiff {
        final ChancelDiff mAlpha;
        final ChancelDiff mRed;
        final ChancelDiff mGreen;
        final ChancelDiff mBlue;

        WriteDiff(File folder, int width, int height) throws IOException {
            mAlpha = new ChancelDiff().setTag("alpha").setFile(folder).setWidth(width).setHeight(height).init();
            mRed = new ChancelDiff().setTag("red").setFile(folder).setWidth(width).setHeight(height).init();
            mGreen = new ChancelDiff().setTag("green").setFile(folder).setWidth(width).setHeight(height).init();
            mBlue = new ChancelDiff().setTag("blue").setFile(folder).setWidth(width).setHeight(height).init();
        }

        public WriteDiff writeAlpha(int left, int right) throws IOException {
            mAlpha.write(left - right);
            return this;
        }

        public WriteDiff writeRed(int left, int right) throws IOException {
            mRed.write(left - right);
            return this;
        }

        public WriteDiff writeGreen(int left, int right) throws IOException {
            mGreen.write(left - right);
            return this;
        }

        public WriteDiff writeBlue(int left, int right) throws IOException {
            mBlue.write(left - right);
            return this;
        }

        public WriteDiff writeRGBA(int left, int right) throws IOException {
            writeAlpha(left >>> 24, right >>> 24);
            writeRed((left & 0xff0000) >>> 16, (right & 0xff0000) >>> 16);
            writeGreen((left & 0xff00) >>> 8, (right & 0xff00) >>> 8);
            writeBlue((left & 0xff), (right & 0xff));
            return this;
        }

        public void finish() throws IOException {
            mAlpha.finish();
            mRed.finish();
            mGreen.finish();
            mBlue.finish();
        }

        private static class ChancelDiff {

            String tag;
            File file;
            CSVPrinter printer;
            int totalNumber;
            int nonZeroNumber;
            int max = -255;
            int min = 255;
            int width;
            int height;
            BufferedImage bufferedImage;
            File grayImage;
            long sum;
            long absSum;

            ChancelDiff init() throws IOException {
                printer = CSVFormat.DEFAULT.print(new BufferedWriter(new FileWriter(file)));
                bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
                return this;
            }

            public ChancelDiff setTag(String tag) {
                this.tag = tag;
                return this;
            }

            public ChancelDiff setWidth(int width) {
                this.width = width;
                return this;
            }

            public ChancelDiff setHeight(int height) {
                this.height = height;
                return this;
            }

            public ChancelDiff setFile(File file) {
                this.file = new File(file, tag + ".csv");
                grayImage = new File(file, tag + ".png");
                return this;
            }

            private void write(int diff) throws IOException {
                bufferedImage.getRaster().setDataElements(totalNumber % width, totalNumber / width,
                        new byte[] { Integer.valueOf(255 - Math.abs(diff)).byteValue() });
                totalNumber++;
                if (diff != 0) {
                    nonZeroNumber++;
                    max = Math.max(max, diff);
                    min = Math.min(min, diff);
                    sum += diff;
                    absSum += Math.abs(diff);
                }
                printer.print(diff);
                if (totalNumber % width == 0) {
                    printer.println();
                }
            }

            private void finish() throws IOException {
                printer.flush();
                printer.close();
                System.out.println(
                        String.format("%s: ZeroNumber: %d, nonZeroNumber: %d, max: %d, min: %d, aver: %f, adsAver: %f",
                                tag, (totalNumber - nonZeroNumber), nonZeroNumber, max, min,
                                ((float) (sum)) / nonZeroNumber, ((float) (absSum)) / nonZeroNumber));
                ImageIO.write(bufferedImage, "png", grayImage);
            }
        }
    }
}
