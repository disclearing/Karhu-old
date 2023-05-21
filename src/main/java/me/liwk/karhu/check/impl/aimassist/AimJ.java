package me.liwk.karhu.check.impl.aimassist;

import io.github.retrooper.packetevents.event.PacketEvent;
import me.liwk.karhu.check.api.Category;
import me.liwk.karhu.check.api.Check;
import me.liwk.karhu.data.PlayerData;
import me.liwk.karhu.event.FlyingEvent;
import me.liwk.karhu.util.MathUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class AimJ extends Check {

    private final double multiplier = Math.pow(2, 24);
    private float previous;
    private double vl, streak;

    public AimJ(final PlayerData playerData) {
        super(Category.COMBAT, playerData);
    }

    @Override
    public void handle(PacketEvent event, Player player) {
        if (event instanceof FlyingEvent) {
            if (((FlyingEvent) event).hasLooked()) {
                Location to = ((FlyingEvent) event).toLocation();
                Location from = playerData.getLastLocation();

                if((playerData.isCinematic() || System.currentTimeMillis() - playerData.getLastCinematic() < 2500L)) return;

                if ((System.currentTimeMillis() - playerData.getLastAttackPacket() >= 2000)) {
                    this.setVl(0);
                    vl = 0;
                    streak = 0;
                    return;
                }

                if (playerData.lastServerPositionTick < 55) {
                    vl = 0;
                    return;
                }

                //if(PacketEvents.getAPI().getPlayerUtils().getClientVersion(player).isHigherThan(ClientVersion.v_1_8)) return; DOESN'T WORK ATM

                float pitchChange = MathUtil.getDistanceBetweenAngles(to.getPitch(), from.getPitch());

                long a = (long) (pitchChange * multiplier);
                long b = (long) (previous * multiplier);

                long gcd = MathUtil.gcd(a, b);

                float magicVal = pitchChange * 100 / previous;

                if (magicVal > 60) {
                    vl = Math.max(0, vl - 1);
                    streak = Math.max(0, streak - 0.125);
                }

                if (pitchChange > 0.5 && pitchChange <= 20 && gcd < 131072) {
                    if (++vl > 1) {
                        ++streak;
                    }
                    if (streak > 10) {
                        streak -= 2;
                        handleFlag(player, "AimAssist J", "§b* §fChecks for GCD" + "\n§b* §fmagicv=§b" + magicVal + "\n§b* §fgcd=§b" + gcd, getBanVL("AimJ"), 300000L);
                    }
                    if(streak > 3) {
                        handleVerbose(player, "AimAssist J", "§b* §fChecks for GCD" + "\n§b* §fmagicv=§b" + magicVal + "\n§b* §fgcd=§b" + gcd);
                    }
                } else {
                    vl = Math.max(0, vl - 1);
                }
                this.previous = pitchChange;
            }
        }
    }
}
