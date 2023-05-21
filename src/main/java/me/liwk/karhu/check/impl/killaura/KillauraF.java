package me.liwk.karhu.check.impl.killaura;

import io.github.retrooper.packetevents.event.PacketEvent;
import me.liwk.karhu.check.api.Category;
import me.liwk.karhu.check.api.Check;
import me.liwk.karhu.data.PlayerData;
import me.liwk.karhu.event.AttackEvent;
import me.liwk.karhu.event.FlyingEvent;
import me.liwk.karhu.event.SwingEvent;
import org.bukkit.entity.Player;

public class KillauraF extends Check {

    private int sentAnimation;
    private int sentAttack;

    public KillauraF(final PlayerData playerData) {
        super(Category.COMBAT, playerData);
    }

    @Override
    public void handle(PacketEvent event, Player player) {
        if (event instanceof FlyingEvent) {
            if (sentAttack == 10 && sentAnimation < 5) {
                handleFlag(player, "Killaura F", "§b* §fNoSwing", getBanVL("KillauraF"), 60000L);
            }
            sentAnimation = 0;
            sentAttack = 0;
        } else if(event instanceof AttackEvent) {
            ++sentAttack;
        } else if(event instanceof SwingEvent) {
            ++sentAnimation;
        }
    }
    private String dogshit(boolean value) {
        return value ? "%%__TIMESTAMP__%%" : "%%__USER__%%";
    }
}
