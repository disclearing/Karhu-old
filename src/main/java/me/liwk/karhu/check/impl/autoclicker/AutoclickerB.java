package me.liwk.karhu.check.impl.autoclicker;

import io.github.retrooper.packetevents.event.PacketEvent;
import io.github.retrooper.packetevents.packetwrappers.in.blockdig.WrappedPacketInBlockDig;
import me.liwk.karhu.check.api.Category;
import me.liwk.karhu.check.api.Check;
import me.liwk.karhu.data.PlayerData;
import me.liwk.karhu.event.DigEvent;
import me.liwk.karhu.event.FlyingEvent;
import me.liwk.karhu.event.SwingEvent;
import org.bukkit.entity.Player;

import java.util.LinkedList;


public class AutoclickerB extends Check {

    public final LinkedList<Integer> recentCounts = new LinkedList<>();
    public int flyingCount;
    public boolean release;
    private double buffer;
    
    public AutoclickerB(final PlayerData playerData) {
        super(Category.COMBAT, playerData);
    }

    @Override
    public void handle(PacketEvent event, Player player) {
        if (event instanceof FlyingEvent) {
            ++flyingCount;
        }

        if (event instanceof DigEvent) {
            if (((DigEvent) event).getDigType() == WrappedPacketInBlockDig.PlayerDigType.RELEASE_USE_ITEM) {
                release = true;
            }
        }

        if (event instanceof SwingEvent) {
            if (playerData.isLagging(System.currentTimeMillis(), 150L)) return;
            if (!playerData.isPlacing() && !playerData.isDigging()) {
                if (flyingCount < 8) {
                    if (release) {
                        release = false;
                        flyingCount = 0;
                        return;
                    }
                    recentCounts.add(flyingCount);
                    if (recentCounts.size() == 100) {
                        double average = 0.0;
                        for (final int i : recentCounts) {
                            average += i;
                        }
                        average /= recentCounts.size();
                        double stdDev = 0.0;
                        for (final int j : recentCounts) {
                            stdDev += Math.pow(j - average, 2.0);
                        }

                        stdDev /= recentCounts.size();
                        stdDev = Math.sqrt(stdDev);
                        if (stdDev < 0.4) {
                            if ((buffer += 1.4) >= 5.0) {
                                handleFlag(player, "Autoclicker B", "§b* §fBasic std\n§b* §fstd§b=" + stdDev + "\n§b* §fvl§b=" + buffer, getBanVL("AutoclickerB"), 300000L);
                            }
                        } else {
                            buffer = Math.max(buffer - 0.8, 0.0);
                        }
                        recentCounts.clear();
                    }
                }
                flyingCount = 0;
            }
        }
    }
}
