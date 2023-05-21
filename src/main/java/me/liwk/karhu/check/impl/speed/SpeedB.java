package me.liwk.karhu.check.impl.speed;

import io.github.retrooper.packetevents.event.PacketEvent;
import me.liwk.karhu.check.api.Category;
import me.liwk.karhu.check.api.Check;
import me.liwk.karhu.data.PlayerData;
import me.liwk.karhu.event.FlyingEvent;
import me.liwk.karhu.util.UtilPlayer;
import org.bukkit.entity.Player;

public class SpeedB extends Check {

    private int buffer;
    private double lastDeltaH;

    public SpeedB(final PlayerData playerData) {
        super(Category.MOVEMENT, playerData);
    }

    @Override
    public void handle(PacketEvent event, Player player) {
        if (event instanceof FlyingEvent) {

            if(((FlyingEvent) event).hasMoved()) {

                if (player.getAllowFlight() || playerData.isInLiquid() || UtilPlayer.isOnGroundBB(player)
                        || ((FlyingEvent) event).isOnGround()
                        || UtilPlayer.isClimbableBlock(player)
                        || playerData.lastServerPositionTick < 120
                        || playerData.getVelocityH() > 0
                        || player.getWalkSpeed() != 0.2
                        || System.currentTimeMillis() - playerData.getLastRiptide() < 4250L
                        || System.currentTimeMillis() - playerData.getLastGlide() < 4250L
                        || playerData.isInWeb()) {
                    return;
                }


                if (playerData.getDeltaXZ() <= 0.005) { return; } // dont wanna flag if they arent moving

                double deltaH = Math.hypot(playerData.getDeltaX(), playerData.getDeltaZ());

                double diff = lastDeltaH * 0.91F + 0.02; // calculate difference between movements

                if(playerData.isSprinting()) diff += 0.0063;

                double shit = deltaH - diff;

                if (shit > 0.000000000001 && diff > 0.08 && deltaH > 0.15) { // catches alot of shitty hops that edit friction even a bit
                    if (++buffer > 8) {
                        this.handleFlag(player, "Speed B", "§b* §fIgnoring friction\n§b* §fdiff=§b" + shit, getBanVL("SpeedB"), 60000L);
                        buffer /= 2;
                    }
                } else {
                    buffer = 0;
                }
                lastDeltaH = deltaH;
            }
        }
    }
}

