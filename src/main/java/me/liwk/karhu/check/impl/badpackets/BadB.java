package me.liwk.karhu.check.impl.badpackets;

import io.github.retrooper.packetevents.event.PacketEvent;
import me.liwk.karhu.check.api.Category;
import me.liwk.karhu.check.api.Check;
import me.liwk.karhu.data.PlayerData;
import me.liwk.karhu.event.HeldItemSlotEvent;
import org.bukkit.entity.Player;

public class BadB extends Check {

    public BadB(final PlayerData playerData) {
        super(Category.PACKET, playerData);
    }

    @Override
    public void handle(PacketEvent event, Player player) {
        if (event instanceof HeldItemSlotEvent) {
            if(playerData.isPlacing()) {
                handleFlag(player, "BadPackets B", "§b* §fPlacing while changing slot", getBanVL("BadPacketsB"), 200000L);
            }
        }
    }
}
