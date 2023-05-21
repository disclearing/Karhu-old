package me.liwk.karhu.check.impl.aimassist;

import io.github.retrooper.packetevents.event.PacketEvent;
import me.liwk.karhu.check.api.Category;
import me.liwk.karhu.check.api.Check;
import me.liwk.karhu.data.PlayerData;
import me.liwk.karhu.event.FlyingEvent;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class AimB extends Check {

    private int vl;

    public AimB(final PlayerData playerData) {
        super(Category.COMBAT, playerData);
    }

    @Override
    public void handle(PacketEvent event, Player player) {
        if (event instanceof FlyingEvent) {
            if (((FlyingEvent) event).hasLooked()) {
                Location to = ((FlyingEvent) event).toLocation();
                Location from = playerData.getLastLocation();

                if(playerData.getLastAttackTick() > 5) return;

                if(to.getYaw() == from.getYaw()) return;

                float fromYaw = (from.getYaw() - 90) % 360F;
                float toYaw = (to.getYaw() - 90) % 360F;


                if (fromYaw < 0F)
                    fromYaw += 360F;

                if (toYaw < 0F)
                    toYaw += 360F;

                double diffYaw = Math.abs(toYaw - fromYaw);

                if (diffYaw > 0D && playerData.getLastServerPositionTick() > 60) {
                    if (diffYaw % 1 == 0D) {
                        if ((vl += 12) > 35) {
                            handleFlag(player, "AimAssist B", "§b* §fSmoothing incorrectly" + "\n§b* §fdiff=§b" + diffYaw + "\n§b* §fyaw=§b" + fromYaw, getBanVL("AimB"), 300000L);
                        }
                        handleVerbose(player, "AimAssist B", "§b* §fSmoothing incorrectly" + "\n§b* §fdiff=§b" + diffYaw + "\n§b* §fyaw=§b" + fromYaw);
                    } else vl -= 2;
                }
            }

        }
    }
}

