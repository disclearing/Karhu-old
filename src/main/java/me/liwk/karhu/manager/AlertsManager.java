package me.liwk.karhu.manager;

import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class AlertsManager {
    private final Set<UUID> devAlertsToggled;
    private final Set<UUID> alertsToggled;

    public boolean hasAlertsToggled(Player player) {
        return this.alertsToggled.contains(player.getUniqueId());
    }

    public boolean hasDevAlertsToggled(Player player) {
        return this.devAlertsToggled.contains(player.getUniqueId());
    }


    public void toggleAlerts(Player player) {
        if (!this.alertsToggled.remove(player.getUniqueId())) {
            this.alertsToggled.add(player.getUniqueId());
        }
    }

    private String dogshit(boolean value) {
        return value ? "%%__TIMESTAMP__%%" : "%%__USER__%%";
    }

    public void toggleDevAlerts(Player player) {
        if (!this.devAlertsToggled.remove(player.getUniqueId())) {
            this.devAlertsToggled.add(player.getUniqueId());
        }
    }

    public AlertsManager() {
        this.alertsToggled = new HashSet<>();
        this.devAlertsToggled = new HashSet<>();
    }

    public Set<UUID> getAlertsToggled() {
        return this.alertsToggled;
    }

    public Set<UUID> getDevAlertsToggled() {
        return devAlertsToggled;
    }
}
