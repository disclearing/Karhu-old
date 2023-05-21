package me.liwk.karhu.check.impl.autoclicker;

import io.github.retrooper.packetevents.event.PacketEvent;
import me.liwk.karhu.check.api.Category;
import me.liwk.karhu.check.api.Check;
import me.liwk.karhu.data.PlayerData;
import me.liwk.karhu.event.FlyingEvent;
import me.liwk.karhu.event.SwingEvent;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.Queue;

public class AutoclickerA extends Check {

    public boolean AAswing;
    public int AAmoves;
    private double buffer;
    public final Queue<Integer> AAMoveIntervals = new LinkedList<>();

    public AutoclickerA(final PlayerData playerData) {
        super(Category.COMBAT, playerData);
    }

    @Override
    public void handle(PacketEvent event, Player player) {
        if (event instanceof FlyingEvent) {
            if (AAswing && !playerData.isPlacing() && !playerData.isDigging()) {
                if (AAmoves < 8) {
                    AAMoveIntervals.add(AAmoves);
                    if (AAMoveIntervals.size() == 100) {

                        double average = AAMoveIntervals.stream().mapToDouble(d -> d).average().orElse(0.0);
                        double stdDeviation = 0.0;

                        for (Integer i : AAMoveIntervals)
                            stdDeviation += Math.pow(i.doubleValue() - average, 2.0);

                        stdDeviation /= AAMoveIntervals.size();

                        if (stdDeviation <= 0.28) {
                            if (++buffer > 5) {
                                handleFlag(player, "Autoclicker A", "§b* §fstd=§b" + stdDeviation, getBanVL("AutoclickerA"), 300000L);
                            }
                        } else {
                            buffer = Math.max(buffer - 1.5, 0.0);
                        }

                        AAMoveIntervals.clear();
                    }

                    AAmoves = 0;
                }
            }

            AAswing = false;
            ++AAmoves;
        } else if(event instanceof SwingEvent) {
            AAswing = true;
        }
    }
}

