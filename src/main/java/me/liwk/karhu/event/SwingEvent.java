package me.liwk.karhu.event;

public class SwingEvent extends Event {

    private final long timeStamp;

    public SwingEvent() {
        timeStamp = (System.nanoTime() / 1000000);
    }

    public long getTimeStamp() {
        return timeStamp;
    }

}
