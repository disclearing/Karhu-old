package me.liwk.karhu.util.check;

import io.github.retrooper.packetevents.utils.server.ServerVersion;
import me.liwk.karhu.Karhu;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class EnabledUtil {

    public static List<Player> playersToBeBanned = new ArrayList<>();

    public static boolean checkIfIsEnabled(String check) {

        if(check.equals("Optifine") || check.equals("Mouse")) return true;

        return Karhu.getInstance().getFileManager().getSettings().getBoolean("checks." + check + ".enabled");
    }

    public static boolean checkIfIsAutoban(String check) {
        return Karhu.getInstance().getFileManager().getSettings().getBoolean("checks." + check.replaceAll("AimAssist", "Aim") + ".autoban");
    }

    public static ServerVersion checkDefault(String ver) {
        if(ver.contains("1_7")) {
            return ServerVersion.v_1_7_10;
        }
        return ServerVersion.v_1_8_8;
    }

    public static String truncateLicense(String lic) {
        return lic.substring(0, 9);
    }

}
