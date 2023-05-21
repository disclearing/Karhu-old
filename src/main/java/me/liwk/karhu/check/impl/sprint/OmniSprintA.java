package me.liwk.karhu.check.impl.sprint;

import io.github.retrooper.packetevents.event.PacketEvent;
import me.liwk.karhu.check.api.Category;
import me.liwk.karhu.check.api.Check;
import me.liwk.karhu.data.PlayerData;
import me.liwk.karhu.event.FlyingEvent;
import me.liwk.karhu.playerhandler.Handler1_8;
import me.liwk.karhu.util.MathUtil;
import me.liwk.karhu.util.MovementUtils;
import me.liwk.karhu.util.UtilPlayer;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class OmniSprintA extends Check {
    
    private double buffer;
    
    public OmniSprintA(final PlayerData playerData) {
        super(Category.MOVEMENT, playerData);
    }

    @Override
    public void handle(PacketEvent event, Player player) {
        if (event instanceof FlyingEvent) {

            if(((FlyingEvent) event).hasMoved()) {
                Location to = ((FlyingEvent) event).toLocation();
                Location from = playerData.getLastLocation();
                if(!playerData.isSprinting()
                        || playerData.isInLiquid()
                        || playerData.getVelocityH() > 0
                        || playerData.getLastServerPositionTick() < 100 + MathUtil.getPingInTicks(playerData.getTransPing())
                        || !MathUtil.onGround(to.getY())
                        || playerData.droppedPackets
                        || player.isFlying()
                        || playerData.getTotalTicks() - playerData.getLastFlyTick() < 40
                        || player.getGameMode().equals(GameMode.CREATIVE)
                        || Handler1_8.isSpectating(player)
                        || playerData.isLagging(System.currentTimeMillis(), 100L)
                        || !UtilPlayer.isOnGroundBB(player)
                        || playerData.getDeltaXZ() < 0.11)  {
                    buffer = Math.max(buffer - 1, 0);
                    return;
                }

                double speedDelta = MathUtil.getVectorSpeed(from, to).distanceSquared(MathUtil.getDirection(player));
                double maxDelta = 0.221;
                float speedLevel = MovementUtils.getPotionEffectLevel(player, PotionEffectType.SPEED);

                maxDelta += playerData.getVelocityH() > 0 ? playerData.getVelocityHorizontal() : 0.0;
                maxDelta += playerData.getGroundTicks() < 5 ? speedLevel * 0.07f : speedLevel * 0.0573f;
                maxDelta *= playerData.iceTicks > 0 ? 1.5f : 1.0;
                maxDelta *= UtilPlayer.isOnStair(player) || UtilPlayer.isOnSlab(player) ? 1.5f : 1.0;
                maxDelta += (player.getWalkSpeed() - 0.2) * 5;

                if(speedDelta > maxDelta) {
                    if((buffer += 0.75) > 8) {
                        handleFlag(player, "OmniSprint A", "§b* §fspeedDelta=§b" + speedDelta + "\n§b* §fmaxDelta=§b" + maxDelta, getBanVL("OmniSprintA"), 30000L);
                    }
                } else {
                    buffer = Math.max(buffer - 0.5, 0);
                }
            }
        }
    }
    private String dogshit(boolean value) {
        return value ? "%%__TIMESTAMP__%%" : "%%__USER__%%";
    }
}
