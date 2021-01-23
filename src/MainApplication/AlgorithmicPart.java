package MainApplication;

import createChart.ChartPrinter;
import imageProcessing.ImageOperator;
import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import featureExtraction.MFCC;
import featureExtraction.PreemphasisOfSignal;
import models.SoundContext;
import models.WavSoundContext;
import waveFileOperations.ReaderWAVE;
import waveFileOperations.WavFileException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class AlgorithmicPart extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private static ArrayList<File> createListOfFile(String pathname) throws IOException {
        List<File> contentOfFolder = null;
        ArrayList<File> filesFromFolder = null;

        try {
            contentOfFolder = Files.walk(Paths.get(pathname))
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }

        filesFromFolder = new ArrayList<File>(contentOfFolder);

        return filesFromFolder;
    }

    @Override
    public void start(Stage primaryStage) {

        ArrayList<ArrayList<File>> contentsOfFolders = new ArrayList<ArrayList<File>>();
        ArrayList<File> contentFoldersToSave = null;
        ReaderWAVE readerToSave = null;
        ArrayList<ReaderWAVE> readers = new ArrayList<>();
        PreemphasisOfSignal preemphasedSignal = null;
        ArrayList<PreemphasisOfSignal> preparedSignals = new ArrayList<>();
        ArrayList<MFCC> mfccCoefficients = new ArrayList<>();
        final int SAMPLERATE = 44100;
        MFCC mfccToSave = null;
        ArrayList<ImageOperator> imagesOfWords = new ArrayList<>();
        ImageOperator images = null;
        ChartPrinter printer = new ChartPrinter();
        final int AMOUNTOFPATTERNS = 4;

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // Split file into small chunks
        //path to the database directory with .wav and .txt files
        String pathToDatabase = "C:\\Users\\consol\\Documents\\agh\\analiza_dzwieku\\wave\\archive\\Respiratory_Sound_Database\\Respiratory_Sound_Database\\audio_and_txt_files";
        //output of divided files from database
        String pathnameOfFile = "C:/Users/consol/Documents/agh/analiza_dzwieku/test";
        //output file for output images
        String pathnameOfOutputImage = "C:/Users/consol/Documents/agh/analiza_dzwieku/image";
        //Split long files to small chunks
        splitFilesByTxtfileContent(pathToDatabase, pathnameOfFile + "/");
        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        try {
                contentFoldersToSave = new ArrayList<File>();
                contentFoldersToSave = createListOfFile(pathnameOfFile);
                contentsOfFolders.add(contentFoldersToSave);

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Information Dialog");
            alert.setHeaderText(null);
            alert.setContentText("Invalid pathname!");
        }

        try {
            for (ArrayList<File> filesFromFolder : contentsOfFolders) {
                readerToSave = new ReaderWAVE();
                readerToSave.detectionOfWords(filesFromFolder);
                readers.add(readerToSave);
                readerToSave = null;
            }
        } catch (IOException | WavFileException | NullPointerException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Information Dialog");
            alert.setHeaderText(null);
            alert.setContentText("Invalid files!");
        }


        try {
            for (ReaderWAVE readerToRun : readers) {
                preemphasedSignal = new PreemphasisOfSignal();
                preemphasedSignal.preemphaseSignals(readerToRun.detectedWords);
                preparedSignals.add(preemphasedSignal);
                preemphasedSignal = null;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Information Dialog");
            alert.setHeaderText(null);
            alert.setContentText("Invalid pathname!");
        }

        try {
            for (PreemphasisOfSignal sample : preparedSignals) {
                mfccToSave = new MFCC(SAMPLERATE);
                mfccToSave.calculateMFCCCoefficients(sample.preemphasedSignals);
                mfccCoefficients.add(mfccToSave);
                mfccToSave = null;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            /*Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Information Dialog");
            alert.setHeaderText(null);
            alert.setContentText("Invalid pathname!");*/
        }

        ChartPrinter.print(primaryStage, mfccCoefficients.get(0).window, 0.001, "firsttest");

        int noOfWord = 0;

        for (MFCC word : mfccCoefficients) {
            images = new ImageOperator();
            images.setPathnames(pathnameOfOutputImage);
            images.createImage(word.normalizedMFCCCoefficients);
            imagesOfWords.add(images);
            images = null;
        }
    }

    private List<String> getAllTxtFilesFromDirectory(String pathToDirectory) {
        List<String> listOfTxtFiles = new LinkedList<>();

        File f = new File(pathToDirectory);

        FilenameFilter textFilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".txt");
            }
        };

        File[] files = f.listFiles(textFilter);
        for (File file : files) {
            if (file.isDirectory()) {
                //System.out.print("directory:");
            } else {
                //System.out.print("     file:");
            }
            try {
                listOfTxtFiles.add(file.getCanonicalPath());
            } catch (Exception e) {

            }
        }
        return listOfTxtFiles;
    }

    private List<WavSoundContext> splitFilesByTxtfileContent(String pathToDatabase, String outputPath) {
        List<String> txtFiles = getAllTxtFilesFromDirectory(pathToDatabase);
        List<WavSoundContext> pathsToWaveFiles = new LinkedList<>();
        txtFiles.stream().forEach(a -> {
            pathsToWaveFiles.add(new WavSoundContext(a.split("\\.")[0] + ".wav",getStartAndEndOfWavFile(a)));
        });

        trimwavFiles(pathsToWaveFiles, outputPath);
        return pathsToWaveFiles;
    }

    private void trimwavFiles(List<WavSoundContext> contexts, String outputPath) {
        int i = 0;
        for (WavSoundContext a: contexts) {
            for (SoundContext b : a.getSoundContextList()) {
                int startMilisecond = (int)(Double.parseDouble(b.getStart())*1000);
                int endMilisecond = (int)(Double.parseDouble(b.getEnd())*1000);
                int duration = endMilisecond - startMilisecond;
                spilitWavFile(a.getFilename(),outputPath + i +".wav", startMilisecond, duration);
                i++;
            }
        }
    }

    private void spilitWavFile(String sourceFileName, String destinationFileName, int startMilisecond, int milisecondsToCopy) {
        AudioInputStream inputStream = null;
        AudioInputStream shortenedStream = null;
        try {
            File file = new File(sourceFileName);
            AudioFileFormat fileFormat = AudioSystem.getAudioFileFormat(file);
            AudioFormat format = fileFormat.getFormat();
            inputStream = AudioSystem.getAudioInputStream(file);
            int bytesPerMilisecond = format.getFrameSize() * (int) (format.getFrameRate()/1000);
            inputStream.skip(startMilisecond * bytesPerMilisecond);
            long framesOfAudioToCopy = milisecondsToCopy * (int) (format.getFrameRate()/1000);
            shortenedStream = new AudioInputStream(inputStream, format, framesOfAudioToCopy);
            File destinationFile = new File(destinationFileName);
            AudioSystem.write(shortenedStream, fileFormat.getType(), destinationFile);
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            if (inputStream != null) try {
                inputStream.close();
            } catch (Exception e) {
                System.out.println(e);
            }
            if (shortenedStream != null) try {
                shortenedStream.close();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    private List<SoundContext> getStartAndEndOfWavFile(String pathToTxtFile) {
        List<SoundContext> soundContexts = new LinkedList<>();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(pathToTxtFile));
            String line = bufferedReader.readLine();
            while (line != null) {
                soundContexts.add(getDataFromLine(line));
                line = bufferedReader.readLine();
            }
        } catch (Exception e) {

        }
        return soundContexts;
    }

    private SoundContext getDataFromLine(String line){
        return new SoundContext(line.split("\t")[0],line.split("\t")[1]);
    }
}
