package me.liwk.karhu.check.impl.badpackets;

import io.github.retrooper.packetevents.event.PacketEvent;
import me.liwk.karhu.check.api.Category;
import me.liwk.karhu.check.api.Check;
import me.liwk.karhu.data.PlayerData;
import me.liwk.karhu.event.HeldItemSlotEvent;
import org.bukkit.entity.Player;

public class BadE extends Check {

    private int lastSlot = 69;

    public BadE(final PlayerData playerData) {
        super(Category.PACKET, playerData);
    }

    @Override
    public void handle(PacketEvent event, Player player) {
        if (event instanceof HeldItemSlotEvent) {
            if (lastSlot == 69) {
                return;
            }
            if (((HeldItemSlotEvent) event).getSlot() == lastSlot) {
                handleFlag(player, "BadPackets E", "§b* §fsent 2 same held slot packets", getBanVL("BadPacketsE"), 6000L);
            }
            lastSlot = ((HeldItemSlotEvent) event).getSlot();
        }
    }
}
