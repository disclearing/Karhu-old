package me.liwk.karhu.check.impl.velocity;

import io.github.retrooper.packetevents.event.PacketEvent;
import me.liwk.karhu.check.api.Category;
import me.liwk.karhu.check.api.Check;
import me.liwk.karhu.data.PlayerData;
import me.liwk.karhu.event.FlyingEvent;
import me.liwk.karhu.util.MathUtil;
import me.liwk.karhu.util.UtilPlayer;
import org.bukkit.entity.Player;

public class VelocityA extends Check {

    private int buffer;

    public VelocityA(final PlayerData playerData) {
        super(Category.COMBAT, playerData);
    }

    @Override
    public void handle(PacketEvent event, Player player) {
        if (event instanceof FlyingEvent) {

            if (playerData.getVelocityY() > 0.2 && MathUtil.onGround(playerData.getLastLastLocation().getY())) {
                int velTicks = playerData.getTotalTicks() - playerData.getVelocityTicks();
                int maxPreVL = MathUtil.getPingInTicks(playerData.getTransPing()) + 5;

                final double p = (playerData.getDeltaY() / playerData.getVelocityY()) * 100.0D + 0.01;

                if (velTicks <= MathUtil.getPingInTicks(playerData.getTransPing()) + 2 && playerData.getLastServerPositionTick() > 55) {
                    if (playerData.getLiquidTicks() < 1 && !UtilPlayer.blockNearHead(player) && !UtilPlayer.isInWeb(player) && !UtilPlayer.isClimbableBlock(player) && player.getVehicle() == null) {
                        if (p < 99 || p > 105.0D) {
                            if (++buffer >= maxPreVL) {
                                handleFlag(player, "Velocity A", "§b* §fVertical velocity§b\n§b* §fapprox ptc=§b" + (int) (p + 1) + "§f%", getBanVL("VelocityA"), 100000L);
                            }
                        } else {
                            buffer = 0;
                        }
                    } else {
                        buffer = 0;
                    }
                }
            }
        }
    }
    private String dogshit(boolean value) {
        return value ? "%%__TIMESTAMP__%%" : "%%__USER__%%";
    }
}
