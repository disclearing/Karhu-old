package me.liwk.karhu.event;

public class AbilityEvent extends Event {

    private final long timeStamp;

    public AbilityEvent() {
        timeStamp = (System.nanoTime() / 1000000);
    }

    public long getTimeStamp() {
        return timeStamp;
    }

}
