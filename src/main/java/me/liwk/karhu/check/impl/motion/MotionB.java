package me.liwk.karhu.check.impl.motion;

import io.github.retrooper.packetevents.event.PacketEvent;
import me.liwk.karhu.check.api.Category;
import me.liwk.karhu.check.api.Check;
import me.liwk.karhu.data.PlayerData;
import me.liwk.karhu.event.FlyingEvent;
import me.liwk.karhu.util.UtilPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class MotionB extends Check {

    private double buffer;

    public MotionB(final PlayerData playerData) {
        super(Category.MOVEMENT, playerData);
    }

    @Override
    public void handle(PacketEvent event, Player player) {
        if (event instanceof FlyingEvent) {

            if (((FlyingEvent) event).hasMoved()) {
                Location to = ((FlyingEvent) event).toLocation();
                Location from = playerData.getLastLocation();
                float deltaY = playerData.getDeltaY();
                float expected = UtilPlayer.blockNearHead(player) ? 0.200000047f : 0.42f;

                if (UtilPlayer.isNearLiquid(player)
                        || UtilPlayer.isNearHoney(player)
                        || UtilPlayer.isNearScaffolding(player)
                        || UtilPlayer.isNearClimbable(player)
                        || player.isInsideVehicle()
                        || player.getAllowFlight()
                        || playerData.lastServerPositionTick < 80
                        || playerData.getVelocityV() > 0
                        || playerData.getVelocityH() > 0
                        || playerData.droppedPackets
                        || playerData.halfTicks > 0
                        || playerData.potTicks > 0
                        || UtilPlayer.isOnLily(player)
                        || UtilPlayer.isOnCarpet(player)
                        || playerData.slimeTicks > 0) return;

                if (to.getY() % 1.0 > 0.0 && from.getY() % 1.0 == 0.0 && deltaY > 0 && (playerData.getTeleportLocation() == null && System.currentTimeMillis() - playerData.getLastTeleport() > 3250L)) {
                    if (deltaY < expected) {
                        if (++buffer > 12) {
                            handleFlag(player, "Motion B", "§b* §fNot following MCP jump laws\n§b* §fy=§b" + deltaY, getBanVL("MotionB"), 60000L);
                        }
                    } else {
                        buffer = Math.max(buffer - 2, 0.0);
                    }
                }
            }
        }
    }
}