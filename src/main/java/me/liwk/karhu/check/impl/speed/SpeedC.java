package me.liwk.karhu.check.impl.speed;

import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.event.PacketEvent;
import io.github.retrooper.packetevents.utils.server.ServerVersion;
import me.liwk.karhu.check.api.Category;
import me.liwk.karhu.check.api.Check;
import me.liwk.karhu.data.PlayerData;
import me.liwk.karhu.event.FlyingEvent;
import me.liwk.karhu.playerhandler.Handler1_13;
import me.liwk.karhu.playerhandler.Handler1_8;
import me.liwk.karhu.util.MovementUtils;
import me.liwk.karhu.util.UtilPlayer;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class SpeedC extends Check {

    private double lastOffsetH, lastDeltaH;
    private double friction = 0.91;
    private double buffer;

    public SpeedC(final PlayerData playerData) {
        super(Category.MOVEMENT, playerData);
    }

    @Override
    public void handle(PacketEvent event, Player player) {
        if (event instanceof FlyingEvent) {

            if (((FlyingEvent) event).hasMoved()) {

                Location to = ((FlyingEvent) event).toLocation();
                Location from = playerData.getLastLocation();

                if (player.getAllowFlight()
                        || UtilPlayer.isClimbableBlock(player)
                        || player.getVehicle() != null
                        || player.getGameMode().equals(GameMode.CREATIVE)
                        || Handler1_8.isSpectating(player)
                        || System.currentTimeMillis() - playerData.getLastRiptide() < 4250L
                        || System.currentTimeMillis() - playerData.getLastGlide() < 4250L
                        || playerData.isRiptiding()
                        || playerData.isGliding()) {
                    return;
                }

                float speedLevel = MovementUtils.getPotionEffectLevel(player, PotionEffectType.SPEED);
                float soulSpeedEnchant = Handler1_13.getSoulSpeedEnchant(player);


                double dx = to.getX() - from.getX();
                double dz = to.getZ() - from.getZ();

                //double offsetH = MathUtil.hypot(dx, dz); //Calculating the offset
                double deltaH = Math.sqrt(dx * dx + dz * dz); //Calculating the offset
                double offsetY = to.getY() - from.getY(); //Calculating the offset

                double jumpHeight = 0.42 + (MovementUtils.getPotionEffectLevel(player, PotionEffectType.JUMP) * 0.2f); //Accounting for jumps like Baldr

                double movementSpeed;

                if (playerData.isLastOnGroundPacket()) {

                    if (PacketEvents.getAPI().getServerUtils().getVersion().isHigherThan(ServerVersion.v_1_12_2)) {
                        movementSpeed = playerData.isSprinting() ? (0.0699999988079071D + 0.026F + 0.005999999865889549D) * 1.3 : 0.1;
                    } else {
                        movementSpeed = playerData.isSprinting() ? (0.0699999988079071D + 0.030000001192092896) * 1.3 : 0.1;
                    }

                    movementSpeed *= 0.16277136 / (friction * friction * friction);

                    if (offsetY > 0.0000001 && offsetY < jumpHeight && playerData.isSprinting()) {
                        movementSpeed += 0.2; //Jump shit
                    }

                    if (playerData.halfTicks > 0) {
                        movementSpeed += 0.2;
                    }

                    if (playerData.iceTicks > 0) {
                        movementSpeed += 0.11;
                    }

                } else {

                    movementSpeed = 0.026; // Base speed when sprinting = 0.26 else 0.2

                    friction = 0.91;

                    if (PacketEvents.getAPI().getServerUtils().getVersion().isHigherThan(ServerVersion.v_1_12_2)) {
                        if (playerData.getMovementsSinceUnderBlock() < 4) {
                            movementSpeed += 0.0435;
                        }
                    } else {
                        if (playerData.getMovementsSinceUnderBlock() < 4) {
                            movementSpeed = movementSpeed * 2;
                        }
                    }

                    if (playerData.halfTicks > 0) {
                        movementSpeed += 0.2;
                    }

                    if (offsetY > 0.4199 && playerData.isSprinting()) {
                        movementSpeed += 0.4199; //Fix for 1 false
                    }
                }

                //Bukkit.broadcastMessage("f: " + friction + " d: " + (friction - blockFriction));

                movementSpeed += (speedLevel * 0.2) * movementSpeed; //EntityLiving.class

                movementSpeed += (soulSpeedEnchant * 0.35F) * movementSpeed; //EntityLiving.class

                movementSpeed += playerData.getVelocityHorizontal(); //Velocity

                movementSpeed += (player.getWalkSpeed() - 0.2) * 12;

                if (playerData.isInLiquid()) {
                    movementSpeed += 0.2;
                    movementSpeed *= 8;
                }


                double diff = deltaH - lastDeltaH;
                double speedup = diff - movementSpeed;

                if (speedup > 0.0012 && deltaH > 0.25 && (playerData.getTeleportLocation() == null && System.currentTimeMillis() - playerData.getLastTeleport() > 2250L)) {

                    buffer = Math.min(500, buffer + 10 + speedup); //We do this to prevent integer overflow.

                    handleVerbose(player, "Speed C", "§b* §fNot following mc speed laws\n§b* §fspeed=§b" + speedup + "\n§b* §fvl=§b" + buffer + " oY: " + offsetY + " g: " + player.isOnGround() + " gt: " + playerData.getGroundTicks());

                    if (buffer > 25) {
                        this.handleFlag(player, "Speed C", "§b* §fNot following mc speed laws\n§b* §fspeed=§b" + speedup + "\n§b* §fvl=§b" + buffer, getBanVL("SpeedC"), 60000L);
                        buffer -= 10;
                    }
                } else {
                    buffer = Math.max(buffer - 0.5, -5);
                }

                lastOffsetH = deltaH * friction;
                lastDeltaH = deltaH * friction;
                friction = UtilPlayer.getBlockFriction(player) * 0.91;
            }
        }
    }
}

