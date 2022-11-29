package com.mattsobek;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        int[][] imageData = imgToTwoD("https://img1.10bestmedia.com/Images/Photos/372221/Cleveland-Letters-at-Edgewater-Park-Normal-Edit-2_54_990x660.jpg");
        if (imageData != null) {
            viewImageData(imageData);

            int[][] trimmed = trimBorders(imageData, 60);
            twoDToImage(trimmed, "trimmed_cle.jpg");

            int[][] negImage = negativeColor(imageData);
            twoDToImage(negImage, "neg_cle.jpg");

            int[][] horizStretchImage = stretchHorizontally(imageData);
            twoDToImage(horizStretchImage, "hstretch_cle.jpg");

            int[][] vertShrinkImage = shrinkVertically(imageData);
            twoDToImage(vertShrinkImage, "vshrink_cle.jpg");

            int[][] invertedImage = invertImage(imageData);
            twoDToImage(invertedImage, "invert_cle.jpg");

            int[][] filterImage = colorFilter(imageData, -75, 30, -30);
            twoDToImage(filterImage, "filter_cle.jpg");

            int[][] randomImage = paintRandomImage(imageData);
            twoDToImage(randomImage, "random_cle.jpg");

            int[] randColor = getRandomRGBA();
            int[] randRectangle = getRandomRectangle(imageData);
            int[][] rectangleImage = paintRectangle(imageData, randRectangle[0], randRectangle[1], randRectangle[2],
                    randRectangle[3], getColorIntValFromRGBA(randColor));
            twoDToImage(rectangleImage, "rect_cle.jpg");

            int[][] multiRandRectangles = generateRectangles(imageData, 5);
            twoDToImage(multiRandRectangles, "multirect_cle.jpg");
        }
    }

    // Image Processing Methods
    public static int[][] trimBorders(int[][] imageTwoD, int pixelCount) {
        // Example Method
        if (imageTwoD.length > pixelCount * 2 && imageTwoD[0].length > pixelCount * 2) {
            int[][] trimmedImg = new int[imageTwoD.length - pixelCount * 2][imageTwoD[0].length - pixelCount * 2];
            for (int i = 0; i < trimmedImg.length; i++) {
                // TODO - learn about arraycopy
                // System.arraycopy(imageTwoD[i + pixelCount], 0 + pixelCount, trimmedImg[i], 0, trimmedImg[i].length);
                for (int j = 0; j < trimmedImg[i].length; j++) {
                    trimmedImg[i][j] = imageTwoD[i + pixelCount][j + pixelCount];
                }
            }
            return trimmedImg;
        } else {
            System.out.println("Cannot trim that many pixels from the given image.");
            return imageTwoD;
        }
    }

    public static int[][] negativeColor(int[][] imageTwoD) {
        int[][] negImage = new int[imageTwoD.length][imageTwoD[0].length];
        for (int i = 0; i < negImage.length; i++) {
            for (int j = 0; j < negImage[i].length; j++) {
                int[] rgba = getRGBAFromPixel(imageTwoD[i][j]);
                for (int k = 0; k < rgba.length - 1; k++) {
                    rgba[k] = 255 - rgba[k];
                }
                negImage[i][j] = getColorIntValFromRGBA(rgba);
            }
        }
        return negImage;
    }

    public static int[][] stretchHorizontally(int[][] imageTwoD) {
        int[][] stretchedImage = new int[imageTwoD.length][imageTwoD[0].length * 2];
        for (int i = 0; i < imageTwoD.length; i++) {
            for (int j = 0; j < imageTwoD[i].length; j++) {
                int index = j * 2;
                stretchedImage[i][index] = imageTwoD[i][j];
                stretchedImage[i][index + 1] = imageTwoD[i][j];
            }
        }
        return stretchedImage;
    }

    public static int[][] shrinkVertically(int[][] imageTwoD) {
        int[][] stretchedImage = new int[imageTwoD.length / 2][imageTwoD[0].length];
        int oddRow = imageTwoD.length % 2;
        for (int i = 0; i < imageTwoD[0].length; i++) {
            for (int j = 0; j < imageTwoD.length - oddRow; j += 2) {
                stretchedImage[j / 2][i] = imageTwoD[j][i];
            }
        }
        return stretchedImage;
    }

    public static int[][] invertImage(int[][] imageTwoD) {
        int[][] invertedImage = new int[imageTwoD.length][imageTwoD[0].length];
        for (int i = 0; i < imageTwoD.length; i++) {
            for (int j = 0; j < imageTwoD[i].length; j++) {
                invertedImage[i][j] = imageTwoD[(imageTwoD.length - 1) - i][(imageTwoD[i].length - 1) - j];
            }
        }
        return invertedImage;
    }

    public static int[][] colorFilter(int[][] imageTwoD, int redChangeValue, int greenChangeValue, int blueChangeValue) {
        int[][] filterImage = new int[imageTwoD.length][imageTwoD[0].length];
        for (int i = 0; i < imageTwoD.length; i++) {
            for (int j = 0; j < imageTwoD[i].length; j++) {
                int[] rgba = getRGBAFromPixel(imageTwoD[i][j]);
                rgba[0] += redChangeValue;
                rgba[1] += greenChangeValue;
                rgba[2] += blueChangeValue;
                for (int k = 0; k < rgba.length - 1; k++) {
                    if (rgba[k] < 0) {
                        rgba[k] = 0;
                    } else if (rgba[k] > 255) {
                        rgba[k] = 255;
                    }
                }
                filterImage[i][j] = getColorIntValFromRGBA(rgba);
            }
        }
        return filterImage;
    }

    // Painting Methods
    public static int[][] paintRandomImage(int[][] canvas) {
        Random rand = new Random();
        int[][] randomImage = new int[canvas.length][canvas[0].length];
        for (int i = 0; i < canvas.length; i++) {
            for (int j = 0; j < canvas[i].length; j++) {
                int[] rgba = new int[4];
                for (int k = 0; k < rgba.length - 1; k++) {
                    rgba[k] = rand.nextInt(256);
                }
                rgba[3] = 255;
                randomImage[i][j] = getColorIntValFromRGBA(rgba);
            }
        }
        return randomImage;
    }

    public static int[][] paintRectangle(int[][] canvas, int height, int width, int rowPosition, int colPosition, int color) {
        int[][] rectangleImage = new int[canvas.length][canvas[0].length];
        for (int i = 0; i < canvas.length; i++) {
            for (int j = 0; j < canvas[i].length; j++) {
                if (i >= rowPosition && i <= rowPosition + height && j >= colPosition && j <= colPosition + width) {
                    rectangleImage[i][j] = color;
                } else {
                    rectangleImage[i][j] = canvas[i][j];
                }
            }
        }
        return rectangleImage;
    }

    public static int[][] generateRectangles(int[][] canvas, int numRectangles) {
        for (int i = 0; i < numRectangles; i++) {
            int[] randColor = getRandomRGBA();
            int[] randRectangle = getRandomRectangle(canvas);
            canvas = paintRectangle(canvas, randRectangle[0], randRectangle[1], randRectangle[2], randRectangle[3],
                    getColorIntValFromRGBA(randColor));
        }
        return canvas;
    }

    // Utility Methods
    public static int[][] imgToTwoD(String inputFileOrLink) {
        try {
            BufferedImage image;
            if (inputFileOrLink.substring(0, 4).equalsIgnoreCase("http")) {
                URL imageUrl = new URL(inputFileOrLink);
                image = ImageIO.read(imageUrl);
                if (image == null) {
                    System.out.println("Failed to get image from provided URL.");
                }
            } else {
                image = ImageIO.read(new File(inputFileOrLink));
            }
            // TODO - fix to handle NullPointerException
            int imgRows = image.getHeight();
            int imgCols = image.getWidth();
            int[][] pixelData = new int[imgRows][imgCols];
            for (int i = 0; i < imgRows; i++) {
                for (int j = 0; j < imgCols; j++) {
                    pixelData[i][j] = image.getRGB(j, i);
                }
            }
            return pixelData;
        } catch (Exception e) {
            System.out.println("Failed to load image: " + e.getLocalizedMessage());
            return null;
        }
    }

    public static void twoDToImage(int[][] imgData, String fileName) {
        try {
            int imgRows = imgData.length;
            int imgCols = imgData[0].length;
            BufferedImage result = new BufferedImage(imgCols, imgRows, BufferedImage.TYPE_INT_RGB);
            for (int i = 0; i < imgRows; i++) {
                for (int j = 0; j < imgCols; j++) {
                    result.setRGB(j, i, imgData[i][j]);
                }
            }
            // TODO - improve file output
            String path = "C:\\Users\\sobes\\OneDrive\\Desktop\\Arrays\\" + fileName;
            File output = new File(path);
            ImageIO.write(result, "jpg", output);
        } catch (Exception e) {
            System.out.println("Failed to save image: " + e.getLocalizedMessage());
        }
    }

    public static int[] getRGBAFromPixel(int pixelColorValue) {
        Color pixelColor = new Color(pixelColorValue);
        return new int[] { pixelColor.getRed(), pixelColor.getGreen(), pixelColor.getBlue(), pixelColor.getAlpha() };
    }

    public static int getColorIntValFromRGBA(int[] colorData) {
        if (colorData.length == 4) {
            Color color = new Color(colorData[0], colorData[1], colorData[2], colorData[3]);
            return color.getRGB();
        } else {
            System.out.println("Incorrect number of elements in RGBA array.");
            return -1;
        }
    }

    public static void viewImageData(int[][] imageTwoD) {
        if (imageTwoD.length > 3 && imageTwoD[0].length > 3) {
            int[][] rawPixels = new int[3][3];
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    rawPixels[i][j] = imageTwoD[i][j];
                }
            }
            System.out.println("Raw pixel data from the top left corner.");
            System.out.print(Arrays.deepToString(rawPixels).replace("],", "],\n") + "\n");
            int[][][] rgbPixels = new int[3][3][4];
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    rgbPixels[i][j] = getRGBAFromPixel(imageTwoD[i][j]);
                }
            }
            System.out.println();
            System.out.println("Extracted RGBA pixel data from top the left corner.");
            for (int[][] row : rgbPixels) {
                System.out.print(Arrays.deepToString(row) + System.lineSeparator());
            }
        } else {
            System.out.println("The image is not large enough to extract 9 pixels from the top left corner");
        }
    }

    public static int[] getRandomRGBA() {
        Random rand = new Random();
        int[] randomRGBA = new int[4];
        for (int i = 0; i < randomRGBA.length - 1; i++) {
            randomRGBA[i] = rand.nextInt(256);
        }
        randomRGBA[3] = 255;
        return randomRGBA;
    }

    public static int[] getRandomRectangle(int[][] imageData) {
        Random rand = new Random();
        int[] randomRectangle = new int[4];
        randomRectangle[0] = rand.nextInt(imageData.length);
        randomRectangle[1] = rand.nextInt(imageData[0].length);
        randomRectangle[2] = rand.nextInt(imageData.length - randomRectangle[0]);
        randomRectangle[3] = rand.nextInt(imageData[0].length - randomRectangle[1]);
        return randomRectangle;
    }
}