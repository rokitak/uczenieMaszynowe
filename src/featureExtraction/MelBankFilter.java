package featureExtraction;

import java.util.ArrayList;

public class  MelBankFilter {

    private double[] scaledFrequencies;
    private int numberOfFrequenciesPoints;
    private int numberOfFFT;

    private double convertToMelScale(double hertzFrequency) {
        return 1127.0 * Math.log(1 + (hertzFrequency/700.0));
    }

    private double convertToHertzScale(double melFrequency) {
        return 700.0 * (Math.exp(melFrequency/1127.0) - 1.0);
    }

    void initMelFilterBank (int amountOfFilters, int sampleRate, int windowSize) {
        numberOfFFT = windowSize;
        numberOfFrequenciesPoints = amountOfFilters + 2;
        final double MINHzFrequency = 20.0;
        final double MAXHzFrequency = 8000.0;

        double MINMelFrequency = convertToMelScale(MINHzFrequency);
        double MAXMelFrequency = convertToMelScale(MAXHzFrequency);

        double[] hzFrequencies= new double[numberOfFrequenciesPoints];
        double[] melFrequencies = new double[numberOfFrequenciesPoints];

        double frequencyStep = (MAXMelFrequency - MINMelFrequency)/(double)(numberOfFrequenciesPoints - 1);

        double tmp = MINMelFrequency;

        for (int i = 0; i < numberOfFrequenciesPoints; i++) {
            melFrequencies[i] = tmp;
            tmp += frequencyStep;
            hzFrequencies[i] = Math.round(convertToHertzScale(melFrequencies[i]));
        }

        scaledFrequencies = new double[numberOfFrequenciesPoints];

        for (int i = 0; i < numberOfFrequenciesPoints; i++) {
            scaledFrequencies[i] = Math.floor(((numberOfFFT+1)*hzFrequencies[i])/16000);
        }
    }

    ArrayList<double[]> createMelFilterBank() {
        ArrayList<double[]> filters = new ArrayList<>();
        double firstFrequency, secondFrequency, thirdFrequency;
        double[] filtersToAdd = new double[numberOfFFT];

        for (int i = 1; i < numberOfFrequenciesPoints - 1; i++) {
            filtersToAdd = new double[numberOfFFT];
            for (int j = 0; j < numberOfFFT; j++) {
                firstFrequency = scaledFrequencies[i - 1];
                secondFrequency = scaledFrequencies[i];
                thirdFrequency = scaledFrequencies[i + 1];

                if (j < firstFrequency)
                    filtersToAdd[j] = 0.0;
                else if (j >= firstFrequency && j <= secondFrequency)
                    filtersToAdd[j] = ((j - firstFrequency) / (secondFrequency - firstFrequency));
                else if (j >= secondFrequency && j <= thirdFrequency)
                    filtersToAdd[j] = (thirdFrequency - j)/(thirdFrequency - secondFrequency);
                else if (j > thirdFrequency)
                    filtersToAdd[j] = 0.0;
            }
            filters.add(filtersToAdd);
            filtersToAdd = null;
        }
        return filters;
    }
}
