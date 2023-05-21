package me.liwk.karhu.check.impl.jesus;

import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.event.PacketEvent;
import io.github.retrooper.packetevents.utils.server.ServerVersion;
import me.liwk.karhu.check.api.Category;
import me.liwk.karhu.check.api.Check;
import me.liwk.karhu.data.PlayerData;
import me.liwk.karhu.event.FlyingEvent;
import me.liwk.karhu.util.UtilPlayer;
import me.liwk.karhu.util.task.Tasker;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class JesusA extends Check {

    private double buffer, buffer2;
    
    public JesusA(final PlayerData playerData) {
        super(Category.MOVEMENT, playerData);
    }

    @Override
    public void handle(PacketEvent event, Player player) {
        if (event instanceof FlyingEvent) {

            if (((FlyingEvent) event).hasMoved()) {

                if(player.isFlying() || UtilPlayer.isOnSlab(player) || UtilPlayer.isOnBadJesusBlock(player) || UtilPlayer.isOnLily(player)) return;

                //Bukkit.broadcastMessage("§7[§c§lPRE-DEBUG§7] §fl: §c" + UtilPlayer.isOnLily(player) + " §fb: §c" + UtilPlayer.isOnBadJesusBlock(player));

                if(player.isOnGround() && UtilPlayer.isAboveLiquid(player)) {
                    if (++buffer2 > 3) {
                        handleFlag(player, "Jesus A", "§b* §fdeltaY=§b" + playerData.deltaY + "\n§b* §fdeltaXZ=§b" + playerData.getDeltaXZ(), getBanVL("JesusA"), 60000L);
                    }
                } else {
                    buffer2 = Math.max(buffer2 - 1, 0);
                    if(UtilPlayer.isAboveLiquid(player) && Math.abs(playerData.deltaY) < 1.0E-4 && playerData.getDeltaXZ() > 0.07D && System.currentTimeMillis() - playerData.getLastVehicle() > 2250L && player.getVehicle() == null) {
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
                        if (++buffer > 4) {
                            handleFlag(player, "Jesus A", "§b* §fdeltaY=§b" + playerData.deltaY + "\n§b* §fdeltaXZ=§b" + playerData.getDeltaXZ(), getBanVL("JesusA"), 60000L);
                        }
                        handleVerbose(player, "Jesus A", "§b* §fdeltaY=§b" + playerData.deltaY + "\n§b* §fdeltaXZ=§b" + playerData.getDeltaXZ());

                    } else {
                        buffer = Math.max(buffer - 1, 0);
                    }
                }

            }
        }
    }
}
