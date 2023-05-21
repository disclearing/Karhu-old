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

public class AimE extends Check {

    private final Deque<Float> samples = Lists.newLinkedList();
    private double buffer;

    public AimE(final PlayerData playerData) {
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

                final float deltaYaw = Math.abs(to.getYaw() - from.getYaw());
                final float deltaPitch = Math.abs(to.getPitch() - from.getPitch());

                if (deltaYaw > 0.0 && deltaPitch > 0.0 && deltaYaw < 30.f && deltaPitch < 30.f) {
                    samples.add(deltaPitch);
                }

                if (samples.size() == 120) {
                    final int distinct = (int) (samples.stream().distinct().count());
                    final int duplicates = samples.size() - distinct;

                    final double average = samples.stream().mapToDouble(d -> d).average().orElse(0.0);

                    if (duplicates <= 16 && duplicates > 6 && average > 7.5f && average < 25.f) {
                        if (++buffer > 1) {
                            handleFlag(player, "AimAssist E", "§7Help from Elevated\n\n§b* §fRandomized aim\n§b* §faverage=§b" + average, getBanVL("AimE"), 300000L);
                        }
                    } else {
                        buffer = Math.max(buffer - 1, 0);
                    }

                    samples.clear();
                }
            }
        }
    }
}

