package me.liwk.karhu.handler;

import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.event.PacketListener;
import io.github.retrooper.packetevents.event.annotation.PacketHandler;
import io.github.retrooper.packetevents.event.impl.PacketReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.utils.server.ServerVersion;
import me.liwk.karhu.Karhu;
import me.liwk.karhu.data.PlayerData;
import me.liwk.karhu.playerhandler.Handler1_13;
import me.liwk.karhu.playerhandler.Handler1_8;
import me.liwk.karhu.playerhandler.Handler1_9;
import me.liwk.karhu.util.UtilPlayer;
import org.bukkit.entity.Player;

public class MovementProcessor implements PacketListener {

    @PacketHandler
    public void onPacketReceive(PacketReceiveEvent e) {
        final Player player = e.getPlayer();
        PlayerData playerData = null;

        if (player != null) {
            playerData = Karhu.getInstance().getPlayerDataManager().getPlayerData(e.getPlayer());
        }

        if (playerData != null && playerData.isObjectLoaded()) {

            if (PacketType.Client.Util.isInstanceOfFlying(e.getPacketId())) {

                if (playerData.getLastLocation() != null && playerData.getLastLastLocation() != null) {

                    playerData.setWasOnGround(playerData.isOnGround());
                    playerData.setWasInLiquid(playerData.isInLiquid());
                    playerData.setWasUnderBlock(playerData.isUnderBlock());
                    playerData.setWasInWeb(playerData.isInWeb());
                    playerData.setInLiquid(UtilPlayer.isNearLiquid(player));
                    playerData.setInWeb(UtilPlayer.isInWeb(player));
                    playerData.setOnIce(UtilPlayer.isNearIce(player));

                    if (PacketEvents.getAPI().getServerUtils().getVersion().isHigherThan(ServerVersion.v_1_8)) {
                        if (Handler1_9.isGliding(player)) {

                        }
                    } else {
                        if (Handler1_8.isGliding(player)) {

                        }
                    }

                    if (PacketEvents.getAPI().getServerUtils().getVersion().isHigherThan(ServerVersion.v_1_12_2)) {
                        if (Handler1_13.isRiptiding(player)) {

                        }
                    }

                    if (playerData.isOnIce()) {
                        playerData.setMovementsSinceIce(0);
                    } else {
                        playerData.setMovementsSinceIce(playerData.getMovementsSinceIce() + 1);
                    }

                    if (playerData.isOnGround()) {
                        playerData.setLastGroundY(playerData.getLocation().getY());
                    }

                    playerData.setOnStairs(UtilPlayer.isOnStair(player));
                    playerData.setUnderBlock(UtilPlayer.blockNearHead(player));
                    if (playerData.isUnderBlock()) {
                        playerData.setMovementsSinceUnderBlock(0);
                    } else {
                        playerData.setMovementsSinceUnderBlock(playerData.getMovementsSinceUnderBlock() + 1);
                    }

                    if (playerData.getVelocityH() > 0) {
                        playerData.setVelocityH(playerData.getVelocityH() - 1);
                    } else {
                        if (playerData.getVelocityIds().size() == 0) playerData.setVelocityHorizontal(0);
                    }

                    if (playerData.getVelocityV() > 0) {
                        playerData.setVelocityV(playerData.getVelocityV() - 1);
                    } else {
                        if (playerData.getVelocityIds().size() == 0) playerData.setVelocityY(0);
                    }

                    double x2 = Math.floor(playerData.getLastLocation().getX());
                    double z2 = Math.floor(playerData.getLastLocation().getZ());

                    if (Math.floor(playerData.getLocation().getX()) != x2 || Math.floor(playerData.getLocation().getZ()) != z2) {
                        playerData.setLastFullblockMoved(System.currentTimeMillis());
                    }

                    if (UtilPlayer.isClimbableBlock(player)) {
                        playerData.setClimbableTicks(playerData.getClimbableTicks() + 1);
                    } else {
                        playerData.setClimbableTicks(0);
                    }

                    boolean onGroundReal = playerData.isOnGround();

                    if (onGroundReal) {

                        playerData.setAirTicks(0);
                        playerData.setGroundTicks(playerData.getGroundTicks() + 1);

                        if (playerData.getAboveBlockTicks() < 60) {
                            playerData.setAboveBlockTicks(playerData.getAboveBlockTicks() + 1);
                        }
                    } else {
                        playerData.setAirTicks(playerData.getAirTicks() + 1);
                        playerData.setGroundTicks(0);

                        if (playerData.getAboveBlockTicks() > 0) {
                            playerData.setAboveBlockTicks(playerData.getAboveBlockTicks() - 1);
                        }

                    }

                    if(playerData.isOnGround()) {
                        playerData.setClientAirTicks(0);
                    } else {
                        playerData.setClientAirTicks(playerData.getClientAirTicks() + 1);
                    }

                    if (playerData.isOnGroundPacket() && playerData.isLastOnGroundPacket() && playerData.fallY != -690000) {
                        playerData.fallDistance = Math.abs(playerData.fallY - playerData.getLocation().getY());
                        playerData.fallY = -690000;
                    }

                    if (!playerData.isOnGroundPacket() && playerData.isLastOnGroundPacket()) {
                        playerData.fallY = playerData.getLocation().getY();
                    }

                    playerData.liquidTicks = Math.max(0, UtilPlayer.isNearLiquid(player) ? Math.min(80, playerData.liquidTicks + 10) : playerData.liquidTicks - 1);
                    playerData.blockTicks = Math.max(0, playerData.isOnGround() ? Math.min(60, playerData.blockTicks + 3) : playerData.blockTicks - 1);
                    playerData.slimeTicks = Math.max(0, UtilPlayer.isNearSlime(player.getLocation()) ? Math.min(playerData.slimeTicks + 60, 120) : playerData.slimeTicks - 1);
                    playerData.slimeHeight = playerData.getSlimeTicks() > 0 ? ((Math.abs(playerData.fallDistance) * 0.05) * playerData.getSlimeTicks() * 0.05f) * 4 : 0;
                    playerData.iceTicks = Math.max(0, UtilPlayer.isNearIce(player) ? Math.min(60, playerData.iceTicks + 5) : playerData.iceTicks - 1);
                    playerData.halfTicks = Math.max(0, UtilPlayer.isOnStair2(player) || UtilPlayer.isOnSlab(player) ? Math.min(60, playerData.halfTicks + 5) : playerData.halfTicks - 1);
                    playerData.skullTicks = Math.max(0, UtilPlayer.isOnSkull(player) ? Math.min(60, playerData.skullTicks + 5) : playerData.skullTicks - 1);
                    playerData.potTicks = Math.max(0, UtilPlayer.isOnPot(player) ? Math.min(20, playerData.potTicks + 5) : playerData.potTicks - 1);
                    playerData.wallTicks = Math.max(0, UtilPlayer.isOnWall(player) || UtilPlayer.isOnFence(player) ? Math.min(60, playerData.wallTicks + 5) : playerData.wallTicks - 1);

                    if (playerData.isOnSlime()) {
                        playerData.setLastOnSlime(System.currentTimeMillis());
                    }

                    playerData.setBelowBlock(false);
                    playerData.setWasBelowBlock(false);
                }
            }
        }
    }
}