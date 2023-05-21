package me.liwk.karhu.check.impl.killaura;

import io.github.retrooper.packetevents.event.PacketEvent;
import me.liwk.karhu.check.api.Category;
import me.liwk.karhu.check.api.Check;
import me.liwk.karhu.data.PlayerData;
import me.liwk.karhu.event.FlyingEvent;
import me.liwk.karhu.util.MathUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class KillauraJ extends Check {

    private float lastDeltaPitch;
    private double buffer;

    public KillauraJ(final PlayerData playerData) {
        super(Category.COMBAT, playerData);
    }

    @Override
    public void handle(PacketEvent event, Player player) {
        if (event instanceof FlyingEvent) {
            if (((FlyingEvent) event).hasLooked()) {
                Location to = ((FlyingEvent) event).toLocation();
                Location from = playerData.getLastLocation();

                if (playerData.lastAttackTick > 5) {
                    return;
                }

                float deltaPitch = MathUtil.getDistanceBetweenAngles(to.getPitch(), from.getPitch());
                float deltaYaw = MathUtil.getDistanceBetweenAngles(to.getYaw(), from.getYaw());

                double range = 100;
                if(playerData.getLastTarget() != null) {
                    Player target = playerData.getLastTarget();
                    if (target != null) {
                        range = player.getEyeLocation().clone().toVector().setY(0.0D).distance(target.getEyeLocation().clone().toVector().setY(0.0D));
                    }
                }

                if(deltaYaw > 0.0 && deltaPitch > 0.0 && range > 0.6 && (!playerData.isCinematic() || System.currentTimeMillis() - playerData.getLastCinematic() > 2500L)) {
                    long dogPitch = (long) (deltaPitch * Math.pow(2, 24));
                    long dogPitch2 = (long) (lastDeltaPitch * Math.pow(2, 24));
                    if (MathUtil.getGcd(dogPitch, dogPitch2) <= 0b100000000000000000) {
                        if(++buffer > 13) {
                            handleFlag(player, "Killaura J", "§b* §fgcd modulo rotation\n§b* §fGCD=§b" + MathUtil.getGcd(dogPitch, dogPitch2), getBanVL("KillauraJ"), 300000L);
                        }
                    } else {
                        buffer = Math.max(buffer - 2.5, 0);
                    }
                } else {
                    buffer = 0;
                }
                lastDeltaPitch = deltaPitch;
            }
        }
    }
    private String dogshit(boolean value) {
        return value ? "%%__TIMESTAMP__%%" : "%%__USER__%%";
    }
}
