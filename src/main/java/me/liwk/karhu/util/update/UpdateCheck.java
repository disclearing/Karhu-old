package me.liwk.karhu.util.update;

import lombok.Getter;
import me.liwk.karhu.Karhu;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class UpdateCheck {

    @Getter
    private static String newVersion;

    public static void checkVersion() {
        String licenseKey = Karhu.getInstance().getFileManager().getSettings().getString("License");
        if(licenseKey.length() != 19) {
            Bukkit.getScheduler().cancelTasks(Karhu.getInstance());
            Bukkit.getPluginManager().disablePlugin(Karhu.getInstance());
        }

        try {
            newVersion = readLines();

            if (!Karhu.getInstance().getBuild().equals(newVersion)) {
                inform();
            }
        } catch (Exception ignored) {
        }
    }

    public static void inform() {
        Bukkit.getLogger().info("[Karhu] You are running an old version! Latest: " + newVersion);
    }

    public static void informStaff(Player player) {
        player.sendMessage("");
        player.sendMessage("§b§lKarhu §7┃ §f* §3§lUPDATE AVAILABLE §f* \n§fYour version: §c" + Karhu.getInstance().getBuild() + "\n§fLatest version: §b" + newVersion);
        player.sendMessage("");
    }

    public static String readLines() throws Exception {
        URLConnection connection = new URL("https://www.karhu.cc/paska.html").openConnection();
        connection.addRequestProperty("User-Agent", "Mozilla/4.0");

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        return reader.readLine();
    }
}
