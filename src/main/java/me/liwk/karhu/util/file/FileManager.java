package me.liwk.karhu.util.file;

import lombok.Getter;
import me.liwk.karhu.Karhu;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class FileManager {

    @Getter private FileConfiguration settings = null;
    @Getter private File settingsFile = null;
    @Getter private FileConfiguration checks = null;
    @Getter private File checksFile = null;
    @Getter private String prefix = null;
    @Getter private String verbosePrefix = null;
    @Getter private String alertMessage = null;
    @Getter private String verboseMessage = null;
    @Getter private String alertHoverMessage = null;
    @Getter private String alertBanMessage = null;
    @Getter private String banCommand = null;
    @Getter private boolean logFileEnable;
    @Getter private boolean autoban;
    @Getter private boolean autobanWait;
    @Getter private boolean antiVPN;
    @Getter private boolean pullback;
    @Getter private String defaultVersion;
    @Getter private String guiName;
    @Getter private String command;
    @Getter private boolean transaction;
    @Getter private boolean bypass;

    @Getter private String countDown;

    //CHECKS
    /*@Getter private boolean ReachA;
    @Getter private boolean ReachB;

    @Getter private boolean AimA;
    @Getter private boolean AimB;
    @Getter private boolean AimC;
    @Getter private boolean AimD;

    @Getter private boolean AutoclickerA;
    @Getter private boolean AutoclickerB;
    @Getter private boolean AutoclickerC;

    @Getter private boolean KillauraA;
    @Getter private boolean KillauraB;
    @Getter private boolean KillauraC;
    @Getter private boolean KillauraE;
    @Getter private boolean KillauraF;*/



    public void load(Plugin plugin) {
        this.settingsFile = new File(plugin.getDataFolder(), "Settings.yml");
        if(!this.settingsFile.exists()) {
            plugin.saveResource("Settings.yml", false);
            Karhu.getInstance().getLogger().info("Generating file Settings.yml");
        } else {
            Karhu.getInstance().getLogger().info("Loading file Settings.yml");
        }
        this.settings = YamlConfiguration.loadConfiguration(this.settingsFile);

        if(!this.settings.isSet("License")) {
            this.settings.set("License", "null");
        }

        if(!this.settings.isSet("Prefix")) {
            this.settings.set("Prefix", "&8[&b&lKarhu&8] ");
        }
        if(!this.settings.isSet("VerbosePrefix")) {
            this.settings.set("VerbosePrefix", "&8[&b&lVERBOSE&8] ");
        }
        if(!this.settings.isSet("AlertsMessage")) {
            this.settings.set("AlertsMessage", "&3%player% &8- &3%check% &3(&bx%vl%&3)");
        }
        if(!this.settings.isSet("VerboseMessage")) {
            this.settings.set("VerboseMessage", "&3%player% &8x &3%check%");
        }
        if(!this.settings.isSet("AlertsHoverableMessage")) {
            this.settings.set("AlertsHoverableMessage", "&7%info% (Ping: %ping% TPS: %tps%) &b(Click to teleport)");
        }
        if(!this.settings.isSet("AlertsBanMessage")) {
            this.settings.set("AlertsBanMessage", "&b%player% &7got autobanned for &b%check%");
        }
        if(!this.settings.isSet("BanCommand")) {
            this.settings.set("BanCommand", "tempban %player% 60d Hacked client");
        }
        if(!this.settings.isSet("PlayerLogFile")) {
            this.settings.set("PlayerLogFile", true);
        }
        if(!this.settings.isSet("autoban")) {
            this.settings.set("autoban", true);
        }
        if(!this.settings.isSet("autoban-cancel-wait")) {
            this.settings.set("autoban-cancel-wait", false);
        }

        if(!this.settings.isSet("pullback")) {
            this.settings.set("pullback", false);
        }

        if(!this.settings.isSet("default-version")) {
            this.settings.set("default-version", "1_8");
        }

        if(!this.settings.isSet("gui.name")) {
            this.settings.set("gui.name", "&bKarhu");
        }

        if(!this.settings.isSet("commands.main-command.name")) {
            this.settings.set("commands.main-command.name", "karhu");
        }

        if(!this.settings.isSet("autoban-countdown-message")) {
            this.settings.set("autoban-countdown-message", "&8[&b&lKarhu&8] &b&l%player% &bwill be autobanned in 15 seconds");
        }

        if(!this.settings.isSet("transaction-exploit-fix")) {
            this.settings.set("transaction-exploit-fix", "true");
        }

        if(!this.settings.isSet("bypass-permission")) {
            this.settings.set("bypass-permission", "false");
        }

        this.prefix = Objects.requireNonNull(this.settings.getString("Prefix")).replace("&", "§");
        this.verbosePrefix = Objects.requireNonNull(this.settings.getString("VerbosePrefix")).replace("&", "§");
        this.alertMessage = Objects.requireNonNull(this.settings.getString("AlertsMessage")).replace("&", "§");
        this.verboseMessage = Objects.requireNonNull(this.settings.getString("VerboseMessage")).replace("&", "§");
        this.alertHoverMessage = Objects.requireNonNull(this.settings.getString("AlertsHoverableMessage")).replace("&", "§");
        this.alertBanMessage = Objects.requireNonNull(this.settings.getString("AlertsBanMessage")).replace("&", "§");
        this.banCommand = this.settings.getString("BanCommand");
        this.logFileEnable = this.settings.getBoolean("PlayerLogFile");
        this.autoban = this.settings.getBoolean("autoban");
        this.autobanWait = this.settings.getBoolean("autoban-cancel-wait");
        this.antiVPN = this.settings.getBoolean("antiVPN");
        this.pullback = this.settings.getBoolean("pullback");
        this.defaultVersion = this.settings.getString("default-version");
        this.guiName = this.settings.getString("gui.name");
        this.command = this.settings.getString("commands.main-command.name");
        this.countDown = this.settings.getString("autoban-countdown-message");
        this.transaction = this.settings.getBoolean("transaction-exploit-fix");
        this.bypass = this.settings.getBoolean("bypass-permission");
    }

    public void save() {
        try {
            this.settings.save(this.settingsFile);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public FileManager(Plugin plugin) {
        this.load(plugin);
    }
}
