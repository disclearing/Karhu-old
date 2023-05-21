package me.liwk.karhu.util;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class CustomLocation2 {
    public double x;
    public double y;
    public double z;
    public float yaw;
    public float pitch;
    public long timeStamp;

    public CustomLocation2(double x, double y, double z, float yaw, float pitch, long timeStamp) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.timeStamp = timeStamp;
    }

    public CustomLocation2(double x, double y, double z, float yaw, float pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.timeStamp = System.currentTimeMillis();
    }

    public CustomLocation2(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.timeStamp = System.currentTimeMillis();
    }

    public CustomLocation2(Location location) {
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.yaw = location.getYaw();
        this.pitch = location.getPitch();
        this.timeStamp = System.currentTimeMillis();
    }

    public Vector toVector() {
        return new Vector(this.x, this.y, this.z);
    }

    public Location toLocation(World world) {
        return new Location(world, this.x, this.y, this.z, this.yaw, this.pitch);
    }

    public CustomLocation2 clone() {
        return new CustomLocation2(this.x, this.y, this.z, this.yaw, this.pitch, this.timeStamp);
    }
}

