package me.liwk.karhu.check.impl.aimassist;

import io.github.retrooper.packetevents.event.PacketEvent;
import me.liwk.karhu.check.api.Category;
import me.liwk.karhu.check.api.Check;
import me.liwk.karhu.data.PlayerData;
import me.liwk.karhu.event.FlyingEvent;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class AimD extends Check {

    private float lastpitch, lastyaw;
    private double buffer, buffer2;
    
    public AimD(final PlayerData playerData) {
        super(Category.COMBAT, playerData);
    }

    @Override
    public void handle(PacketEvent event, Player player) {
        if (event instanceof FlyingEvent) {
            if (((FlyingEvent) event).hasLooked()) {
                Location to = ((FlyingEvent) event).toLocation();
                Location from = playerData.getLastLocation();

                if (playerData.getLastAttackTick() > 5) return;
                
                final float deltaYaw = Math.abs(to.getYaw() - from.getYaw());
                final float deltaPitch = Math.abs(to.getPitch() - from.getPitch());

                final float yawAccel = Math.abs(this.lastyaw - deltaYaw);
                final float pitchAccel = Math.abs(this.lastpitch - deltaPitch);

                double range = 100;
                if(playerData.getLastTarget() != null) {
                    Player target = playerData.getLastTarget();
                    if (target != null) {
                        range = player.getEyeLocation().clone().toVector().setY(0.0D).distance(target.getEyeLocation().clone().toVector().setY(0.0D));
                    }
                }

                if (yawAccel > 0 && deltaYaw > 1 && deltaPitch > 0.278 && deltaPitch < 0.29) {
                    if (++buffer2 > 3) {
                        handleFlag(player, "AimAssist D", "§b* §fP=§b" + deltaPitch + "\n§b* §fY=§b" + deltaYaw, getBanVL("AimD"), 300000L);
                    }
                } else {
                    buffer2 = Math.max(buffer2 - 1, 0);
                }

                if (yawAccel > 1.5 && (pitchAccel < 0.05 && pitchAccel > 0.0 || pitchAccel == 0) && range > 0.5) {
                    if (++buffer > 5) {
                        handleFlag(player, "AimAssist D", "§b* §fpA=§b" + pitchAccel + "\n§b* §fyA=§b" + yawAccel, getBanVL("AimD"), 300000L);
                    }
                } else {
                    buffer = Math.max(buffer - 1, 0);
                }
                this.lastyaw = deltaYaw;
                this.lastpitch = deltaPitch;
            }
        }
    }
}

