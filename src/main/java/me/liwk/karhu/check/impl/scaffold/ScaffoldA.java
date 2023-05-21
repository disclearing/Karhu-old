package me.liwk.karhu.check.impl.scaffold;

import io.github.retrooper.packetevents.event.PacketEvent;
import me.liwk.karhu.check.api.Category;
import me.liwk.karhu.check.api.Check;
import me.liwk.karhu.data.PlayerData;
import me.liwk.karhu.event.BlockPlaceEvent;
import me.liwk.karhu.event.FlyingEvent;
import org.bukkit.entity.Player;

public class ScaffoldA extends Check {

    private long lastFlying = -1;
    private int buffer;

    public ScaffoldA(final PlayerData playerData) {
        super(Category.MOVEMENT, playerData);
    }

    @Override
    public void handle(PacketEvent event, Player player) {
        if (event instanceof FlyingEvent) {
            lastFlying = System.currentTimeMillis();
        } else if (event instanceof BlockPlaceEvent) {
            if(((BlockPlaceEvent) event).getItemStack().getType().isSolid()) {
                if ((System.currentTimeMillis() - lastFlying) < 10L && !playerData.isLagging(System.currentTimeMillis(), 200L) && playerData.lastServerPositionTick > 20 && playerData.getTotalTicks() - playerData.getLastPacketDrop() > 20) {
                    if (++buffer > 8) {
                        handleFlag(player, "Scaffold A", "§b* §fPost scaffold\n§b* §ftime=§b" + (System.currentTimeMillis() - lastFlying), getBanVL("ScaffoldA"), 60000L);
                    }
                } else {
                    buffer = 0;
                }
            }
        }
    }
}
