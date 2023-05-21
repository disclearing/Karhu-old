package me.liwk.karhu.check.impl.aimassist;

import com.google.common.collect.Lists;
import io.github.retrooper.packetevents.event.PacketEvent;
import me.liwk.karhu.check.api.Category;
import me.liwk.karhu.check.api.Check;
import me.liwk.karhu.data.PlayerData;
import me.liwk.karhu.event.FlyingEvent;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Deque;

public class AimI extends Check {

    public AimI(final PlayerData playerData) {
        super(Category.COMBAT, playerData);
    }


    private final Deque<Float> samplesP = Lists.newLinkedList();
    private final Deque<Float> samplesY = Lists.newLinkedList();
    private double buffer;
    
    private double lastAveragePitch;
    private double lastAverageYaw;

    @Override
    public void handle(PacketEvent event, Player player) {
        if (event instanceof FlyingEvent) {
            if (((FlyingEvent) event).hasLooked()) {
                Location to = ((FlyingEvent) event).toLocation();
                Location from = playerData.getLastLocation();

                if (playerData.lastAttackTick > 2) {
                    return;
                }

                final float deltaYaw = Math.abs(to.getYaw() - from.getYaw());
                final float deltaPitch = Math.abs(to.getPitch() - from.getPitch());

                if (deltaYaw > 0.0 && deltaPitch > 0.0 && deltaYaw < 30.f && deltaPitch < 30.f) {
                    samplesP.add(deltaPitch);
                    samplesY.add(deltaYaw);
                }

                if (samplesP.size() == 20 && samplesY.size() == 20) {

                    final double averagePitch = samplesP.stream().mapToDouble(d -> d).average().orElse(0.0);
                    final double averageYaw = samplesY.stream().mapToDouble(d -> d).average().orElse(0.0);

                    final double finalPitch = averagePitch - lastAveragePitch;
                    final double finalYaw = averageYaw - lastAverageYaw;


                    if (finalPitch > 0.65f && finalPitch < 1.2f && finalYaw > 3F && finalYaw < 5.2f) {
                        if (++buffer> 3) {
                            handleFlag(player, "AimAssist I", "§b* §fSmooth aim\n§b* §faveragePitch=§b" + averagePitch + "\n§b* §faverageYaw=§b" + averageYaw, getBanVL("AimI"), 300000L);
                        }
                    } else {
                        buffer = Math.max(buffer- 1.25, 0);
                    }

                    samplesP.clear();
                    samplesY.clear();

                    lastAverageYaw = averageYaw;
                    lastAveragePitch = averagePitch;
                }
            }
        }
    }
}
