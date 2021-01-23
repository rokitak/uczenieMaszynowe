package models;

import java.util.List;

public class WavSoundContext {
    String filename;
    List<SoundContext> soundContextList;

    public WavSoundContext(String pFilename, List<SoundContext> pSoundContextList) {
        filename = pFilename;
        soundContextList = pSoundContextList;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String pFilename) {
        filename = pFilename;
    }

    public List<SoundContext> getSoundContextList() {
        return soundContextList;
    }

    public void setSoundContextList(List<SoundContext> pSoundContextList) {
        soundContextList = pSoundContextList;
    }
}
