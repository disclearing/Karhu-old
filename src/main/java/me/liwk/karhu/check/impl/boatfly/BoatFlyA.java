package me.liwk.karhu.check.impl.boatfly;

import io.github.retrooper.packetevents.event.PacketEvent;
import me.liwk.karhu.check.api.Category;
import me.liwk.karhu.check.api.Check;
import me.liwk.karhu.data.PlayerData;
import me.liwk.karhu.event.FlyingEvent;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;

public class BoatFlyA extends Check {

    private double buffer;

    public BoatFlyA(final PlayerData playerData) {
        super(Category.MOVEMENT, playerData);
    }

    @Override
    public void handle(PacketEvent event, Player player) {
        if (event instanceof FlyingEvent) {

            if (((FlyingEvent) event).hasMoved()) {
                if (player.getVehicle() != null && !(player.getVehicle() instanceof Horse)) {
                    if((playerData.getDeltaY() - playerData.getLastDeltaY()) > 0.11 && playerData.getSlimeTicks() < 1 && playerData.getTotalTicks() - playerData.getLastVehicleTicks() > 5) {
                        if(++buffer > 4) {
                            handleFlag(player, "BoatFly A", "§b* §fdeltaY=§b" + (playerData.getDeltaY() - playerData.getLastDeltaY()), getBanVL("BoatFlyA"), 60000L);
                        }
                    } else {
                        buffer = Math.max(buffer - 0.5, 0);
                    }
                }
            }
        }
    }
}
