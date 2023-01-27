package com.ant.little.common.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;

/**
 * @author: little-ant
 * 描述:
 * @date: 2023/1/25
 * @Version 1.0
 **/
public class ImageUtil {
    public final static int SCALE_SIZE = 12;

    public static int[][] readGrayImage(String fileName) throws IOException {
        final File imageFile = new File(fileName);
        final BufferedImage image = ImageIO.read(imageFile);
// --- Confirm that image has ColorSpace Type is GRAY and PixelSize==16
        final Raster raster = image.getData();
// --- Confirm that: raster.getTransferType() == DataBuffer.TYPE_BYTE
        int[][] imageArray = new int[image.getHeight()][];//image.getWidth()];
        for (int y = 0, yLimit = image.getHeight(); y < yLimit; y++) {
            imageArray[y] = new int[image.getWidth()];
            for (int x = 0, xLimit = image.getWidth(); x < xLimit; x++) {
                final Object dataObject = raster.getDataElements(x, y, null);
                final byte[] pixelData = (byte[]) dataObject;
                final int grayscalePixelValue = pixelData[0] & 0xFF;
                imageArray[y][x] = grayscalePixelValue / SCALE_SIZE;
            }
        }
        return imageArray;
    }

    public static void main(String[] args) throws IOException {
        int image[][] = readGrayImage("/Users/yuwanglin/project/weixin-little-ant/python/game_map/mori_game_map_gray.png");
        System.out.printf("123");
    }
}
