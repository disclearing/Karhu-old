package me.liwk.karhu.check.impl.inventory;

import io.github.retrooper.packetevents.event.PacketEvent;
import me.liwk.karhu.check.api.Category;
import me.liwk.karhu.check.api.Check;
import me.liwk.karhu.data.PlayerData;
import me.liwk.karhu.event.WindowEvent;
import org.bukkit.entity.Player;

public class InventoryB extends Check {

    private double buffer;

    public InventoryB(final PlayerData playerData) {
        super(Category.PACKET, playerData);
    }

    @Override
    public void handle(PacketEvent event, Player player) {
        if (event instanceof WindowEvent) {
            if (
                    !playerData.isInventoryOpen()
                    && playerData.deltaXZ > 0.1
                    && !playerData.droppedPackets
                    && !playerData.isLagging(System.currentTimeMillis(), 100L)
                    && playerData.liquidTicks == 0
                    && (playerData.getTotalTicks() - playerData.getInvStamp()) > 250
                    && playerData.getVelocityH() <= 0
                    && playerData.getIceTicks() <= 0
                    && !playerData.hasFast()
            ) {
                if (++buffer > 4) {
                    handleFlag(player, "Inventory B", "§b* §fClicking inventory without it being open\n§b* §fticks=§b" + (playerData.getTotalTicks() - playerData.getInvStamp()), getBanVL("InventoryB"), 50000L);
                }
            } else {
                buffer = Math.max(buffer - 2, 0);
            }
        }
    }
}
