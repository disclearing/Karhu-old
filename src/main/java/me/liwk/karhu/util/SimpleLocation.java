package me.liwk.karhu.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.liwk.karhu.data.PlayerData;
import org.bukkit.Location;
import org.bukkit.World;

@AllArgsConstructor
@Getter
public class SimpleLocation {
    double x, y, z;
    float yaw, pitch;


    public SimpleLocation(double x, double y, double z) {
        this(x, y, z, 0, 0);
    }

    public SimpleLocation(PlayerData data) {
        this.x = data.getLocation().getX();
        this.y = data.getLocation().getY();
        this.z = data.getLocation().getZ();
        this.yaw = data.getLocation().getYaw();
        this.pitch = data.getLocation().getPitch();
    }


    public Location toLocation(World world) {
        return new Location(world, x, y, z, yaw, pitch);
    }
}
