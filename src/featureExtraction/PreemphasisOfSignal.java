package featureExtraction;

import java.util.ArrayList;

public class PreemphasisOfSignal {
    public ArrayList<double[]> preemphasedSignals = new ArrayList<>();
    private double preEmphasisCoefficient = 0.97;

    /**
     * @name      preemphaseSignals
     * @reference Prepare signal to framing
     *
     * @author  Katarzyna Giadla
     * @param   signalsToAnalyse     type: ArrayList<double[]>   list of frames of signals
     * @throws NullPointerException
     * @return  void
     *
     */

    public void preemphaseSignals(ArrayList<double[]> signalsToAnalyse) throws NullPointerException{
        if (signalsToAnalyse.isEmpty()){
            throw new NullPointerException("Array is empty!");
        } else {
            //defensive programming
        }
        for (double[] sample : signalsToAnalyse) {
            double[] signalToSave = new double[sample.length];
            for (int i = 0; i < sample.length; i++) {
                if (i == 0) {
                    signalToSave[i] = sample[i];
                } else {
                    signalToSave[i] = sample[i] - preEmphasisCoefficient * sample[i - 1];
                }
            }
            preemphasedSignals.add(signalToSave);
        }
    }
}
