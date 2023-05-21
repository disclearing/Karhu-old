package me.liwk.karhu.check.impl.fastladder;

import io.github.retrooper.packetevents.event.PacketEvent;
import me.liwk.karhu.check.api.Category;
import me.liwk.karhu.check.api.Check;
import me.liwk.karhu.data.PlayerData;
import me.liwk.karhu.event.FlyingEvent;
import me.liwk.karhu.util.UtilPlayer;
import org.bukkit.entity.Player;

public class FastLadderA extends Check {

    public FastLadderA(final PlayerData playerData) {
        super(Category.MOVEMENT, playerData);
    }

    @Override
    public void handle(PacketEvent event, Player player) {
        if (event instanceof FlyingEvent) {

            if (((FlyingEvent) event).hasMoved()) {
                if(playerData.getClimbableTicks() <= 15
                        || playerData.getTotalTicks() - playerData.getLastFlyTick() < 40
                        || UtilPlayer.isNearGround(player.getLocation())
                        || playerData.getVelocityV() > 0
                        || playerData.getVelocityH() > 0) return;
                if(playerData.deltaY >= 0.15) {
                    handleFlag(player, "FastLadder A", "§b* §fdeltaY=§b" + playerData.deltaY + "\n§b* §fmaxSpeed=§b0.15", getBanVL("FastLadderA"), 60000L);
                }
            }
        }
    }
}
