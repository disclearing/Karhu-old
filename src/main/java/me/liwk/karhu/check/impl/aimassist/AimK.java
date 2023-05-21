package me.liwk.karhu.check.impl.aimassist;

import io.github.retrooper.packetevents.event.PacketEvent;
import me.liwk.karhu.check.api.Category;
import me.liwk.karhu.check.api.Check;
import me.liwk.karhu.data.PlayerData;
import me.liwk.karhu.event.FlyingEvent;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class AimK extends Check {

    private double buffer;

    public AimK(final PlayerData playerData) {
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

                if(deltaYaw % 0.5 == 0.0 && deltaYaw > 0) {
                    if(++buffer > 4) {
                        handleFlag(player, "AimAssist K", "§b* §fTick aim" + "\n§b* §fdeltaYaw=§b" + deltaYaw, getBanVL("AimK"), 500000L);
                        buffer = 0;
                    }
                } else {
                    buffer = Math.max(buffer - 1, 0);
                }
            }
        }
    }
}
