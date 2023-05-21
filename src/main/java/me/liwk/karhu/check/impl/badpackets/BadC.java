package me.liwk.karhu.check.impl.badpackets;

import io.github.retrooper.packetevents.event.PacketEvent;
import me.liwk.karhu.check.api.Category;
import me.liwk.karhu.check.api.Check;
import me.liwk.karhu.data.PlayerData;
import me.liwk.karhu.event.AbilityEvent;
import org.bukkit.entity.Player;

public class BadC extends Check {

    public BadC(final PlayerData playerData) {
        super(Category.PACKET, playerData);
    }

    @Override
    public void handle(PacketEvent event, Player player) {
        if (event instanceof AbilityEvent) {
            if(!player.getAllowFlight()) {
                handleFlag(player, "BadPackets C", "§b* §fSent ability packet without flying", getBanVL("BadPacketsC"), 6000L);
            }
        }
    }
}

