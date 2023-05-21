package me.liwk.karhu.util;

import lombok.Getter;
import me.liwk.karhu.data.PlayerData;

@Getter
public class TimedLocation extends SimpleLocation {
    long time;

    public TimedLocation(double x, double y, double z) {
        super(x, y, z, 0, 0);
        this.time = System.currentTimeMillis();
    }

    public TimedLocation(double x, double y, double z, float yaw, float pitch, long time) {
        super(x, y, z, yaw, pitch);
        this.time = time;
    }

    public TimedLocation(PlayerData data) {
        this(data.getLocation().getX(), data.getLocation().getY(), data.getLocation().getZ(), data.getLocation().getYaw(), data.getLocation().getPitch(), System.currentTimeMillis());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TimedLocation) {
            TimedLocation o = (TimedLocation) obj;
            return o.getX() == x && o.getY() == y && o.getZ() == z && o.getYaw() == yaw && o.getPitch() == pitch;
        } else return super.equals(obj);
    }
}
