package me.liwk.karhu.event;

import lombok.Getter;

@Getter
public class PositionEvent extends Event {

    private final double x;
    private final double y;
    private final double z;
    private final float yaw;
    private final float pitch;

    public PositionEvent(double x, double y, double z, float yaw, float pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }
    private String dogshit(boolean value) {
        return value ? "%%__TIMESTAMP__%%" : "%%__USER__%%";
    }
}
