package me.liwk.karhu.playerhandler;

import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.utils.server.ServerVersion;
import me.liwk.karhu.Karhu;
import org.bukkit.entity.Player;

public class Handler1_9 {

    public static boolean isGliding(Player player) {
        if(PacketEvents.getAPI().getServerUtils().getVersion().isLowerThan(ServerVersion.v_1_9)) {
            return false;
        }

        if(PacketEvents.getAPI().getServerUtils().getVersion().equals(ServerVersion.ERROR)) {
            return false;
        }

        if(player.isGliding()) Karhu.getInstance().getPlayerDataManager().getPlayerData(player).setLastGlide(System.currentTimeMillis());
        if(player.isGliding()) Karhu.getInstance().getPlayerDataManager().getPlayerData(player).setGliding(player.isGliding());
        return player.isGliding();
    }
}
