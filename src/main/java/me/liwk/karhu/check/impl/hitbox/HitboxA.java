package me.liwk.karhu.check.impl.hitbox;

import io.github.retrooper.packetevents.event.PacketEvent;
import io.github.retrooper.packetevents.utils.player.ClientVersion;
import me.liwk.karhu.check.api.Category;
import me.liwk.karhu.check.api.Check;
import me.liwk.karhu.data.PlayerData;
import me.liwk.karhu.entity.EntityLocationHandler;
import me.liwk.karhu.event.FlyingEvent;
import me.liwk.karhu.util.AxisAlignedBB;
import me.liwk.karhu.util.MathUtil;
import me.liwk.karhu.util.Vec3;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class HitboxA extends Check {

    private double buffer;
    private Vec3 eyeLocation;
    private Vec3 look, lookMouseDelayFix;

    public HitboxA(final PlayerData playerData) {
        super(Category.COMBAT, playerData);
    }

    @Override
    public void handle(PacketEvent event, Player player) {
        if (event instanceof FlyingEvent) {
            if (!player.getGameMode().equals(GameMode.CREATIVE)
                    && playerData.getLastTarget() != null
                    && !playerData.isLagging(System.currentTimeMillis(), 200L)
                    && playerData.getLastAttackTick() <= 1
                    && playerData.getPastLocsHitBox().size() >= 10
                    && playerData.getTotalTicks() - playerData.getLastPacketDrop() > 5
                    && System.currentTimeMillis() - playerData.getLastDelayedPacket() > 160L
                    && playerData.lastServerPositionTick > 100 + Math.min(MathUtil.getPingInTicks(playerData.getTransPing()), 5)) {

                float sneakAmount1_8 = playerData.isSneaking() ? 1.54F : 1.62F;
                float sneakAmount1_13 = playerData.isSneaking() ? 1.27F : 1.62F;

                if (playerData.getLocation() != null) {
                    eyeLocation = MathUtil.getPositionEyes(playerData.attackerX2, playerData.attackerY2, playerData.attackerZ2, playerData.getClientVersion().isLowerThan(ClientVersion.v_1_13) ? sneakAmount1_8 : sneakAmount1_13);
                }

                if (((FlyingEvent) event).hasLooked()) {
                    lookMouseDelayFix = MathUtil.getVectorForRotation(playerData.attackerPitch2, ((FlyingEvent) event).getYaw());
                    look = MathUtil.getVectorForRotation(playerData.attackerPitch2, playerData.attackerYaw2);
                } else {
                    lookMouseDelayFix = MathUtil.getVectorForRotation(playerData.attackerPitch2, playerData.attackerYaw2);
                    look = lookMouseDelayFix;
                }

                Vec3 vec3 = eyeLocation;
                Vec3 vec31 = look;
                Vec3 vec311 = lookMouseDelayFix;

                Vec3 vec32 = vec3.addVector(vec31.xCoord * 6.0D, vec31.yCoord * 6.0D, vec31.zCoord * 6.0D);
                Vec3 vec322 = vec3.addVector(vec311.xCoord * 6.0D, vec311.yCoord * 6.0D, vec311.zCoord * 6.0D);

                AxisAlignedBB axisalignedbb = new AxisAlignedBB(0, 0, 0, 0, 0, 0);
                boolean a = false;
                int missed = 0;

                final int nowTicks = playerData.getTotalTicks();
                final int pingTicks = MathUtil.getPingInTicks(playerData.getTransPing()) + 3;

                /*for(Pair<AxisAlignedBB, Integer> pair : playerData.getPastLocsHitBox()) {
                    if(Math.abs(nowTicks - pair.getY() - pingTicks) < 2) {
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

                        if (movingobjectposition == null && movingobjectposition2 == null && !axisalignedbb.isVecInside(vec3)) {
                            ++missed;
                        }
                    }
                }*/


                /*if (missed >= 3 && playerData.getTotalTicks() - playerData.getLastPacketDrop() > 10) {
                    if((buffer += 0.75) > 8.5) {
                        //handleFlag(player, "Hitbox A", "§b* §fHit outside of players box\n§b* §fmisses=§b" + missed, getBanVL("HitboxA"), 60000L);
                    }
                } else buffer = Math.max(buffer - 4.5, 0.0);*/
            }
            EntityLocationHandler.updateFlyingLocations2(playerData, (FlyingEvent) event);
        }
    }
}
