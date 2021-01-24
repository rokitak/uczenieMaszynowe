package imageProcessing;

import gaussianFilter.GaussianFilter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ImageOperator {
    public String pathnames = new String();
    private ArrayList<BufferedImage> imagesOfWords = new ArrayList<>();
    private ArrayList<BufferedImage> smoothedImages = new ArrayList<>();
    public ArrayList<BufferedImage> rescaledImages = new ArrayList<>();

    public int[] sizeOfImages = new int[2];

    public void createImage(ArrayList<ArrayList<double[]>> coefficients) {
        int height = 0;
        int width = 0;

        for (ArrayList<double[]> word: coefficients) {
            width = word.size();
            height = word.get(0).length;

            sizeOfImages[0] = height;
            sizeOfImages[1] = width;

            BufferedImage imageToFilter = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Color[][] toImage = new Color[height][width];

            fillImageInGreyScale(height, width, word, toImage);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y< height; y++) {
                    imageToFilter.setRGB(x, y, toImage[y][x].getRGB());
                }
            }
            imagesOfWords.add(imageToFilter);
        }
        smoothGauss(2);
        scaleImages(computeScale());
        saveImage(pathnames);
    }

    private static void fillImageInGreyScale(int height, int witdh, ArrayList<double[]> data, Color[][] image) {
        int valueOfColor;
        for (int x = 0; x < height; x++) {
            for (int y = 0; y < witdh; y++) {
                valueOfColor = (int)(Math.round(data.get(y)[x]));
                image[x][y] = new Color(valueOfColor, valueOfColor, valueOfColor);
            }
        }
    }

    public void smoothGauss(float radianGauss){
        GaussianFilter gauss = new GaussianFilter(radianGauss);

        for (BufferedImage image: imagesOfWords) {
            BufferedImage smoothedImage = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
            gauss.filter(image, smoothedImage);
            smoothedImages.add(smoothedImage);
        }
    }

    public void saveImage(String pathname){
        int noOfImage = 0;
        for (BufferedImage imageToSave: rescaledImages) {
            try {
                ImageIO.write(imageToSave, "bmp", new File(pathname + noOfImage+".bmp"));
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("end");
            }
            noOfImage++;
        }
    }

    public void scaleImages(int sizeToScale[]) {
        rescaledImages = new ArrayList<>();
        BufferedImage scaledImg = null;

        for (BufferedImage image: smoothedImages) {
            scaledImg = new BufferedImage(sizeToScale[1], sizeToScale[0], image.getType());
            Graphics2D gr = scaledImg.createGraphics();
            gr.drawImage(image, 0, 0, sizeToScale[1], sizeToScale[0], null);
            gr.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            gr.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            gr.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            gr.dispose();
            rescaledImages.add(scaledImg);
        }
    }

    public int[] computeScale() {
        int [] scale = new int[2];
        int tmpHigh=30, tmpWidth=30, MAXHIGH = 30, MAXWIDTH = 100,
                MINHIGH = 0, MINWIDTH = 0;

        for (BufferedImage image: smoothedImages) {
            tmpHigh = image.getHeight();
            tmpWidth = image.getWidth();

            if (tmpHigh > MAXHIGH)
                MAXHIGH = tmpHigh;
            else if (tmpHigh < MINHIGH)
                MINHIGH = tmpHigh;

            if (tmpWidth > MAXWIDTH)
                MAXWIDTH = tmpWidth;
            else if (tmpWidth < MINWIDTH)
                MINWIDTH = tmpWidth;
        }

        scale[0] = 30;
        scale[1] = 100;

        return scale;
    }

    public void setPathnames(String pathnames) {
        this.pathnames = pathnames;
    }
}
