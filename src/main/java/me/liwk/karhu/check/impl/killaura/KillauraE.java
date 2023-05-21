package me.liwk.karhu.check.impl.killaura;

import io.github.retrooper.packetevents.event.PacketEvent;
import me.liwk.karhu.check.api.Category;
import me.liwk.karhu.check.api.Check;
import me.liwk.karhu.data.PlayerData;
import me.liwk.karhu.event.FlyingEvent;
import me.liwk.karhu.util.MathUtil;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class KillauraE extends Check {

    private int buffer;

    public KillauraE(final PlayerData playerData) {
        super(Category.COMBAT, playerData);
    }

    @Override
    public void handle(PacketEvent event, Player player) {
        if (event instanceof FlyingEvent) {
            if (playerData.getLastTarget() != null  && player.getVehicle() == null && playerData.getLastAttackTick() <= 1 && playerData.lastServerPositionTick > 60) {

                Player target = playerData.getLastTarget();

                final double range = player.getEyeLocation().clone().toVector().setY(0.0D).distance(target.getEyeLocation().clone().toVector().setY(0.0D));
                final Vector vec = target.getLocation().clone().toVector().setY(0.0).subtract(player.getEyeLocation().clone().toVector().setY(0.0));
                final float angle = player.getEyeLocation().getDirection().angle(vec);

                final double direction = MathUtil.getDirection(player.getEyeLocation(), target.getEyeLocation());
                final double dist = MathUtil.getDistanceBetweenAngles360(player.getEyeLocation().getYaw(), direction);

                double maxAngle = 35.0D;

                maxAngle += MathUtil.pingFormula(playerData.getTransPing()) + 5;

                if ((dist > maxAngle || angle > 2) && range > 1.5 && System.currentTimeMillis() - playerData.lastFlag > 50L) {
                    playerData.lastFlag = System.currentTimeMillis();
                    if (++buffer > 5) {
                        buffer = 0;
                        handleFlag(player, "Killaura E", "* Hitbox aura\n* dist=" + dist + "\n* expected=" + maxAngle, getBanVL("KillauraE"), 60000L);
                    }
                } else if(dist < maxAngle && angle < 2) {
                    buffer = Math.max(buffer - 1, 0);
                }
            }
        }
    }
}
