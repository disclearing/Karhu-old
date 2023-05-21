package me.liwk.karhu.check.impl.timer;

import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.event.PacketEvent;
import io.github.retrooper.packetevents.utils.player.ClientVersion;
import io.github.retrooper.packetevents.utils.server.ServerVersion;
import me.liwk.karhu.check.api.Category;
import me.liwk.karhu.check.api.Check;
import me.liwk.karhu.data.PlayerData;
import me.liwk.karhu.event.FlyingEvent;
import org.bukkit.entity.Player;

public class TimerB extends Check {

    public long lastPacket;
    private double count, buffer;
    
    public TimerB(final PlayerData playerData) {
        super(Category.PACKET, playerData);
    }

    @Override
    public void handle(PacketEvent event, Player player) {
        if (event instanceof FlyingEvent) {
            if (playerData.getClientVersion().isLowerThan(ClientVersion.v_1_9)) {
                long delay = System.currentTimeMillis() - playerData.lastFlying;
                if (delay > 66L) {
                    if (count++ > 10) {
                        count -= 2;
                        if (++buffer > 3) {
                            buffer -= 2;
                            handleFlag(player, "Timer B", "§b* §fNegative timer§b\n§b* §fdelay=§b" + delay, getBanVL("TimerB"), 100000L);
                        }
                    }
                } else {
                    count = Math.max(buffer - 1, 0);
                    buffer = Math.max(buffer - 0.25, 0);
                }
            } else if (playerData.getClientVersion().isHigherThan(ClientVersion.v_1_8) && PacketEvents.getAPI().getServerUtils().getVersion().isHigherThan(ServerVersion.v_1_8)) {
                if (playerData.lastServerPositionTick > 35 && System.currentTimeMillis() - playerData.getLastMovement() < 400L) {
                    long delay = System.currentTimeMillis() - playerData.lastFlying;
                    if (delay > 75L) {
                        if (count++ > 15) {
                            count -= 5;
                            if (++buffer > 5) {
                                buffer = 0;
                                handleFlag(player, "Timer B", "§b* §fNegative timer§b\n§b* §fdelay=§b" + delay, getBanVL("TimerB"), 100000L);
                            }
                        }
                    } else {
                        count = Math.max(buffer - 1, 0);
                        buffer = Math.max(buffer - 0.25, 0);
                    }
                    lastPacket = System.currentTimeMillis();
                }
            }
        }
    }
}

