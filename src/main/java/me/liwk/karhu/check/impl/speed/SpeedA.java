package me.liwk.karhu.check.impl.speed;

import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.event.PacketEvent;
import io.github.retrooper.packetevents.utils.server.ServerVersion;
import me.liwk.karhu.check.api.Category;
import me.liwk.karhu.check.api.Check;
import me.liwk.karhu.data.PlayerData;
import me.liwk.karhu.event.FlyingEvent;
import me.liwk.karhu.playerhandler.Handler1_13;
import me.liwk.karhu.util.MovementUtils;
import me.liwk.karhu.util.UtilPlayer;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class SpeedA extends Check {

    private double buffer, buffer2;

    public SpeedA(final PlayerData playerData) {
        super(Category.MOVEMENT, playerData);
    }

    @Override
    public void handle(PacketEvent event, Player player) {
        if (event instanceof FlyingEvent) {

            if(((FlyingEvent) event).hasMoved()) {

                float maxSpeed = (((FlyingEvent) event).isOnGround() && !UtilPlayer.isOnLily(player)) ? 0.3125f : 0.3585f;
                float maxSpeed2 = (((FlyingEvent) event).isOnGround() && !UtilPlayer.isOnLily(player)) ? 0.6f : 1.0f;

                float speedLevel = MovementUtils.getPotionEffectLevel(player, PotionEffectType.SPEED);
                float depthStriderLevel = MovementUtils.getDepthStriderLevel(player);
                float dolphinLevel = 0;

                if(PacketEvents.getAPI().getServerUtils().getVersion().isHigherThan(ServerVersion.v_1_12_2)) {
                    dolphinLevel = Handler1_13.getDolphinLevel(player);
                }

                if (player.isFlying()
                        || player.getAllowFlight()
                        || playerData.getTeleportLocation() != null
                        || player.getGameMode().equals(GameMode.CREATIVE)
                        || player.isInsideVehicle()
                        || event.getTimestamp() - playerData.getLastVehicle() < 2000L
                        || (playerData.getTotalTicks() - playerData.lastTeleportReset) < 25
                        || playerData.getDeltaXZ() <= 0.01) return;


                maxSpeed2 += playerData.getGroundTicks() < 5 ? speedLevel * 0.07f : speedLevel * 0.0573f;
                maxSpeed2 *= playerData.iceTicks > 0 ? 4.4f : 1.0;

                maxSpeed += playerData.getGroundTicks() < 5 ? speedLevel * 0.07f : speedLevel * 0.0573f;
                maxSpeed *= playerData.iceTicks > 0 ? 4.4f : 1.0;

                if(playerData.iceTicks == 0) {
                    if(UtilPlayer.isNearIce(player)) {
                        maxSpeed *= 4.4f;
                        maxSpeed2 *= 4.4f;
                    }
                }

                maxSpeed *= playerData.slimeTicks > 0 ? 1.25f : 1.0;
                maxSpeed += UtilPlayer.blockNearHead(player) ? 0.25 : 0.0;

                if(playerData.getTotalTicks() - playerData.getLastFlyTick() < 40) {
                    maxSpeed += 0.3;
                    maxSpeed2 += 0.3;
                }

                if(playerData.getLiquidTicks() > 0) {
                    maxSpeed += dolphinLevel * 0.15f;
                    maxSpeed += (depthStriderLevel * 0.45f);
                }

                maxSpeed += (player.getWalkSpeed() - 0.2) * 2.5;
                maxSpeed += (player.getFlySpeed() - 0.1) * 2.5;

                maxSpeed2 += (player.getWalkSpeed() - 0.2) * 2.5;
                maxSpeed2 += (player.getFlySpeed() - 0.1) * 2.5;

                maxSpeed *= (UtilPlayer.isOnStair(player) || UtilPlayer.isOnStair2(player)) || UtilPlayer.isOnSlab(player) ? 1.5f : 1.0;

                maxSpeed += playerData.getVelocityHorizontal();
                maxSpeed2 += playerData.getVelocityHorizontal();

                if(playerData.isUnderBlock())
                    maxSpeed += 0.26;

                maxSpeed += System.currentTimeMillis() - playerData.getLastGlide() < 4400L ? 2 : 0;
                maxSpeed2 += System.currentTimeMillis() - playerData.getLastGlide() < 4400L ? 1.7 : 0;
                maxSpeed += System.currentTimeMillis() - playerData.getLastRiptide() < 4400L ? 2 : 0;
                maxSpeed2 += System.currentTimeMillis() - playerData.getLastRiptide() < 4400L ? 1.7 : 0;

                //Bukkit.broadcastMessage("§7[§c§lPOST-DEBUG§7] §fS: §c" + playerData.getDeltaXZ() + " §fM: §c" + maxSpeed);

                if (playerData.deltaXZ > maxSpeed) {
                    if(++buffer > 15) {
                        buffer -= 2;
                        handleFlag(player, "Speed A", "§b* §fdeltaXZ=§b" + playerData.deltaXZ + "\n§b* §fmaxSpeed=§b" + maxSpeed, getBanVL("SpeedA"), 60000L);
                    }
                } else {
                    buffer -= buffer > 0 ? 1 : 0;
                }

                if(playerData.deltaXZ > maxSpeed2 && !playerData.isLagging2(System.currentTimeMillis(), 100L)) {
                    if((buffer2 += 10) > 30) {
                        handleFlag(player, "Speed A", "§b* §fdeltaXZ=§b" + playerData.deltaXZ + "\n§b* §fmaxSpeed=§b" + maxSpeed2, getBanVL("SpeedA"), 60000L);
                    }
                } else {
                    buffer2 -= buffer2 > 0 ? 1 : 0;
                }
            }
        }
    }
}
