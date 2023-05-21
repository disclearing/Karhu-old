package me.liwk.karhu.playerhandler;

import io.github.retrooper.packetevents.PacketEvents;import io.github.retrooper.packetevents.utils.server.ServerVersion;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class Handler1_8 {

    public static boolean isGliding(Player player) {
        return false;
    }

    public static boolean isSpectating(Player player) {
        if(PacketEvents.getAPI().getServerUtils().getVersion().isLowerThan(ServerVersion.v_1_8)) {
            return false;
        }
        return player.getGameMode().equals(GameMode.SPECTATOR);
    }
}
