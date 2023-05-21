package me.liwk.karhu.check.impl.fly;

import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.event.PacketEvent;
import io.github.retrooper.packetevents.utils.server.ServerVersion;
import me.liwk.karhu.check.api.Category;
import me.liwk.karhu.check.api.Check;
import me.liwk.karhu.data.PlayerData;
import me.liwk.karhu.event.BlockPlaceEvent;
import me.liwk.karhu.event.FlyingEvent;
import me.liwk.karhu.util.MathUtil;
import me.liwk.karhu.util.MovementUtils;
import me.liwk.karhu.util.UtilPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class FlyA extends Check {

    private boolean lastOnGround, lastLastOnGround;
    private int lastPlace;
    private int lastSlimePlace;
    private double buffer = 0.0d;
    private int ticks;

    public FlyA(final PlayerData playerData) {
        super(Category.MOVEMENT, playerData);
    }

    @Override
    public void handle(PacketEvent event, Player player) {
        if (event instanceof FlyingEvent) {

            if (((FlyingEvent) event).hasMoved() && (playerData.getDeltaY() != 0.0D || playerData.getDeltaXZ() != 0.0D)) {
                Location to = ((FlyingEvent) event).toLocation();
                Location from = playerData.getLastLocation();

                double deltaY = playerData.deltaY;
                double accel = Math.abs(deltaY - playerData.lastDeltaY);

                boolean onGround = UtilPlayer.isOnGroundBB(player);

                boolean lastOnGround = this.lastOnGround;
                this.lastOnGround = onGround;

                boolean lastLastOnGround = this.lastLastOnGround;
                this.lastLastOnGround = lastOnGround;

                if(playerData.getTotalTicks() - playerData.getLastFlyTick() < 40
                        || playerData.isInLiquid()
                        || playerData.getLiquidTicks() > 0
                        || playerData.getTotalTicks() - lastPlace <= 5
                        || playerData.getTotalTicks() - lastSlimePlace <= 100
                        || onGround
                        || lastOnGround
                        || lastLastOnGround
                        || UtilPlayer.isNearHoney(player)
                        || playerData.getTotalTicks() - playerData.getLastPacketDrop() < 5
                        || (playerData.lastServerPositionTick < 55 + Math.min(MathUtil.getPingInTicks(playerData.getTransPing()), 5))
                        || playerData.isWasInLiquid()
                        || event.getTimestamp() - playerData.getLastVehicle() < 2000L
                        || playerData.getTotalTicks() < 80
                        || playerData.getTotalTicks() - playerData.getLastPacketDrop() < 10
                        || System.currentTimeMillis() - playerData.getLastGlide() < 4250L
                        || UtilPlayer.isNearScaffolding(player)
                        || UtilPlayer.isClimbableBlock(player)
                        || playerData.isLagging(System.currentTimeMillis(), 100L)
                        || playerData.getVelocityV() > 0
                        || playerData.getVelocityH() > 0
                        || UtilPlayer.isOnFlyABad(player)) {
                    return;
                }

                if (from.getX() == to.getX() && from.getY() == to.getY() && from.getZ() == to.getZ()) {
                    return;
                }

                if(PacketEvents.getAPI().getServerUtils().getVersion().isHigherThan(ServerVersion.v_1_7_10)) {
                    if (!player.getWorld().isChunkLoaded(player.getLocation().getBlockX() >> 4, player.getLocation().getBlockZ() >> 4)) {
                        return;
                    }
                }

                float limit = playerData.airTicks <= 20 ? 0.0f + MovementUtils.getPotionEffectLevel(player, PotionEffectType.JUMP) * 0.2f : 0.0f;

                limit += System.currentTimeMillis() - playerData.getLastGlide() < 4400L ? 4.2 : 0;
                limit += System.currentTimeMillis() - playerData.getLastRiptide() < 4400L ? 4.2 : 0;
                limit += playerData.getSlimeHeight();

                if ((playerData.getTeleportLocation() == null && System.currentTimeMillis() - playerData.getLastTeleport() > 2250L)) {
                    if(from.getX() != to.getX() || from.getZ() != to.getZ() || from.getY() != to.getY()) {

                        if (playerData.getAirTicks() > 14) {
                            if ((deltaY >= limit || accel == 0.0) && !UtilPlayer.isNearWeb(player)) {
                                handleFlag(player, "Fly A", "§b* §fdeltaY=§b" + deltaY + "\n§b* §faccel=§b" + accel + "\n§b* §fairTicks=§b" + playerData.getAirTicks() + "\n§b* §fdeltaH=§b" + playerData.getDeltaXZ(), getBanVL("FlyA"), 60000L);
                            }
                        }
                    }
                }
            }
        } else if(event instanceof BlockPlaceEvent) {
            if (((BlockPlaceEvent) event).getItemStack().getType().isSolid()) {
                this.lastPlace = playerData.getTotalTicks();
            }
            if(((BlockPlaceEvent) event).getItemStack().toString().contains("SLIME")) {
                this.lastSlimePlace = playerData.getTotalTicks();
            }
        }
    }
}