package me.liwk.karhu.check.impl.aimassist;

import io.github.retrooper.packetevents.event.PacketEvent;
import me.liwk.karhu.check.api.Category;
import me.liwk.karhu.check.api.Check;
import me.liwk.karhu.data.PlayerData;
import me.liwk.karhu.event.FlyingEvent;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class AimM extends Check {

    private double buffer, vl;
    private double ticks;
    private double lastModuloShit, lastRModuloShit;
    private double lastDifference, lastRDifference;

    public AimM(final PlayerData playerData) {
        super(Category.COMBAT, playerData);
    }

    @Override
    public void handle(PacketEvent event, Player player) {
        if (event instanceof FlyingEvent) {
            if (((FlyingEvent) event).hasLooked()) {
                Location to = ((FlyingEvent) event).toLocation();
                Location from = playerData.getLastLocation();

                if (playerData.lastAttackTick > 4) {
                    return;
                }

                final float deltaYaw = Math.abs(to.getYaw() - from.getYaw());
                final float deltaPitch = Math.abs(to.getPitch() - from.getPitch());

                final float rDeltaYaw = Math.abs(deltaYaw - Math.round(deltaYaw));
                final float rDeltaPitch = Math.abs(deltaPitch - Math.round(deltaPitch));

                final double moduloShit = deltaYaw % deltaPitch;
                final double difference = Math.abs(moduloShit - lastModuloShit);
                final double deltaDifference = Math.abs(difference - lastDifference);

                final double rModuloShit = rDeltaYaw % rDeltaPitch;
                final double rDifference = Math.abs(rModuloShit - lastRModuloShit);
                final double rDeltaDifference = Math.abs(rDifference - lastRDifference);

                if (rDeltaDifference < 0.05 && rDeltaDifference > 0.016 && (!playerData.isCinematic() || System.currentTimeMillis() - playerData.getLastCinematic() > 2000L)) {
                    ++buffer;

                    if (Double.isNaN(rDeltaDifference)) {
                        buffer = Math.max(buffer - 0.8, 0);
                    }

                    if (buffer > 30) {
                        ++ticks;
                        if (buffer > 35)
                            buffer -= 10;
                    } else {
                        if (ticks > 0) {
                            ticks -= 1;
                        }
                    }

                    //Bukkit.broadcastMessage("§frD: §b" + rDeltaDifference + " §ft b: §b" + ticks + " " + buffer + " §fc: §b" + (System.currentTimeMillis() - playerData.getLastCinematic() < 2000L));
                    if (ticks > 20) {
                        handleFlag(player, "AimAssist M", "§b* §f% change is too low" + "\n§b* §fdiff=§b" + rDeltaDifference + "\n§b* §fticks=§b" + ticks, getBanVL("AimM"), 500000L);
                        if (ticks >= 25)
                            ticks -= 10;
                            buffer -= 10;
                        if (buffer > 35) {
                            buffer /= 2;
                        }
                    }
                }
                lastModuloShit = moduloShit;
                lastRModuloShit = rModuloShit;
                lastDifference = difference;
                lastRDifference = rDifference;
            } else {
                buffer = Math.max(buffer - 0.75, 0);
            }
        }
    }
}
