package me.liwk.karhu.check.impl.aimassist;

import io.github.retrooper.packetevents.event.PacketEvent;
import me.liwk.karhu.check.api.Category;
import me.liwk.karhu.check.api.Check;
import me.liwk.karhu.data.PlayerData;
import me.liwk.karhu.event.FlyingEvent;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class AimA extends Check {

    private float lastPitch, lastYaw;
    private double buffer;
    private double streak;

    public AimA(final PlayerData playerData) {
        super(Category.COMBAT, playerData);
    }

    @Override
    public void handle(PacketEvent event, Player player) {
        if (event instanceof FlyingEvent) {
            if (((FlyingEvent) event).hasLooked()) {
                Location to = ((FlyingEvent) event).toLocation();
                Location from = playerData.getLastLocation();

                if(playerData.getLastAttackTick() > 10) return;

                float diffYaw = Math.abs(to.getYaw() - from.getYaw());
                float diffPitch = Math.abs(to.getPitch() - from.getPitch());

                float yawAccel = Math.abs(diffYaw - lastYaw);
                float pitchAccel = Math.abs(diffPitch - lastPitch);

                //Bukkit.broadcastMessage("§7[§c§lPRE-DEBUG§7] §fpA: §c" + pitchAccel + " §fyA: §c" + yawAccel);

                if(yawAccel > 1.5 && pitchAccel > 0.7 && diffPitch > 0.5 && diffPitch < 1.4 && pitchAccel < 2.25) {
                    if(++buffer > 3) {
                        buffer = 0;
                        if(++streak > 2) {
                            handleFlag(player, "AimAssist A", "§b* §fpA=§b" + pitchAccel + "\n§b* §fyA=§b" + yawAccel, getBanVL("AimA"), 30000L);
                        }
                    }
                } else {
                    buffer = Math.max(buffer - 0.5, 0);
                    streak = Math.max(streak - 0.25, 0);
                }

                lastYaw = diffYaw;
                lastPitch = diffPitch;
            }
        }
    }
}
