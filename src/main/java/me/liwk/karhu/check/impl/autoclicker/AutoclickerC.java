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

public class AutoclickerC extends Check {

    private int clicks, outliers, flyings;
    private boolean release;
    private double buffer;


    public AutoclickerC(final PlayerData playerData) {
        super(Category.COMBAT, playerData);
    }

    @Override
    public void handle(PacketEvent event, Player player) {
        if (event instanceof FlyingEvent) {
            ++flyings;
        }

        if (event instanceof DigEvent) {
            if (((DigEvent) event).getDigType() == WrappedPacketInBlockDig.PlayerDigType.RELEASE_USE_ITEM) {
                release = true;
            }
        }

        if (event instanceof SwingEvent) {
            if (flyings < 10 && flyings != 0) {
                if (release) {
                    release = false;
                    flyings = 0;
                    return;
                }

                if (flyings > 3 && !playerData.isDigging() && !playerData.isPlacing()) {
                    ++outliers;
                }

                if (++clicks == 80) {
                    if (outliers == 0) {
                        if ((buffer += 1.4) >= 6.8) {
                            handleFlag(player, "Autoclicker C", "§b* §fChecks for impossible consistency\n§b* §foutliers§b=" + outliers + "\n§b* §fvl§b=" + buffer, getBanVL("AutoclickerC"), 300000L);
                        }
                    } else {
                        buffer = Math.max(buffer - 0.8, 0);
                    }
                    outliers = 0;
                    clicks = 0;
                }
            }
        }
        flyings = 0;
    }
}


