package me.liwk.karhu.check.impl.killaura;

import com.google.common.collect.Lists;
import io.github.retrooper.packetevents.event.PacketEvent;
import me.liwk.karhu.check.api.Category;
import me.liwk.karhu.check.api.Check;
import me.liwk.karhu.data.PlayerData;
import me.liwk.karhu.event.FlyingEvent;
import me.liwk.karhu.util.MathUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Deque;

public class KillauraH extends Check {

    private final Deque<Float> samples = Lists.newLinkedList();
    private double buffer, buffer2;

    public KillauraH(final PlayerData playerData) {
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

                if (deltaYaw > 0.0 && deltaPitch > 0.0) {
                    samples.add(deltaYaw % deltaPitch);
                }

                if (samples.size() == 40) {
                    final int distinct = (int) (samples.stream().distinct().count());
                    final int duplicates = samples.size() - distinct;

                    final double average = samples.stream().mapToDouble(d -> d).average().orElse(0.0);
                    
                    final double std = MathUtil.getStandardDeviation(samples);

                    //Bukkit.broadcastMessage("§7[§c§lPRE-DEBUG§7] §fD: §c" + duplicates + " §fA: §c" + average + " §fN: §c" + Double.isNaN(std) + " §fS: §c" + std);

                    if (duplicates >= 6 && average > 0.125f && std > 0.165 && average < 0.55 && std < 0.325) {
                        //Bukkit.broadcastMessage("§7[§c§lPOST-DEBUG§7] §fD: §c" + duplicates + " §fA: §c" + average + " §fN: §c" + Double.isNaN(std) + " §fS: §c" + std);
                        if (++buffer > 2.25) {
                            handleFlag(player, "Killaura H", "§b* §fInvalid rotation\n§b* §fA=§b" + average + "\n§b* §fD=§b" + duplicates + "\n§b* §fS=§b" + std, getBanVL("KillauraH"), 600000L);
                        }
                    } else {
                        buffer = Math.max(buffer - 0.25, 0);
                    }

                    if (duplicates >= 4 && average > 0.3d && std > 0.3 && average < 0.6 && std < 0.45 || isNearlySame(std, average) && duplicates > 2) {
                        //Bukkit.broadcastMessage("§7[§c§lPOST-DEBUG§7] §fD: §c" + duplicates + " §fA: §c" + average + " §fN: §c" + Double.isNaN(std) + " §fS: §c" + std);
                        if (++buffer2 > 2.5) {
                            handleFlag(player, "Killaura H", "§b* §fInvalid rotation\n§b* §fA=§b" + average + "\n§b* §fD=§b" + duplicates + "\n§b* §fS=§b" + std, getBanVL("KillauraH"), 600000L);
                        }
                    } else {
                        buffer2 = Math.max(buffer2 - 0.25, 0);
                    }

                    samples.clear();
                }
            }
        }
    }
    public boolean isNearlySame(double d1, double d2) {
        return Math.abs(d1 - d2) < 0.011;
    }
}