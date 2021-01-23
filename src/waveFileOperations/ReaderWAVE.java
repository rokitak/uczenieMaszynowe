package waveFileOperations;

import javafx.scene.control.Alert;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ReaderWAVE {
    private ArrayList<double[]> framesFromRecordsSamples = new ArrayList<double[]>();
    public ArrayList<double[]> detectedWords = new ArrayList<>();
    public int sampleRate;

    /**
     * @name      readFramesFromRecords
     * @reference Read content of frames from WAVE frames
     *
     * @author  Katarzyna Giadla
     * @param   filesToRead     type: ArrayList<File>   list of files to read
     * @throws  IOException, WavFileException, NullPointerException
     * @return  void
     *
     */

    private void readFramesFromRecords(ArrayList<File> filesToRead) throws IOException, WavFileException, NullPointerException {
        WavFile firstFile;
        int i = 0;
        for(File readFile: filesToRead) {
            try {
                firstFile = WavFile.openWavFile(readFile);

                int noOfChannel = firstFile.getNumChannels();
                long noFrames = firstFile.getNumFrames();
                this.sampleRate = (int)firstFile.getSampleRate();
                int sizeOfBufferToRead = (int)noFrames * noOfChannel;
                double [] buffer = new double[sizeOfBufferToRead];

                firstFile.readFrames(buffer, sizeOfBufferToRead);

                framesFromRecordsSamples.add(buffer);

                firstFile.close();
            } catch (IOException | WavFileException| NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @name      detectionOfWords
     * @reference Detect word of frames
     *
     * @author  Katarzyna Giadla
     * @param   void
     * @return  void
     *
     */

    public void detectionOfWords(ArrayList<File> filesToRead) throws WavFileException, IOException, NullPointerException{
        try {
            readFramesFromRecords(filesToRead);
        } catch (WavFileException | IOException | NullPointerException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Information Dialog");
            alert.setHeaderText(null);
            alert.setContentText("Invalid files!");
        }

        for(double[] sample: framesFromRecordsSamples){
            detectedWords.add(sample);
        }
    }
}