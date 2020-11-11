package com.android.screencap;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import javax.imageio.ImageIO;

/**
 * <a
 * ref=https://stackoverflow.com/questions/22034959/what-format-does-adb-screencap-sdcard-screenshot-raw-produce-without-p-f/59470924#59470924/>
 * 
 * @author yanbdong@cienet.com.cn
 * @since Sep 17, 2020
 */
class ParseRaw {

    public static void main(String[] args) throws Exception {
        BufferedImage r1 = ImageIO.read(new File("/Users/mats/Desktop/SB/png/png/try.png"));
        try (FileChannel channel = FileChannel.open(Paths.get("/Users/mats/Desktop/SB/png/png/try.raw"),
                StandardOpenOption.READ)) {
            ByteBuffer header = ByteBuffer.allocate(12).order(ByteOrder.LITTLE_ENDIAN);
            channel.read(header);
            header.flip();
            int width = header.getInt();
            int height = header.getInt();
            int type = header.getInt();
            // Data
            ByteBuffer data = ByteBuffer.allocate(4096);
            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
            int index = 0;
            while (channel.read(data) != -1) {
                data.flip();
                while (data.hasRemaining()) {
                    byte r = data.get();
                    byte g = data.get();
                    byte b = data.get();
                    byte a = data.get();
                    int d = (a << 24) + (r << 16) + (g << 8) + b;
                    bufferedImage.setRGB(index % width, index / width, d);
                    index++;
                }
                data.clear();
            }
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (r1.getRGB(x,y) == bufferedImage.getRGB(x,y)) {
                        continue;
                    } else {
                        System.out.println(String.format("At %4d, %4d with %X: %X", x, y, r1.getRGB(x,y), bufferedImage.getRGB(x,y)));
                    }
                }
            }
            ImageIO.write(bufferedImage, "png", new File("/Users/mats/Desktop/SB/myTry.png"));
            ImageIO.write(bufferedImage, "bmp", new File("/Users/mats/Desktop/SB/myTry.bmp"));
            ImageIO.write(bufferedImage, "jpg", new File("/Users/mats/Desktop/SB/myTry.jpg"));
            ImageIO.write(bufferedImage, "gif", new File("/Users/mats/Desktop/SB/myTry.gif"));
        }
        // ColorModel colorModel = new ComponentColorModel(32, 0x00ff0000, // Red
        // 0x0000ff00, // Green
        // 0x000000ff, // Blue
        // 0xff000000 );
        // BufferedImage bufferedImage = new BufferedImage(rawData.mWidth, rawData.mHeight,
        // BufferedImage.TYPE_4BYTE_ABGR);
        // bufferedImage.setData();
        //
    }

    public static class RawData {
        static final int ALPHA = 0;
        static final int RED = 1;
        static final int GREEN = 2;
        static final int BLUE = 3;
        final int mWidth;
        final int mHeight;
        final int mSize;
        final ComponentData[] mCs = new ComponentData[4];

        private RawData(int width, int height) {
            mWidth = width;
            mHeight = height;
            mSize = width * height;
            mCs[ALPHA] = new ComponentData(mSize);
            mCs[RED] = new ComponentData(mSize);
            mCs[GREEN] = new ComponentData(mSize);
            mCs[BLUE] = new ComponentData(mSize);
        }

        public static RawData init(int width, int height, int type) {
            if (type != 1) {
                // TODO
            }
            return new RawData(width, height);
        }

        public boolean hasRemaining() {
            return mCs[ALPHA].mIndex < mSize;
        }

        public RawData addData(int component, byte data) {
            mCs[component].add(data);
            return this;
        }

        static class ComponentData {
            final byte[] mComponents;
            int mIndex = 0;

            public ComponentData(int size) {
                this.mComponents = new byte[size];
            }

            public ComponentData add(byte data) {
                mComponents[mIndex] = data;
                mIndex++;
                return this;
            }
        }
    }
}
