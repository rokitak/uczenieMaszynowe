package models;

public class SoundContext {
    private String start;
    private String end;

    public SoundContext(String pStart, String pEnd) {
        start = pStart;
        end = pEnd;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String pStart) {
        start = pStart;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String pEnd) {
        end = pEnd;
    }
}
