package me.liwk.karhu.check.impl.killaura;

import io.github.retrooper.packetevents.event.PacketEvent;
import me.liwk.karhu.check.api.Category;
import me.liwk.karhu.check.api.Check;
import me.liwk.karhu.data.PlayerData;
import me.liwk.karhu.event.AttackEvent;
import me.liwk.karhu.event.FlyingEvent;
import me.liwk.karhu.event.PositionEvent;
import org.bukkit.entity.Player;

public class KillauraG extends Check {

    private long lastFlyingTime = -1;
    private double buffer;

    public KillauraG(final PlayerData playerData) {
        super(Category.COMBAT, playerData);
    }

    @Override
    public void handle(PacketEvent event, Player player) {
        if (event instanceof FlyingEvent) {
            this.lastFlyingTime = System.currentTimeMillis();
        } else if (event instanceof AttackEvent) {
            if ((System.currentTimeMillis() - playerData.getLastAttackPacket()) < 1000L && playerData.lastServerPositionTick > 60 && playerData.getTotalTicks() - playerData.getLastPacketDrop() > 20 && !playerData.isLagging(System.currentTimeMillis(), 200L)) {
                long timeDelta = System.currentTimeMillis() - this.lastFlyingTime;
                // calculate delta so that we can check if they're sending attack packets too frequently
                if (timeDelta < 5) {
                    if(++buffer > 10) {
                        handleFlag(player, "Killaura G", "§b* §fToo small timedelta\n§b* §ftimeDelta=§b" + timeDelta, getBanVL("KillauraG"), 60000L);
                    }
                } else {
                    buffer = 0;
                }
            }
        } else if (event instanceof PositionEvent) {
            buffer = 0;
        }
    }
}
