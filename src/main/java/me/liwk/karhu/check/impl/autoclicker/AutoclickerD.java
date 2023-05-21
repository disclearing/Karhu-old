package me.liwk.karhu.check.impl.autoclicker;

import io.github.retrooper.packetevents.event.PacketEvent;
import me.liwk.karhu.check.api.Category;
import me.liwk.karhu.check.api.Check;
import me.liwk.karhu.data.PlayerData;
import me.liwk.karhu.event.FlyingEvent;
import me.liwk.karhu.event.SwingEvent;
import me.liwk.karhu.util.MathUtil;
import org.bukkit.entity.Player;

import java.util.LinkedList;

public class AutoclickerD extends Check {

    public final LinkedList<Integer> clicksList = new LinkedList<>();
    public int flyingCount;
    private double lastStd;
    private double PreVL;

    public AutoclickerD(final PlayerData playerData) {
        super(Category.COMBAT, playerData);
    }

    @Override
    public void handle(PacketEvent event, Player player) {
        if (event instanceof SwingEvent) {
            if (flyingCount < 10 && !playerData.isPlacing() && !playerData.isDigging()) {
                this.clicksList.add(flyingCount);

                double standardDeviation = MathUtil.getStandardDeviation(clicksList);

                if (clicksList.size() == 50) {
                    if (Math.abs(standardDeviation - lastStd) < 0.04) {
                        if (++PreVL > 2) {
                            handleFlag(player, "Autoclicker D", "§b* §fPoor randomization\n§b* §fdiff§b=" + Math.abs(standardDeviation - lastStd), getBanVL("AutoclickerD"), 300000L);
                        }
                    } else {
                        PreVL = Math.max(PreVL - 1.25, 0.0);
                    }
                    clicksList.clear();
                    lastStd = standardDeviation;
                }
            }
            flyingCount = 0;

        } else if (event instanceof FlyingEvent) {
            ++flyingCount;
        }
    }
}
