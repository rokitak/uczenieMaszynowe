package imageProcessing;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class PatternGenerator extends ImageOperator {
    ArrayList<BufferedImage> boundaryImages = new ArrayList<>();
    ArrayList<BufferedImage> rescaledBoundaryImage = new ArrayList<>();

    public void createBounadryValues(ArrayList<BufferedImage> baseOfWord, int amountOfPatterns) throws IllegalArgumentException {
        int width = baseOfWord.get(0).getWidth();
        int height = baseOfWord.get(0).getHeight();
        int imageType = baseOfWord.get(0).getType();
        BufferedImage word;

        BufferedImage upBoundary = new BufferedImage(width, height, imageType);
        BufferedImage downBoundary = new BufferedImage(width, height, imageType);

        int val, MIN, MAX;

        if (amountOfPatterns > baseOfWord.size())
            throw new IllegalArgumentException();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                MAX = -1;
                MIN = 256;

                for (int i = 0; i < amountOfPatterns; i++) {
                    word = baseOfWord.get(i);
                    val = (word.getRGB(x, y) & 0xff);

                    if (val < MIN)
                        MIN = val;
                    if (val > MAX)
                        MAX = val;
                }

                upBoundary.setRGB(x, y, new Color(MAX, MAX, MAX).getRGB());
                downBoundary.setRGB(x, y, new Color(MIN, MIN, MIN).getRGB());
            }
        }
        boundaryImages.add(upBoundary);
        boundaryImages.add(downBoundary);

        saveImage(super.pathnames);
    }

    @Override
    public void saveImage(String pathname) {
        int noOfImage = 0;
        String name;
        for (BufferedImage imageToSave: boundaryImages) {
            if (noOfImage == 0) {
                name = "_BOTTOM";
            } else {
                name = "_TOP";
            }

            try {
                ImageIO.write(imageToSave, "bmp", new File(pathname + name +".bmp"));
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("end");
            }
            noOfImage++;
        }
    }

    @Override
    public void setPathnames(String pathnames) {
        super.setPathnames(pathnames);
    }

    @Override
    public void scaleImages(int[] sizeToScale) {

        for (BufferedImage image: boundaryImages) {
            BufferedImage scaledImg = null;
            scaledImg = new BufferedImage(sizeToScale[1], sizeToScale[0], image.getType());
            Graphics2D gr = scaledImg.createGraphics();
            gr.drawImage(image, 0, 0, sizeToScale[1], sizeToScale[0], null);
            gr.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            gr.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            gr.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            gr.dispose();
            rescaledBoundaryImage.add(scaledImg);
        }
    }
}