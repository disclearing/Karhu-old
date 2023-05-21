package me.liwk.karhu.check.impl.nofall;

import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.event.PacketEvent;
import io.github.retrooper.packetevents.utils.server.ServerVersion;
import me.liwk.karhu.check.api.Category;
import me.liwk.karhu.check.api.Check;
import me.liwk.karhu.data.PlayerData;
import me.liwk.karhu.event.BlockPlaceEvent;
import me.liwk.karhu.event.FlyingEvent;
import me.liwk.karhu.util.MathUtil;
import me.liwk.karhu.util.UtilPlayer;
import me.liwk.karhu.util.task.Tasker;
import org.bukkit.Location;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class NofallB extends Check {

    private int lastPlace;
    private double buffer;

    public NofallB(final PlayerData playerData) {
        super(Category.MOVEMENT, playerData);
    }

    @Override
    public void handle(PacketEvent event, Player player) {
        if (event instanceof FlyingEvent) {

            if (((FlyingEvent) event).hasMoved()) {
                Location to = ((FlyingEvent) event).toLocation();
                Location from = playerData.getLastLocation();

                final boolean serverGround = MathUtil.onGround(to.getY());

                if(!player.getAllowFlight()
                        && playerData.lastServerPositionTick > 100
                        && playerData.getTotalTicks() > 400
                        && playerData.getVelocityV() <= 0
                        && playerData.getVelocityH() <= 0
                        && player.getVehicle() == null
                        && !UtilPlayer.isClimbableBlock(player)
                        && playerData.getClimbableTicks() == 0
                        && !UtilPlayer.isOnStair2(player)
                        && !UtilPlayer.isOnSlab(player)
                        && !UtilPlayer.isNearSlime(player.getLocation())
                        && playerData.getTotalTicks() - playerData.getLastPacketDrop() > 5
                        && !playerData.hasFast()
                        && playerData.getTotalTicks() - lastPlace > 15
                        && playerData.getLiquidTicks() == 0
                        && !playerData.isLagging(System.currentTimeMillis(), 250L)) {
                    if (serverGround != ((FlyingEvent) event).isOnGround()) {
                        if(PacketEvents.getAPI().getServerUtils().getVersion().isHigherThan(ServerVersion.v_1_12_2)) {
                            Tasker.run(() -> {
                                for (Entity ent : player.getNearbyEntities(3.0, 3.0, 3.0)) {
                                    if (ent instanceof Boat) {
                                        return;
                                    }
                                }
                            });
                        } else {
                            for (Entity ent : player.getNearbyEntities(3.0, 3.0, 3.0)) {
                                if (ent instanceof Boat) {
                                    return;
                                }
                            }
                        }
                        if (++buffer > 7) {
                            handleFlag(player, "Nofall B", "§b* §fserverGround=§b" + MathUtil.onGround(to.getY()) + "\n§b* §fclientGround=§b" + playerData.isOnGroundPacket(), getBanVL("NofallB"), 60000L);
                        }

                    } else buffer = Math.max(buffer - 0.75, 0);

                } else buffer = Math.max(buffer - 0.5, 0);
            }
        } else if(event instanceof BlockPlaceEvent) {
            if (((BlockPlaceEvent) event).getItemStack().getType().isSolid()) {
                this.lastPlace = playerData.getTotalTicks();
            }
        }
    }
}
