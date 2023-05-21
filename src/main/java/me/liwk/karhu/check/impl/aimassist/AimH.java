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

public class AimH extends Check {

    private final Deque<Float> samples = Lists.newLinkedList();
    private double buffer;

    public AimH(final PlayerData playerData) {
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

                final float deltaPitch = Math.abs(to.getPitch() - from.getPitch());

                if (deltaPitch > 0.0 && deltaPitch < 40.f) {
                    samples.add(deltaPitch);
                }

                if (samples.size() == 40) {
                    final int distinct = (int) (samples.stream().distinct().count());
                    final int duplicates = samples.size() - distinct;

                    final double average = samples.stream().mapToDouble(d -> d).average().orElse(0.0);

                    if (average > 9.7f && average < 26.5f && duplicates >= 2) {
                        if (++buffer > 2) {
                            handleFlag(player, "AimAssist H", "§b* §fDuplicated pattern\n§b* §faverage=§b" + average + "\n§b* §fduplicates=§b" + duplicates, getBanVL("AimH"), 300000L);
                        }
                    } else buffer = Math.max(buffer - 0.5, 0);

                    samples.clear();
                }
            }
        }
    }
}
