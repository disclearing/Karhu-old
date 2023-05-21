package me.liwk.karhu.playerhandler;

import io.github.retrooper.packetevents.utils.server.ServerVersion;
import me.liwk.karhu.Karhu;
import me.liwk.karhu.data.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class VersionBridgeHelper {

    public static ItemStack getStackInHand(PlayerData data){
        return getStackInHand(data.getDataPlayer());
    }

    public static ItemStack getStackInHand(Player data){
        return Karhu.SERVER_VERSION.isHigherThan(ServerVersion.v_1_8_8) ? data.getInventory().getItemInMainHand() : data.getItemInHand();
    }

}
