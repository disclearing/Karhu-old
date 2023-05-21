package me.liwk.karhu.check.impl.aimassist;

import io.github.retrooper.packetevents.event.PacketEvent;
import me.liwk.karhu.check.api.Category;
import me.liwk.karhu.check.api.Check;
import me.liwk.karhu.data.PlayerData;
import me.liwk.karhu.event.FlyingEvent;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class AimG extends Check {

    private int buffer;

    public AimG(final PlayerData playerData) {
        super(Category.COMBAT, playerData);
    }

    @Override
    public void handle(PacketEvent event, Player player) {
        if (event instanceof FlyingEvent) {
            if (((FlyingEvent) event).hasLooked()) {
                Location to = ((FlyingEvent) event).toLocation();
                Location from = playerData.getLastLocation();

                if (playerData.lastAttackTick > 2) {
                    return;
                }

                if(playerData.isCinematic()) return;

                float diffYaw = Math.abs(to.getYaw() - from.getYaw());
                float diffPitch = Math.abs(to.getPitch() - from.getPitch());

                if (diffYaw <= 3.0 && diffYaw > 1 && diffPitch > 4.035 && diffPitch < 7) {
                    if (++buffer > 4) {
                        handleFlag(player, "AimAssist G", "§b* §fpitch=§b" + diffPitch + "\n§b* §fyaw=§b" + diffYaw, getBanVL("AimG"), 300000L);
                    }
                } else {
                    buffer = 0;
                }
            }
        }
    }
}

