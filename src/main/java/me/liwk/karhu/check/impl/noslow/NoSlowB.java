package me.liwk.karhu.check.impl.noslow;

import io.github.retrooper.packetevents.event.PacketEvent;
import io.github.retrooper.packetevents.packetwrappers.in.blockdig.WrappedPacketInBlockDig;
import me.liwk.karhu.check.api.Category;
import me.liwk.karhu.check.api.Check;
import me.liwk.karhu.data.PlayerData;
import me.liwk.karhu.event.DigEvent;
import org.bukkit.entity.Player;

public class NoSlowB extends Check {

    private int buffer;

    public NoSlowB(final PlayerData playerData) {
        super(Category.MOVEMENT, playerData);
    }

    @Override
    public void handle(PacketEvent event, Player player) {
        if (event instanceof DigEvent) {
            if(((DigEvent) event).getDigType().equals(WrappedPacketInBlockDig.PlayerDigType.RELEASE_USE_ITEM) && playerData.isPlacing() && playerData.getDeltaXZ() > 0.125) {
                if(++buffer > 3) {
                    handleFlag(player, "NoSlow B", "§b* §fdeltaXZ=§b" + playerData.deltaXZ + "\n§b* §fSending bad block packets", getBanVL("NoSlowB"), 40000L);
                }
            } else {
                buffer = 0;
            }
        }
    }
}
