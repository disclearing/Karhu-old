package me.liwk.karhu.check.impl.velocity;

import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.event.PacketEvent;
import io.github.retrooper.packetevents.utils.player.ClientVersion;
import me.liwk.karhu.check.api.Category;
import me.liwk.karhu.check.api.Check;
import me.liwk.karhu.data.PlayerData;
import me.liwk.karhu.event.FlyingEvent;
import me.liwk.karhu.util.MathUtil;
import me.liwk.karhu.util.UtilPlayer;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class VelocityB extends Check {

    private double buffer;

    public VelocityB(final PlayerData playerData) {
        super(Category.COMBAT, playerData);
    }

    @Override
    public void handle(PacketEvent event, Player player) {
        if (event instanceof FlyingEvent) {
            Location to = ((FlyingEvent) event).toLocation();
            int velTicks = playerData.getTotalTicks() - playerData.getVelocityTicks();
            
            double lastVelX = playerData.getLastVelocityX(), lastVelZ = playerData.getLastVelocityZ();

            if (playerData.getLastLocation() != null && playerData.getLastLastLocation() != null) {
                if (!MathUtil.onGround(to.getY()) && isTouchingAir(player.getLocation())) {
                    if (MathUtil.onGround(playerData.getLastLocation().getY()) && playerData.isLastLastOnGroundPacket() && !UtilPlayer.blockNearHead(player) && !UtilPlayer.isNearWall(player) && !UtilPlayer.isNextToWall(player) && !UtilPlayer.isInWeb(player)) {
                        if (velTicks == 1 && playerData.lastServerPositionTick > 120) {
                            double velFXZ = MathUtil.hypot(lastVelX, lastVelZ);
                            double velFTXZ = MathUtil.hypot(to.getX() - playerData.getLastLocation().getX(), to.getZ() - playerData.getLastLocation().getZ());
                            double velFFTXZ = MathUtil.hypot(playerData.getLastLastLocation().getX() - playerData.getLastLocation().getX(), playerData.getLastLastLocation().getZ() - playerData.getLastLocation().getZ());
                            double shit = Math.max(velFTXZ, velFFTXZ) / velFXZ; // With this you can calculate the AVG % of the velocity modification
                            double maxShit = PacketEvents.getAPI().getPlayerUtils().getClientVersion(player).isHigherThan(ClientVersion.v_1_8) ? 0.65 : 0.99;
                            if (playerData.getDeltaXZ() <= playerData.getVelocityHorizontal() * maxShit && shit < maxShit && velFXZ > 0.2) {
                                if ((buffer += 1.1 - shit) > 3.5) {
                                    handleFlag(player, "Velocity B", "§b* §fHorizontal velocity§b\n§b* §fest ptc=§b" + (shit * 100) + "§f%", getBanVL("VelocityB"), 100000L);
                                    buffer = 0;
                                }
                            } else {
                                buffer -= Math.min(buffer, 1.1);
                            }
                        }
                    }
                } else {
                    if (MathUtil.onGround(playerData.getLastLocation().getY()) && !UtilPlayer.blockNearHead(player) && !UtilPlayer.isNearWall(player) && !UtilPlayer.isNextToWall(player)) {
                        if (velTicks == 1 && playerData.lastServerPositionTick > 120) {
                            double velFXZ = MathUtil.hypot(lastVelX, lastVelZ);
                            double velFTXZ = MathUtil.hypot(to.getX() - playerData.getLastLocation().getX(), to.getZ() - playerData.getLastLocation().getZ());
                            double velFFTXZ = MathUtil.hypot(playerData.getLastLastLocation().getX() - playerData.getLastLocation().getX(), playerData.getLastLastLocation().getZ() - playerData.getLastLocation().getZ());
                            double shit = Math.max(velFTXZ, velFFTXZ) / velFXZ; // With this you can calculate the AVG % of the velocity modification
                            if (playerData.getDeltaXZ() <= playerData.getVelocityHorizontal() * 0.5 && shit < 0.4D && velFXZ > 0.2) {
                                if ((buffer += 1.1 - shit) > 3.5) {
                                    handleFlag(player, "Velocity B", "§b* §fHorizontal velocity§b\n§b* §fest ptc=§b" + (int) (shit * 100) + "§f%", getBanVL("VelocityB"), 100000L);
                                    buffer = 0;
                                }
                            } else {
                                buffer -= Math.min(buffer, 1.1);
                            }
                        }
                    }
                }
            }
        }
    }
    public boolean isTouchingAir(Location location) {
        if (!location.getWorld().isChunkLoaded(location.getBlockX() >> 4, location.getBlockZ() >> 4)) {
            return true;
        }
        Block block = location.getWorld().getBlockAt(location.getBlockX(), location.getBlockY() + 2, location.getBlockZ());
        return !block.getType().isSolid();
    }
    private String dogshit(boolean value) {
        return value ? "%%__TIMESTAMP__%%" : "%%__USER__%%";
    }
}
