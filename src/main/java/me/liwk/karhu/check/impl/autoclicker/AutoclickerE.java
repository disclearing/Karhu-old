package me.liwk.karhu.check.impl.autoclicker;

import io.github.retrooper.packetevents.event.PacketEvent;
import io.github.retrooper.packetevents.utils.player.ClientVersion;
import me.liwk.karhu.check.api.Category;
import me.liwk.karhu.check.api.Check;
import me.liwk.karhu.data.PlayerData;
import me.liwk.karhu.event.FlyingEvent;
import me.liwk.karhu.event.SwingEvent;
import me.liwk.karhu.util.MathUtil;
import org.bukkit.entity.Player;

import java.util.ArrayDeque;
import java.util.Deque;

public class AutoclickerE extends Check {

    private final Deque<Long> delays = new ArrayDeque<>();
    private int delay;
    private long lastTimestamp;

    public AutoclickerE(final PlayerData playerData) {
        super(Category.COMBAT, playerData);
    }

    @Override
    public void handle(PacketEvent event, Player player) {
        if (event instanceof SwingEvent) {
            boolean valid = !playerData.isPlacing() && !playerData.isDigging() && (playerData.getTotalTicks() - playerData.getLastDig()) > 10;

            long delay2 = playerData.getClientVersion().isLowerThan(ClientVersion.v_1_9) ? delay * 50L : ((SwingEvent) event).getTimeStamp() - lastTimestamp;

            if (delay2 <= 500 && valid) {
                delays.add(delay2);
            }

            if (delays.size() > 5 && valid) {
                double CPS = MathUtil.getCPS1000(delays);


                if (CPS > 20) {
                    handleFlag(player, "Autoclicker E", "§b* §fCPS=§b" + CPS, getBanVL("AutoclickerE"), 300000L);
                }

                if(delays.size() >= 10) {
                    delays.clear();
                }

            }
            lastTimestamp = ((SwingEvent) event).getTimeStamp();
            delay = 0;
        } else if (event instanceof FlyingEvent) {
            ++delay;
        }
    }
}
