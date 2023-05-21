package me.liwk.karhu.check.impl.aimassist.smooth;

import io.github.retrooper.packetevents.event.PacketEvent;
import me.liwk.karhu.check.api.Category;
import me.liwk.karhu.check.api.Check;
import me.liwk.karhu.data.PlayerData;
import me.liwk.karhu.event.FlyingEvent;
import me.liwk.karhu.util.MathUtil;
import me.liwk.karhu.util.evictinglist.EvictingList;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class Mouse extends Check {

    public Mouse(final PlayerData playerData) {
        super(Category.COMBAT, playerData);
    }

    private static Map<Double, Double> sensitivityMap = new HashMap<>();

    static {
        for(double d = 50.9; d < 204.8; d += 0.7725) {
            sensitivityMap.put(d, sensitivityMap.size() * 0.005);
        }
    }

    private EvictingList<Double> samples = new EvictingList<>(50);
    private double lastDeltaPitch, lastMode, lastConstant, recordedConstant;

    @Override
    public void handle(PacketEvent event, Player player) {
        if (event instanceof FlyingEvent) {
            if (((FlyingEvent) event).hasLooked()) {
                Location to = ((FlyingEvent) event).toLocation();
                Location from = playerData.getLastLocation();

                double deltaPitch = Math.abs(to.getPitch() - from.getPitch());

                long expandedPitch = (long) Math.abs(deltaPitch * Math.pow(2, 24));
                long lastExpandedPitch = (long) Math.abs(lastDeltaPitch * Math.pow(2, 24));

                double pitchGcd = MathUtil.getGcd(expandedPitch, lastExpandedPitch) / Math.pow(2, 24);

                samples.add(pitchGcd);
                if (samples.size() == 50) {
                    double mode = MathUtil.getMode(samples);

                    long expandedMode = (long) (mode * Math.pow(2, 24));
                    long lastExpandedMode = (long) (lastMode * Math.pow(2, 24));

                    double modeGcd = MathUtil.getGcd(expandedMode, lastExpandedMode);
                    double constant = Math.round((Math.cbrt(modeGcd / .15 / 8) - .2 / .6) * 1000.0) / 1000.0;


                    if (Math.abs(constant - lastConstant) < 0.01) {
                        playerData.setVerifyingSensitivity(false);
                        recordedConstant = constant;
                    } else {
                        playerData.setVerifyingSensitivity(true);
                    }

                    double sensitivity = getSensitivity(recordedConstant);
                    if (sensitivity > -1) {
                        playerData.setSensitivity(sensitivity);
                    }

                    /*if(!playerData.isVerifyingSensitivity()) {
                        Bukkit.broadcastMessage(ChatColor.RED + "" + player.getName() + "'s Sensitivity = " + ChatColor.GREEN + Math.round(sensitivity * 200.0) + "%");
                    }*/

                    lastConstant = constant;
                    lastMode = mode;
                    samples.clear();
                }

                lastDeltaPitch = deltaPitch;
            }
        }
    }

    public double getSensitivity(double constant) {
        for(double val : sensitivityMap.keySet()) {
            if(Math.abs(val - constant) <= 0.4) {
                return sensitivityMap.get(val);
            }
        }
        return -1;
    }
}
