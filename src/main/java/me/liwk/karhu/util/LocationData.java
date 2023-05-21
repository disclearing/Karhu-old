package me.liwk.karhu.util;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LocationData {
    private static final double HITBOX_SIZE = 0.3;

    private double x, y, z;
    private float yaw, pitch;

    private final double minX, maxX;
    private final double minZ, maxZ;

    private final long time = System.currentTimeMillis();

    public LocationData(double x, double y, double z, float yaw, float pitch) {
        this.x = x;
        minX = x - HITBOX_SIZE;
        maxX = x + HITBOX_SIZE;

        this.y = y;

        this.z = z;
        minZ = z - HITBOX_SIZE;
        maxZ = z + HITBOX_SIZE;

        this.yaw = yaw;
        this.pitch = pitch;
    }

    public Vec3 toVector() {
        return new Vec3(this.x, this.y, this.z);
    }

}
