package me.liwk.karhu.check.impl.killaura;

import io.github.retrooper.packetevents.event.PacketEvent;
import me.liwk.karhu.check.api.Category;
import me.liwk.karhu.check.api.Check;
import me.liwk.karhu.data.PlayerData;
import me.liwk.karhu.event.AttackEvent;
import me.liwk.karhu.event.BlockPlaceEvent;
import me.liwk.karhu.event.FlyingEvent;
import me.liwk.karhu.event.InteractEvent;
import org.bukkit.entity.Player;

public class KillauraB extends Check {

    private boolean sentInteract;
    private boolean sentAttack;

    public KillauraB(final PlayerData playerData) {
        super(Category.COMBAT, playerData);
    }

    @Override
    public void handle(PacketEvent event, Player player) {
        if (event instanceof FlyingEvent) {
            sentInteract = false;
            sentAttack = false;
        } else if(event instanceof AttackEvent) {
            sentAttack = true;
        } else if(event instanceof InteractEvent) {
            sentInteract = true;
        } else if(event instanceof BlockPlaceEvent) {
            if (sentAttack &! sentInteract) {
                handleFlag(player, "Killaura B", "§b* §fAutoblock", getBanVL("KillauraB"), 60000L);
            }
        }
    }
}
