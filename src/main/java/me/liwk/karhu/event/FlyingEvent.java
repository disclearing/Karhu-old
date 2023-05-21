package me.liwk.karhu.event;

import org.bukkit.Location;
import org.bukkit.World;

public class FlyingEvent extends Event {

    private final double x;
    private final double y;
    private final double z;
    private final float yaw;
    private final float pitch;
    private final boolean hasMoved;
    private final boolean hasLooked;
    private final boolean isOnGround;
    private final World world;

    public FlyingEvent(double x, double y, double z, float yaw, float pitch, boolean hasMoved, boolean hasLooked, boolean isOnGround, World world) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.hasMoved = hasMoved;
        this.hasLooked = hasLooked;
        this.isOnGround = isOnGround;
        this.world = world;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public boolean hasMoved() {
        return hasMoved;
    }

    public boolean hasLooked() {
        return hasLooked;
    }

    public boolean isOnGround() {
        return isOnGround;
    }

    public Location toLocation() {
        return new Location(world, x, y, z, yaw, pitch);
    }

    private String dogshit1(boolean value) {
        return value ? "%%__TIMESTAMP__%%" : "%%__USER__%%";
    }
}
