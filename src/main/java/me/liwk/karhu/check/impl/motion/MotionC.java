package me.liwk.karhu.check.impl.motion;

import io.github.retrooper.packetevents.event.PacketEvent;
import me.liwk.karhu.check.api.Category;
import me.liwk.karhu.check.api.Check;
import me.liwk.karhu.data.PlayerData;
import me.liwk.karhu.event.FlyingEvent;
import me.liwk.karhu.util.UtilPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class MotionC extends Check {

    private int buffer, buffer2;
    private int ticks;
    private int lastPlace;

    public MotionC(final PlayerData playerData) {
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
                        || UtilPlayer.isClimbableBlock(player)
                        || playerData.getClimbableTicks() != 0
                        || playerData.halfTicks > 0
                        || playerData.getVelocityV() > 0
                        || playerData.getTotalTicks() - lastPlace < 10
                        || player.getAllowFlight()
                        || UtilPlayer.isNearWeb(player)
                        || playerData.liquidTicks > 0
                        || playerData.getDeltaY() == 0
                        || playerData.lastServerPositionTick < 120
                        || playerData.getTotalTicks() - playerData.getLastTeleportReset() < 40
                        || playerData.getTotalTicks() < 100
                        || playerData.slimeTicks > 0) return;

                if(UtilPlayer.isNearTrapdoor(player.getLocation()) || UtilPlayer.isNearIce(player)) {
                    buffer = 0;
                    return;
                }

                if (player.getLocation().getWorld().isChunkLoaded(player.getLocation().getBlockX() >> 4, player.getLocation().getBlockZ() >> 4)) {
                    buffer = Math.max(buffer - 1, 0);
                    return;
                }

                if (playerData.getDeltaY() == playerData.getLastDeltaY() && (playerData.getTeleportLocation() == null && System.currentTimeMillis() - playerData.getLastTeleport() > 2250L)) {
                    if(++buffer > 2) {
                        handleFlag(player, "Motion C", "§b* §fNot following MCP jump laws\n§b* §fMotion=§b" + playerData.getDeltaY(), getBanVL("MotionA"), 60000L);
                    }
                } else {
                    buffer = Math.max(buffer - 1, 0);
                }

                if(!UtilPlayer.isNearGround(player.getLocation()) && isAgainstBlock(player.getLocation())) {
                    if(++ticks > 7) {
                        if(playerData.getDeltaY() > 0.1) {
                            if(++buffer2 > 3) {
                                handleFlag(player, "Motion C", "§b* §fClimbing a wall\n§b* §fMotion=§b" + playerData.getDeltaY(), getBanVL("MotionA"), 60000L);
                            }
                        } else {
                            buffer2 = Math.max(buffer2 - 1, 0);
                        }
                    }
                } else {
                    ticks = 0;
                }
            }
        }
    }
    private boolean isAgainstBlock(Location loc) {
        double expand = 0.3;

        if (!loc.getWorld().isChunkLoaded(loc.getBlockX() >> 4, loc.getBlockZ() >> 4)) {
            return false;
        }

        for (double x = -expand; x <= expand; x += expand) {
            for (double z = -expand; z <= expand; z += expand) {
                if (loc.clone().add(x, 0.0001, z).getBlock().getType().isSolid()) {
                    return true;
                }
            }
        }
        for (double x = -expand; x <= expand; x += expand) {
            for (double z = -expand; z <= expand; z += expand) {
                if (loc.clone().add(x, 1.0001, z).getBlock().getType().isSolid()) {
                    return true;
                }
            }
        }
        return false;
    }
}
