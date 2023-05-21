package me.liwk.karhu.check.impl.reach;

import io.github.retrooper.packetevents.event.PacketEvent;
import io.github.retrooper.packetevents.utils.player.ClientVersion;
import me.liwk.karhu.check.api.Category;
import me.liwk.karhu.check.api.Check;
import me.liwk.karhu.data.PlayerData;
import me.liwk.karhu.entity.EntityLocationHandler;
import me.liwk.karhu.event.FlyingEvent;
import me.liwk.karhu.util.AxisAlignedBB;
import me.liwk.karhu.util.MathUtil;
import me.liwk.karhu.util.MovingObjectPosition;
import me.liwk.karhu.util.Vec3;
import me.liwk.karhu.util.pair.Pair;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class ReachB extends Check {

    private double buffer, bufferBox;
    private Vec3 eyeLocation;
    private Vec3 look, lookMouseDelayFix;

    public ReachB(final PlayerData playerData) {
        super(Category.COMBAT, playerData);
    }

    @Override
    public void handle(PacketEvent event, Player player) {
        if (event instanceof FlyingEvent) {

            if (!player.getGameMode().equals(GameMode.CREATIVE)
                    && playerData.getLastTarget() != null
                    && !playerData.isLagging(System.currentTimeMillis(), 200L)
                    && playerData.getPastLocs().size() >= 10
                    && playerData.getLastAttackTick() <= 1
                    && playerData.getTotalTicks() - playerData.getLastPacketDrop() > 5
                    && System.currentTimeMillis() - playerData.getLastDelayedPacket() > 160L
                    && playerData.lastServerPositionTick > 100 + Math.min(MathUtil.getPingInTicks(playerData.getTransPing()), 5)) {


                float sneakAmount1_8 = playerData.isSneaking() ? 1.54F : 1.62F;
                float sneakAmount1_13 = playerData.isSneaking() ? 1.27F : 1.62F;

                if (playerData.getLocation() != null) {
                    eyeLocation = MathUtil.getPositionEyes(playerData.attackerX, playerData.attackerY, playerData.attackerZ, playerData.getClientVersion().isLowerThan(ClientVersion.v_1_13) ? sneakAmount1_8 : sneakAmount1_13);
                }

                if (((FlyingEvent) event).hasLooked()) {
                    lookMouseDelayFix = MathUtil.getVectorForRotation(playerData.attackerPitch, ((FlyingEvent) event).getYaw());
                    look = MathUtil.getVectorForRotation(playerData.attackerPitch, playerData.attackerYaw);
                } else {
                    lookMouseDelayFix = MathUtil.getVectorForRotation(playerData.attackerPitch, playerData.attackerYaw);
                    look = lookMouseDelayFix;
                }

                Vec3 vec3 = eyeLocation;
                Vec3 vec31 = look;
                Vec3 vec311 = lookMouseDelayFix;

                Vec3 vec32 = vec3.addVector(vec31.xCoord * 6.0D, vec31.yCoord * 6.0D, vec31.zCoord * 6.0D);
                Vec3 vec322 = vec3.addVector(vec311.xCoord * 6.0D, vec311.yCoord * 6.0D, vec311.zCoord * 6.0D);

                AxisAlignedBB axisalignedbb = new AxisAlignedBB(0, 0, 0, 0, 0, 0);
                boolean a = false;

                int nowTicks = playerData.getTotalTicks();
                int pingTicks = MathUtil.getPingInTicks(playerData.getTransPing()) + 3;

                double distance = -1;
                int collided = 0, missed = 0;

                for (Pair<AxisAlignedBB, Integer> pair : playerData.getPastLocs()) {
                    if (Math.abs(nowTicks - pair.getY() - pingTicks) < 2) {
                        if (!a) {
                            axisalignedbb = pair.getX();
                            a = true;
                        } else {
                            axisalignedbb.minX = Math.min(axisalignedbb.minX, pair.getX().minX);
                            axisalignedbb.maxX = Math.max(axisalignedbb.maxX, pair.getX().maxX);
                            axisalignedbb.minY = Math.min(axisalignedbb.minY, pair.getX().minY);
                            axisalignedbb.maxY = Math.max(axisalignedbb.maxY, pair.getX().maxY);
                            axisalignedbb.minZ = Math.min(axisalignedbb.minZ, pair.getX().minZ);
                            axisalignedbb.maxZ = Math.max(axisalignedbb.maxZ, pair.getX().maxZ);
                        }

                        MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);
                        MovingObjectPosition movingobjectposition2 = axisalignedbb.calculateIntercept(vec3, vec322);

                        if (movingobjectposition != null && movingobjectposition2 != null && !axisalignedbb.isVecInside(vec3)) {
                            double d3 = vec3.distanceTo(movingobjectposition.hitVec);
                            double d33 = vec3.distanceTo(movingobjectposition2.hitVec);
                            distance = Math.min(d3, d33);
                            if (distance > 3.03D) {
                                ++collided;
                            }
                        } else if (movingobjectposition == null && movingobjectposition2 == null && !axisalignedbb.isVecInside(vec3)) {
                            ++missed;
                        }
                    }
                }

                double maxDist = 3.0D;
                //Bukkit.broadcastMessage("P: " + player.getName() + "R: " + distance + " C: " + collided);
                if (distance > 3.03D && collided > 2) {

                    handleVerbose(player, "Reach B", "§b* §fRange=§b" + distance + "\n§b* §fCollided=§b" + collided + "\n§b* §fPredict=§b" + maxDist + "\n§b* §fvl=§b" + buffer);

                    if ((buffer += 1.5) > 3.5) {

                        handleFlag(player, "Reach B", "§b* §fRange=§b" + distance + "\n§b* §fCollided=§b" + collided + "\n§b* §fPredict=§b" + maxDist + "\n§b* §fvl=§b" + buffer, getBanVL("ReachB"), 60000L);
                    }
                } else {
                    buffer = Math.max(buffer - 0.5, 0);
                }
            }
            EntityLocationHandler.updateFlyingLocations(playerData, (FlyingEvent) event);
        }
    }
}
