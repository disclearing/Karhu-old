package me.liwk.karhu.event;

public class WindowEvent extends Event {

    private final long timeStamp;

    public WindowEvent() {
        timeStamp = (System.nanoTime() / 1000000);
    }

    public long getTimeStamp() {
        return timeStamp;
    }

}
