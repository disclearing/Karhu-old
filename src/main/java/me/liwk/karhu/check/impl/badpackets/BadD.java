package me.liwk.karhu.check.impl.badpackets;

import io.github.retrooper.packetevents.event.PacketEvent;
import me.liwk.karhu.check.api.Category;
import me.liwk.karhu.check.api.Check;
import me.liwk.karhu.data.PlayerData;
import me.liwk.karhu.event.FlyingEvent;
import org.bukkit.entity.Player;

public class BadD extends Check {

    private int buffer;

    public BadD(final PlayerData playerData) {
        super(Category.PACKET, playerData);
    }

    @Override
    public void handle(PacketEvent event, Player player) {
        if (event instanceof FlyingEvent) {

            if(playerData.getTotalTicks() < 300) {
                return;
            }

            if(playerData.getPing() == 0 && playerData.getTransPing() > 1) {
                if(buffer > 5) {
                    handleFlag(player, "BadPackets D", "§b* §fNull ping\n§b* §fKPing=§b" + playerData.getPing() + "\n§b* §fTPing=§b" + playerData.getTransPing(), getBanVL("BadPacketsD"), 6000L);
                }
            } else {
                buffer = 0;
            }
        }
    }
}
