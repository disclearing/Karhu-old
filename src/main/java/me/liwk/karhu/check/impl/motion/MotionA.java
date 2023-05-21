package me.liwk.karhu.check.impl.motion;

import io.github.retrooper.packetevents.event.PacketEvent;
import me.liwk.karhu.check.api.Category;
import me.liwk.karhu.check.api.Check;
import me.liwk.karhu.data.PlayerData;
import me.liwk.karhu.event.FlyingEvent;
import me.liwk.karhu.util.MathUtil;
import me.liwk.karhu.util.UtilPlayer;
import org.bukkit.entity.Player;

public class MotionA extends Check {

    private double buffer;

    public MotionA(final PlayerData playerData) {
        super(Category.MOVEMENT, playerData);
    }

    @Override
    public void handle(PacketEvent event, Player player) {
        if (event instanceof FlyingEvent) {

            if (((FlyingEvent) event).hasMoved()) {

                if(UtilPlayer.isNearLiquid(player)
                        || UtilPlayer.isNearHoney(player)
                        || player.isInsideVehicle()
                        || UtilPlayer.isNearAnvil(player)
                        || UtilPlayer.isNearScaffolding(player)
                        || UtilPlayer.isNearClimbable(player)
                        || System.currentTimeMillis() - playerData.getLastMovePkt() > 2250L
                        || playerData.getVelocityV() > 0
                        || player.getAllowFlight()
                        || playerData.getDeltaY() == 0
                        || playerData.lastServerPositionTick < 60 + MathUtil.getPingInTicks(playerData.getTransPing())
                        || playerData.slimeTicks > 0) return;

                if(UtilPlayer.isNearTrapdoor(player.getLocation()) || UtilPlayer.isNearIce(player)) {
                    buffer = 0;
                    return;
                }

                if (playerData.getDeltaY() == -playerData.getLastDeltaY() && (playerData.getTeleportLocation() == null && System.currentTimeMillis() - playerData.getLastTeleport() > 3250L)) {
                    if(++buffer > 3) {
                        handleFlag(player, "Motion A", "§b* §fNot following MCP jump laws\n§b* §fMotion=§b" + playerData.getDeltaY(), getBanVL("MotionA"), 60000L);
                    }
                } else {
                    buffer = Math.max(buffer - 1, 0.0);
                }
            }
        }
    }
}
