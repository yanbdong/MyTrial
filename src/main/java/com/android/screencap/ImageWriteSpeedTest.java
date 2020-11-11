package com.android.screencap;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.IOException;

public class ImageWriteSpeedTest {

    // Load OpenCV native libraries
    static {
        // nu.pattern.OpenCV.loadShared();
        System.load("/Users/mats/Documents/workspace/nodeProd/node/test/src/dist/opencv/libopencv_java411.dylib");
    }

    public static void main(String[] args) throws Exception {
        File baseFolder = new File("/Users/mats/Desktop/SB/png/png/yanbdong");
        File t1 = new File(baseFolder, "spectrum.png");
        File t2 = new File(baseFolder, "spectrum.raw");
        // imageWriteMyself(file, t2);
        for (int i = 1; i <= 9; i++) {
            File target = new File(baseFolder, "compression-" + i + ".png");
            imageWriteMyself(t2, target, i);
        }

        // File cop = Paths
        // .get(ImageWriteSpeedTest.class.getResource("/").getFile(), "..", "resources", "image", "try.png")
        // .toFile();
        // BufferedImage c = ImageIO.read(cop);
        // BufferedImage cb1 = ImageIO.read(t1);
        // BufferedImage cb2 = ImageIO.read(t2);
        // System.out.println("c1");
        // PngCompare.WriteDiff writeDiff = new PngCompare.WriteDiff(new File(System.getProperty("user.dir")),
        // c.getWidth(), c.getHeight());
        // for (int i = 0; i < c.getHeight(); i++) {
        // for (int j = 0; j < c.getWidth(); j++) {
        // writeDiff.writeRGBA(c.getRGB(j, i), cb1.getRGB(j, i));
        // }
        // }
        // writeDiff.finish();
        // System.out.println("c2");
        // writeDiff = new PngCompare.WriteDiff(new File(System.getProperty("user.dir")), c.getWidth(), c.getHeight());
        // for (int i = 0; i < c.getHeight(); i++) {
        // for (int j = 0; j < c.getWidth(); j++) {
        // writeDiff.writeRGBA(c.getRGB(j, i), cb2.getRGB(j, i));
        // }
        // }
        // writeDiff.finish();

    }

    public static void imageWrite(File source, File sink, int pngCompression) throws IOException {
        Hex.ParsedImageData data = Hex.parseRawFile(source);
        Hex.TimeRecord timeRecord = new Hex.TimeRecord();
        Mat mat = convertImage(data.mData, data.mHeight, data.mWidth);
        timeRecord.print("convertImage");
        Imgcodecs.imwrite(sink.getAbsolutePath(), mat, new MatOfInt(Imgcodecs.IMWRITE_PNG_COMPRESSION, pngCompression));
        timeRecord.print("convertImage： done");
    }

    public static void imageWriteMyself(File source, File sink, int pngCompression) throws IOException {
        Hex.ParsedImageData data = Hex.parseZipFile(source);
        Mat mat = convertImageMySelf(data.mData, data.mHeight, data.mWidth);
        long startTime = System.currentTimeMillis();
        Imgcodecs.imwrite(sink.getAbsolutePath(), mat, new MatOfInt(Imgcodecs.IMWRITE_PNG_COMPRESSION, pngCompression));
        long t = System.currentTimeMillis() - startTime;
        System.out.println(String.format("Compression-%d: size %dByte, time %dms",pngCompression, sink.length(), t));
    }

    public static Mat convertImage(byte[] data, int height, int width) {
        Mat mat = new Mat(height, width, CvType.CV_8UC4);
        mat.put(0, 0, data);
        Mat n = new Mat();
        Imgproc.cvtColor(mat, n, Imgproc.COLOR_RGBA2BGRA);
        return n;
    }

    public static Mat convertImageMySelf(byte[] data, int height, int width) {
        Mat mat = new Mat(height, width, CvType.CV_8UC4);
        byte tmp;
        for (int i = 0; i < data.length; i += 4) {
            tmp = data[i];
            data[i] = data[i + 2];
            data[i + 2] = tmp;
        }
        mat.put(0, 0, data);
        return mat;
    }
}
