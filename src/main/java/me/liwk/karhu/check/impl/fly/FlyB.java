package me.liwk.karhu.check.impl.fly;

import io.github.retrooper.packetevents.event.PacketEvent;
import me.liwk.karhu.check.api.Category;
import me.liwk.karhu.check.api.Check;
import me.liwk.karhu.data.PlayerData;
import me.liwk.karhu.event.FlyingEvent;
import me.liwk.karhu.playerhandler.Handler1_8;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class FlyB extends Check {

    private double buffer;
    private double lastDeltaY;

    public FlyB(final PlayerData playerData) {
        super(Category.COMBAT, playerData);
    }

    @Override
    public void handle(PacketEvent event, Player player) {
        if (event instanceof FlyingEvent) {

            Location to = ((FlyingEvent) event).toLocation();
            Location from = playerData.getLastLocation();

            double deltaY = to.getY() - from.getY();

            double estimation = (lastDeltaY - 0.08) * 0.9800000190734863D;

            double diff = Math.abs(deltaY - estimation);

            if(Math.abs(deltaY + 0.098) <= 1E-4) {
                return;
            }

            boolean valid = playerData.getVelocityV() <= 0
                    && playerData.getVelocityH() <= 0 && !player.getAllowFlight() && !Handler1_8.isSpectating(player);

            if (playerData.getAirTicks() > 6 && !playerData.isTeleporting() && diff >= 0.005D && Math.abs(estimation) >= 0.005D && !player.isInsideVehicle()) {
                if(++buffer > 3) {
                    handleFlag(player, "Fly B", "§b* §fInvalid y-axis movement" + "\n§b* §fpred=§b" + estimation + "\n§b* §fticks=§b" + playerData.getAirTicks(), getBanVL("FlyB"), 60000L);
                }
            } else {
                buffer = Math.max(buffer - 0.5, 0);
            }


            this.lastDeltaY = deltaY;
        }
    }
}