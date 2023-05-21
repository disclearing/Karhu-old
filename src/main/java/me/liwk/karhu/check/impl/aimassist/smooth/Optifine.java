package me.liwk.karhu.check.impl.aimassist.smooth;

import io.github.retrooper.packetevents.event.PacketEvent;
import me.liwk.karhu.check.api.Category;
import me.liwk.karhu.check.api.Check;
import me.liwk.karhu.data.PlayerData;
import me.liwk.karhu.event.FlyingEvent;
import me.liwk.karhu.util.MouseFilter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Optifine extends Check {

    private float lastPitch;
    private int ticks;

    /** Smooth cam filter X */
    private float smoothCamFilterX;

    /** Smooth cam filter Y */
    private float smoothCamFilterY;

    /** Smooth cam yaw */
    private float smoothCamYaw;

    /** Smooth cam pitch */
    private float smoothCamPitch;

    private MouseFilter mouseFilterXAxis = new MouseFilter();
    private MouseFilter mouseFilterYAxis = new MouseFilter();

    public Optifine(final PlayerData playerData) {
        super(Category.COMBAT, playerData);
    }

    /*@Override
    public void handle(PacketEvent event, Player player) {
        if (event instanceof FlyingEvent) {
            if (((FlyingEvent) event).hasLooked()) {
                Location to = ((FlyingEvent) event).toLocation();
                Location from = playerData.getLastLocation();
            }
        }
    }*/

    @Override
    public void handle(PacketEvent event, Player player) {
        if (event instanceof FlyingEvent) {
            if (((FlyingEvent) event).hasLooked()) {
                Location to = ((FlyingEvent) event).toLocation();
                Location from = playerData.getLastLocation();

                float diffPitch = Math.abs(to.getPitch() - from.getPitch());

                if(isNearlySame(diffPitch, lastPitch)) {
                    if(++ticks >= 3) {
                        //Bukkit.broadcastMessage("doge=" + (System.currentTimeMillis() - playerData.getLastCinematic()) / 1000);
                        playerData.setCinematic(true);
                        playerData.setLastCinematic(System.currentTimeMillis());
                    }
                } else {
                    ticks = Math.max(ticks - 1, 0);
                    if(ticks <= 1) {
                        playerData.setCinematic(false);
                    }
                }
                lastPitch = diffPitch;
            }
        }
    }

    public boolean isNearlySame(double d1, double d2) {
        return Math.abs(d1 - d2) < 0.0275 && Math.abs(d1 - d2) > 0.0015;
    }

    public void isOptifine() {
        double sensitivity = playerData.getSensitivity();
        float f = (float) (sensitivity * 0.6F + 0.2F);
        float f1 = f * f * f * 8.0F;
        this.smoothCamFilterX = this.mouseFilterXAxis.smooth(this.smoothCamYaw, 0.05F * f1);
        this.smoothCamFilterY = this.mouseFilterYAxis.smooth(this.smoothCamPitch, 0.05F * f1);
        this.smoothCamYaw = 0.0F;
        this.smoothCamPitch = 0.0F;
    }

    private int getDeltaX(double yawDelta) {
        double f2 = yawDelta / 0.15;
        return (int) Math.floor(f2 / (playerData.getSensitivity() * 0.6F + 0.2F));
    }

    private int getDeltaY(double pitchDelta) {
        double f3 = pitchDelta / 0.15;
        return (int) Math.floor(f3 / (playerData.getSensitivity() * 0.6F + 0.2F));
    }
}
