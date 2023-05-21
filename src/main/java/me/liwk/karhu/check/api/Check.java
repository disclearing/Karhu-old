package me.liwk.karhu.check.api;

import io.github.retrooper.packetevents.event.PacketEvent;
import lombok.Getter;
import lombok.Setter;
import me.liwk.karhu.Karhu;
import me.liwk.karhu.api.events.BanEvent;
import me.liwk.karhu.data.PlayerData;
import me.liwk.karhu.api.events.AlertEvent;
import me.liwk.karhu.util.UtilPlayer;
import me.liwk.karhu.util.check.EnabledUtil;
import me.liwk.karhu.util.file.LogUtil;
import me.liwk.karhu.util.task.Tasker;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public abstract class Check {
    @Getter
    private String rawName;
    @Getter
    @Setter
    private Player player;
    @Getter
    private final String name;
    @Getter
    private final char type;
    @Getter
    private final Category category;
    protected final PlayerData playerData;

    public boolean enabled, punishable, dev;

    public double getVl() {
        return playerData.getCheckVl(this);
    }

    public void setVl(double vl) {
        playerData.setCheckVl(vl, this);
    }

    public Check(final String name, final char type, final Category category, PlayerData playerData) {
        this.name = name;
        this.type = type;
        this.category = category;
        this.playerData = playerData;
    }

    public Check(final Category category, final PlayerData playerData) {
        this.playerData = playerData;
        Class<?> cls = getClass();
        this.name = cls.getSimpleName();
        this.type = cls.getSimpleName().charAt(cls.getSimpleName().length() - 1);
        this.category = category;
    }

    public int getBanVL(String name) {
        String f = name.replace(" ", "");

        if(!Karhu.getInstance().getFileManager().getSettings().isSet("checks." + f + ".ban-vl")) {
            Karhu.getInstance().getFileManager().getSettings().set("checks." + f + ".ban-vl", 20);
        }

        return Karhu.getInstance().getFileManager().getSettings().getInt("checks." + f + ".ban-vl");
    }

    protected void handleFlag(Player player, String information, String data, int maxvl, long time) {
        Karhu.getInstance().getExecutorService().execute(() -> {
            if (player == null) return;
            if (Karhu.getInstance().isServerLagging()) return;
            if (playerData.isBanned()) return;
            if (playerData.isExempt()) return;
            playerData.addViolation(this);
            int violations = playerData.getViolations(this, time);
            playerData.setCheckVl(violations, this);
            this.rawName = information;
            for (Player online : Bukkit.getOnlinePlayers()) {
                if (online.hasPermission("karhu.alerts")) {
                    if (Karhu.getInstance().getAlertsManager().hasAlertsToggled(online)) {
                        TextComponent message = new TextComponent(Karhu.getInstance().getFileManager().getPrefix() + Karhu.getInstance().getFileManager().getAlertMessage().replaceAll("%player%", player.getName()).replaceAll("%check%", information).replaceAll("%vl%", String.valueOf(violations)));
                        message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + player.getName()));
                        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Karhu.getInstance().getFileManager().getAlertHoverMessage().replaceAll("%info%", data).replaceAll("%player%", player.getName()).replaceAll("%ping%", String.valueOf(playerData.getTransPing())).replaceAll("%tps%", String.valueOf((int) Karhu.getInstance().getTPS()))).create()));
                        online.spigot().sendMessage(message);
                    }
                }
            }
            if (Karhu.getInstance().getFileManager().isLogFileEnable()) {
                LogUtil.TextFile logFile = new LogUtil.TextFile("" + player.getName().toLowerCase(), "/logs");
                try {
                    LogUtil.logToFile(logFile, "%time% | " + ChatColor.stripColor(information).replaceAll("\n", " ") + " [" + ChatColor.stripColor(data.replaceAll("\n", " ") + "]" + " [" + playerData.getTransPing() + "ms]" + "/[" + Karhu.getInstance().getTPS() + " TPS]"  + " (x" + violations + ")"));
                } catch (Exception ignored) { }

            }
            if (category.equals(Category.MOVEMENT)) {
                if (Karhu.getInstance().getFileManager().isPullback()) {
                    Tasker.run(() -> {
                        UtilPlayer.pullback(playerData.getLastLastLastLocation(), player.getLocation(), player);
                    });
                }
            }
            if (violations >= maxvl) {
                if (playerData.isExempt()) {
                    return;
                }
                if(Karhu.getInstance().getFileManager().isBypass()) {
                    if(player.hasPermission("karhu.bypass")) return;
                }
                if (EnabledUtil.checkIfIsAutoban(information.replaceAll(" ", "").replaceAll("§4^", "")) && Karhu.getInstance().getFileManager().isAutoban()) {
                    if (!playerData.isBanned()) {
                        Tasker.run(() -> {
                            Bukkit.getPluginManager().callEvent(new BanEvent(player.getPlayer(), information, data));
                        });
                        playerData.setBanned(true);
                        if (Karhu.getInstance().getFileManager().isAutobanWait()) {
                            if (!EnabledUtil.playersToBeBanned.contains(player)) {
                                EnabledUtil.playersToBeBanned.add(player);
                            }
                            for (Player staff : Bukkit.getOnlinePlayers()) {
                                if (player.hasPermission("karhu.alerts")) {
                                    TextComponent message = new TextComponent(ChatColor.translateAlternateColorCodes('&', Karhu.getInstance().getFileManager().getCountDown().replaceAll("%player%", player.getName())));
                                    message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/karhu cancelban " + player.getName()));
                                    message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(
                                            ChatColor.translateAlternateColorCodes('&', "&b&lClick to cancel ban!")).create()));
                                    staff.spigot().sendMessage(message);
                                }
                            }
                            Bukkit.getScheduler().runTaskLaterAsynchronously(Karhu.getInstance(), new Runnable() {
                                public void run() {
                                    if (EnabledUtil.playersToBeBanned.contains(player)) {
                                        EnabledUtil.playersToBeBanned.remove(player);
                                        Tasker.run(() -> {
                                            for (Player online : Bukkit.getOnlinePlayers()) {
                                                if (online.hasPermission("karhu.alerts")) {
                                                    if (Karhu.getInstance().getAlertsManager().hasAlertsToggled(online)) {
                                                        TextComponent message = new TextComponent(Karhu.getInstance().getFileManager().getAlertBanMessage().replaceAll("%player%", player.getName()).replaceAll("%check%", information));
                                                        message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/unban " + player.getName()));
                                                        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Click to UNBAN\n" + Karhu.getInstance().getFileManager().getAlertHoverMessage().replaceAll("%info%", data).replaceAll("%player%", player.getName()).replaceAll("%ping%", String.valueOf(playerData.getTransPing())).replaceAll("%tps%", String.valueOf((int) Karhu.getInstance().getTPS()))).create()));
                                                        online.spigot().sendMessage(message);
                                                    }
                                                }
                                            }
                                            LogUtil.TextFile logFile = new LogUtil.TextFile("" + player.getName().toLowerCase(), "/logs");
                                            try {
                                                LogUtil.logToFile(logFile, "PUNISHMENT APPLIED @ %time% |" + ChatColor.stripColor(information).replaceAll("\n", " ") + " [" + ChatColor.stripColor(data.replaceAll("\n", " ") + "]" + " [" + playerData.getTransPing() + "ms]" + "/[" + Karhu.getInstance().getTPS() + " TPS]"  + " (x" + violations + ")"));
                                            } catch (Exception ex) {
                                                //IGNORED
                                            }
                                            Tasker.run(() -> {
                                                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), Karhu.getInstance().getFileManager().getBanCommand().replaceAll("%player%", player.getName()).replaceAll("%check%", information));
                                            });
                                        });
                                    } else {
                                        playerData.setBanned(false);
                                    }
                                }
                            }, 20 * 15);
                        } else {
                            for (Player online : Bukkit.getOnlinePlayers()) {
                                if (online.hasPermission("karhu.alerts")) {
                                    if (Karhu.getInstance().getAlertsManager().hasAlertsToggled(online)) {
                                        TextComponent message = new TextComponent(Karhu.getInstance().getFileManager().getAlertBanMessage().replaceAll("%player%", player.getName()).replaceAll("%check%", information));
                                        message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/unban " + player.getName()));
                                        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Click to UNBAN\n" + Karhu.getInstance().getFileManager().getAlertHoverMessage().replaceAll("%info%", data).replaceAll("%player%", player.getName()).replaceAll("%ping%", String.valueOf(playerData.getTransPing())).replaceAll("%tps%", String.valueOf((int) Karhu.getInstance().getTPS()))).create()));
                                        online.spigot().sendMessage(message);
                                    }
                                }
                            }
                            LogUtil.TextFile logFile = new LogUtil.TextFile("" + player.getName().toLowerCase(), "/logs");
                            try {
                                LogUtil.logToFile(logFile, "PUNISHMENT APPLIED @ %time% | " + ChatColor.stripColor(information).replaceAll("\n", " ") + " [" + ChatColor.stripColor(data.replaceAll("\n", " ") + "]" + " [" + playerData.getTransPing() + "ms]" + "/[" + Karhu.getInstance().getTPS() + " TPS]"  + " (x" + violations + ")"));
                            } catch (Exception ex) {
                                //IGNORED
                            }
                            Tasker.run(() -> {
                                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), Karhu.getInstance().getFileManager().getBanCommand().replaceAll("%player%", player.getName()).replaceAll("%check%", information));
                            });
                        }
                    }
                }
            }
            Tasker.run(() -> {
                Bukkit.getPluginManager().callEvent(new AlertEvent(player.getPlayer(), this, information, data, violations));
            });
        });
    }

    protected void debug(Player player, String name, String stuff, Boolean broadcast) {
        if(broadcast) {
            Bukkit.broadcastMessage("§7[§c§lDEBUG§7] §c" + player.getName() + " §7N: §c" + name + " §7D: §c" + stuff);
        } else {
            player.sendMessage("§7[§c§lDEBUG§7] §c" + player.getName() + " §7N: §c" + name + " §7D: §c" + stuff);
        }
    }

    protected void handleVerbose(Player player, String information, String data) {
        Karhu.getInstance().getExecutorService().execute(() -> {
            if (player == null) return;
            if (playerData.isExempt()) {
                return;
            }
            for (Player online : Bukkit.getOnlinePlayers()) {
                if (online.hasPermission("karhu.alerts")) {
                    if (Karhu.getInstance().getAlertsManager().hasDevAlertsToggled(online)) {
                        TextComponent message = new TextComponent(Karhu.getInstance().getFileManager().getVerbosePrefix() + Karhu.getInstance().getFileManager().getVerboseMessage().replaceAll("%player%", player.getName()).replaceAll("%check%", information));
                        message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + player.getName()));
                        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Karhu.getInstance().getFileManager().getAlertHoverMessage().replaceAll("%info%", data).replaceAll("%player%", player.getName()).replaceAll("%ping%", String.valueOf(playerData.getTransPing())).replaceAll("%tps%", String.valueOf((int) Karhu.getInstance().getTPS()))).create()));
                        online.spigot().sendMessage(message);
                    }
                }
            }
        });
    }

    public abstract void handle(PacketEvent e, Player player);

    /*public void handle(final PacketEvent event) {
    }*/

    public void handleMovement(final PacketEvent event, Location to, Location from) {
    }

}
