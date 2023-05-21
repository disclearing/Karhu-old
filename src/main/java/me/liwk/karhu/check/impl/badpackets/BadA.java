package me.liwk.karhu.check.impl.badpackets;

import io.github.retrooper.packetevents.event.PacketEvent;
import me.liwk.karhu.check.api.Category;
import me.liwk.karhu.check.api.Check;
import me.liwk.karhu.data.PlayerData;
import me.liwk.karhu.event.FlyingEvent;
import org.bukkit.entity.Player;

public class BadA extends Check {

    public BadA(final PlayerData playerData) {
        super(Category.PACKET, playerData);
    }

    @Override
    public void handle(PacketEvent event, Player player) {
        if (event instanceof FlyingEvent) {
            if (((FlyingEvent) event).hasLooked() || ((FlyingEvent) event).hasMoved()) {
                if(Math.abs(((FlyingEvent) event).getPitch()) > 90 && playerData.getTeleportLocation() == null) {
                    handleFlag(player, "BadPackets A", "§b* §fInvalid pitch", getBanVL("BadPacketsA"), 200000L);
                }
            }
        }
    }

}
