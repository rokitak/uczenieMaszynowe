package featureExtraction;

import java.util.ArrayList;
import java.util.Arrays;

public class MFCC {

    private double MINVALUE = 0.0;
    private double MAXVALUE = -1;
    private ArrayList<double[]> melFilterBank = new ArrayList<>();
    private ArrayList<ArrayList<double[]>> mfccCoefficients = new ArrayList<>();;
    public ArrayList<ArrayList<double[]>> normalizedMFCCCoefficients = new ArrayList<>();
    final private int WINDOWSIZE = 1024;
    final private int AMOUNTOFFILTERS = 26;
    public double[] window = new double[WINDOWSIZE];

    private void createHammingWindow() {
        for (int i = 0; i < WINDOWSIZE; i++)
            window[i] = 0.54 - 0.46 * Math.cos((2*Math.PI*i)/(WINDOWSIZE - 1));
    }

    public MFCC(int samplingRate) {
        createHammingWindow();
        MelBankFilter melFilter = new MelBankFilter();
        melFilter.initMelFilterBank(AMOUNTOFFILTERS, samplingRate, WINDOWSIZE);
        melFilterBank = melFilter.createMelFilterBank();
    }

    public void calculateMFCCCoefficients(ArrayList<double[]> samples) throws NullPointerException {

        ArrayList<double[]> mfccForWord;
        double[] tmp = new double[WINDOWSIZE];
        double [] windowResult = new double[WINDOWSIZE];

        if (samples.size() == 0) {
            throw new NullPointerException();
        } else {
            //defensive programming
        }

        for (double[] word: samples) {
            mfccForWord = new ArrayList<>();
            for (int i = 0; i <= word.length; i+= (WINDOWSIZE/2)) {
                windowResult = new double[WINDOWSIZE];
                tmp = new double[WINDOWSIZE];
                //1. Windowing
                for (int j = 0, actualSample; j < WINDOWSIZE; j++) {
                    actualSample = i + j;
                    if (actualSample >= word.length)
                        actualSample = word.length - 1;
                    tmp[j] = word[actualSample];
                    windowResult[j] = window[j] * word[actualSample];
                }

                //2. FFT
                double[] realResultFFT = new double[WINDOWSIZE];
                double[] imagineResultFFT = new double[WINDOWSIZE];
                System.arraycopy(windowResult, 0, realResultFFT, 0, WINDOWSIZE);
                Arrays.fill(imagineResultFFT, 0.0);
                FFT.transform(realResultFFT, imagineResultFFT);

                //3. amplitude spectrum
                double[] amplitudeSpectrum = new double[realResultFFT.length / 2];
                for (int k = 0; k < amplitudeSpectrum.length; k++) {
                    amplitudeSpectrum[k] = Math.pow(realResultFFT[k], 2) + Math.pow(imagineResultFFT[k], 2);
                }

                //4. filtrate through Mel Bank Filters
                double[] filtredWindow = new double[melFilterBank.size()];
                for (int k = 0; k < AMOUNTOFFILTERS; k++) {
                    filtredWindow[k] = 0.0;
                    for (int j = 0; j < amplitudeSpectrum.length; j++) {
                        filtredWindow[k] += amplitudeSpectrum[k] * melFilterBank.get(k)[j];
                    }
                }

                // 5. log
                double[] afterLogs = new double[melFilterBank.size()];
                for (int numberOfCoefficient = 0; numberOfCoefficient < melFilterBank.size(); numberOfCoefficient++) {
                    if (filtredWindow[numberOfCoefficient] < 0) {
                        filtredWindow[numberOfCoefficient] = 0.1E-6; //dopytać czy to tak zastąpić
                    }
                    afterLogs[numberOfCoefficient] = (Math.log(filtredWindow[numberOfCoefficient]));
                }

                //6. inverse FFT
                double[] realResultIFFT = afterLogs;
                double[] imagineResultIFFT = new double[melFilterBank.size()];
                FFT.inverseTransform(realResultIFFT, imagineResultIFFT);

                // 7. add to list of result tables (^f, values ,>time)
                mfccForWord.add(imagineResultIFFT);
            }
            mfccCoefficients.add(mfccForWord);
            mfccForWord = null;
        }

        normalizeMFCC();
    }

    public void normalizeMFCC() {
        double minTmp = 0.0;
        double maxTmp = -1.0;
        final double NEWMINVALUE = 0.0;
        final double NEWMAXVALUE = 255.0;
        final double NEWDIFF = NEWMAXVALUE - NEWMINVALUE;
        double diffMinMax= 0.0;
        ArrayList<double[]> normalizedMFCCForWord;

        //search maximal and minimal value in word
        for (ArrayList<double[]> record: mfccCoefficients)
        {
            for (double[] frame: record) {
                minTmp = Arrays.stream(frame).min().getAsDouble();
                maxTmp = Arrays.stream(frame).max().getAsDouble();

                if (minTmp < MINVALUE)
                    MINVALUE = minTmp;

                if (maxTmp > MAXVALUE)
                    MAXVALUE = maxTmp;
            }
        }

        diffMinMax = MAXVALUE - MINVALUE;

        for (ArrayList<double[]> record: mfccCoefficients) {
            normalizedMFCCForWord = new ArrayList<>();
            for (double[] frame: record) {
                double[] tmpFrame = new double[frame.length];
                for (int i = 0; i < frame.length; i++) {
                    tmpFrame[i] = (((frame[i] - MINVALUE)/diffMinMax) * NEWDIFF) + NEWMINVALUE;
                }
                normalizedMFCCForWord.add(tmpFrame);
                tmpFrame = null;
            }
            normalizedMFCCCoefficients.add(normalizedMFCCForWord);
            normalizedMFCCForWord = null;
        }
    }
}
