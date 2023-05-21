package me.liwk.karhu.check.impl.killaura;

import io.github.retrooper.packetevents.event.PacketEvent;
import me.liwk.karhu.check.api.Category;
import me.liwk.karhu.check.api.Check;
import me.liwk.karhu.data.PlayerData;
import me.liwk.karhu.event.AttackEvent;
import me.liwk.karhu.event.DigEvent;
import me.liwk.karhu.event.FlyingEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class KillauraD extends Check {

    private boolean sentDig;
    private int buffer;

    public KillauraD(final PlayerData playerData) {
        super(Category.COMBAT, playerData);
    }

    @Override
    public void handle(PacketEvent event, Player player) {
        if (event instanceof AttackEvent) {
            if (sentDig) {
                if (player.getItemInHand().getType() != Material.BOW || player.getItemInHand().getType() != Material.FISHING_ROD) {
                    if (++buffer > 6) {
                        handleFlag(player, "Killaura D", "§b* §fDigging while attacking", getBanVL("KillauraD"), 40000L);
                    }
                } else {
                    buffer = Math.max(buffer - 1, 0);
                }
            } else {
                buffer = Math.max(buffer - 1, 0);
            }
        } else if (event instanceof FlyingEvent) {
            sentDig = false;
        } else if (event instanceof DigEvent) {
            sentDig = true;
        }
    }
}
