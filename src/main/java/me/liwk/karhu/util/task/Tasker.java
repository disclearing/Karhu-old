package me.liwk.karhu.util.task;

import me.liwk.karhu.Karhu;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public class Tasker {

    public static void run(Runnable runnable) {
        Karhu.getInstance().getServer().getScheduler().runTask(Karhu.getInstance(), runnable);
    }

    private static BukkitTask taskTimer(Runnable runnable, Plugin plugin) {
        return Bukkit.getScheduler().runTaskTimer(plugin, runnable, 0L, 1L);
    }

    public static BukkitTask taskTimer(Runnable runnable) {
        return taskTimer(runnable, Karhu.getInstance());
    }

    private static BukkitTask taskAsync(Runnable runnable, Plugin plugin) {
        return Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable);
    }

    public static BukkitTask taskAsync(Runnable runnable) {
        return taskAsync(runnable, Karhu.getInstance());
    }

    private static BukkitTask taskTimerAsync(Runnable runnable, Plugin plugin) {
        return Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, runnable, 0L, 1L);
    }

    public static BukkitTask taskTimerAsync(Runnable runnable) {
        return taskTimerAsync(runnable, Karhu.getInstance());
    }

    public static void runSyncRepeating(Runnable runnable) {
        Karhu.getInstance().getServer().getScheduler().scheduleSyncRepeatingTask(Karhu.getInstance(), runnable, 1L, 1L);
    }

}
