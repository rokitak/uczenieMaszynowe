package imageProcessing;

import javafx.scene.control.Alert;

import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;

public class PatternComparator {
    ArrayList<PatternGenerator> patterns = new ArrayList<>();
    private int[] sizeOfAllImages = new int[2];

    public void setPatterns(ArrayList<PatternGenerator> patterns) {
        this.patterns = patterns;
    }

    private void computeScalePatterns() {
        int [] scale = new int[2];
        int tmpHigh, tmpWidth, MAXHIGH = 0, MAXWIDTH = 0,
                MINHIGH = Integer.MAX_VALUE, MINWIDTH = Integer.MAX_VALUE;

        for (PatternGenerator generator: patterns) {
            tmpHigh = generator.boundaryImages.get(0).getHeight();
            tmpWidth = generator.boundaryImages.get(0).getWidth();

            if (tmpHigh > MAXHIGH)
                MAXHIGH = tmpHigh;
            else if (tmpHigh < MINHIGH)
                MINHIGH = tmpHigh;

            if (tmpWidth > MAXWIDTH)
                MAXWIDTH = tmpWidth;
            else if (tmpWidth < MINWIDTH)
                MINWIDTH = tmpWidth;
        }
        scale[0] = (MAXHIGH + MINHIGH)/2;
        scale[1] = (MAXWIDTH + MINWIDTH)/2;

        sizeOfAllImages = scale;
    }

    public void scalePatterns() {
        computeScalePatterns();
        for (PatternGenerator word: patterns) {
            word.scaleImages(sizeOfAllImages);
        }
    }

    public void compareWordWithPercentage(ImageOperator operatorOfWord, int wordMain) {
        double percentage, actualValue, patternBottomValue,patternTopValue;
        int mistakes = 0;

        PatternGenerator patternsToCompare = this.patterns.get(wordMain);
        BufferedImage toCompare = null;

        operatorOfWord.scaleImages(sizeOfAllImages);
        toCompare = operatorOfWord.rescaledImages.get(0);
        final int ALLPLACES = sizeOfAllImages[0] * sizeOfAllImages[1];

        for(int x=0; x<toCompare.getWidth(); x++)
        {
            for(int y=0; y<toCompare.getHeight(); y++)
            {
                actualValue = (toCompare.getRGB(x, y) & 0xff);
                patternBottomValue = (patternsToCompare.rescaledBoundaryImage.get(1).getRGB(x, y) & 0xff);
                patternTopValue = (patternsToCompare.rescaledBoundaryImage.get(0).getRGB(x, y) & 0xff);

                if( actualValue > patternTopValue )
                {
                    mistakes++;
                }
                else if( actualValue < patternBottomValue )
                {
                    mistakes++;
                }
                else {
                    //defensive programming
                }
            }
        }
        percentage = ((double) mistakes / (double)ALLPLACES)*100.0;

        String msg = "";

        if(percentage >= 0 && percentage < 5)          msg = "Excellent";
        else if(percentage >= 5 && percentage < 10)    msg = "Very good";
        else if(percentage >= 10 && percentage < 20)    msg = "Good";
        else if(percentage >= 20 && percentage < 30)    msg = "Sufficient";
        else if(percentage >= 30 && percentage < 40)    msg = "Not good";
        else if(percentage >= 40 && percentage <= 100)  msg = "Poor";

        BigDecimal scoreWithPrecision = new BigDecimal(new Double(percentage).toString());
        BigDecimal roundOf = scoreWithPrecision.setScale(2, BigDecimal.ROUND_HALF_EVEN);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(msg);
        alert.setHeaderText(null);
        alert.setContentText("You're score of mistakes: " + roundOf + "%!");

        alert.showAndWait();
    }
}
