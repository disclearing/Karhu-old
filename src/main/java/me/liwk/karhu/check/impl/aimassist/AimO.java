package me.liwk.karhu.check.impl.aimassist;

import io.github.retrooper.packetevents.event.PacketEvent;
import me.liwk.karhu.check.api.Category;
import me.liwk.karhu.check.api.Check;
import me.liwk.karhu.data.PlayerData;
import me.liwk.karhu.event.FlyingEvent;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class AimO extends Check {

    private int buffer;

    public AimO(final PlayerData playerData) {
        super(Category.COMBAT, playerData);
    }

    @Override
    public void handle(PacketEvent event, Player player) {
        if (event instanceof FlyingEvent) {
            if (((FlyingEvent) event).hasLooked()) {
                Location to = ((FlyingEvent) event).toLocation();
                Location from = playerData.getLastLocation();

                if (playerData.lastAttackTick > 3) {
                    return;
                }

                final float deltaYaw = Math.abs(to.getYaw() - from.getYaw());
                final float deltaPitch = Math.abs(to.getPitch() - from.getPitch());

                if(deltaYaw == 0) return;

                double rP = reversedPitch(playerData.getSensitivity(), deltaPitch);
                double rY = reversedYaw(playerData.getSensitivity(), deltaYaw);

                if(String.valueOf(deltaPitch).contains("E") && deltaYaw > 1) {
                    if(++buffer > 10) {
                        handleFlag(player, "AimAssist O", "§b* §fNearly impossible rotation" + "\n§b* §fpitch §b" + deltaPitch, getBanVL("AimO"), 500000L);
                    }
                } else {
                    buffer = Math.max(buffer - 1, 0);
                }

                if(rY <= 0.000001 || rP <= 0.000001 && rP != 0 && rY != 0) {
                    //handleFlag(player, "AimAssist O", "§b* §fClient GCD patch", getBanVL("AimO"), 500000L);
                }


            }
        }
    }


    private double reversedPitch(double sens, double pitch) {
        float f = (float)(sens * 0.6 + 0.2);
        float gcd = f * f * f * 1.2F;
        return pitch % gcd;
    }

    private double reversedYaw(double sens, double yaw) {
        float f = (float)(sens * 0.6 + 0.2);
        float gcd = f * f * f * 1.2F;
        return yaw % gcd;
    }
}
