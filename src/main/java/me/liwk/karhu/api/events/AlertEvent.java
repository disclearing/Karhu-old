package me.liwk.karhu.api.events;

import me.liwk.karhu.Karhu;
import me.liwk.karhu.check.api.Check;
import me.liwk.karhu.data.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AlertEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    private Player player;
    private Check check;
    private int vl;
    private String information;
    private String data;

    public AlertEvent(Player player, Check check, String information, String data, Integer vl) {
        this.player = player;
        this.check = check;
        this.vl = vl;
        this.information = information;
        this.data = data;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public Check getCheck() {
        return check;
    }

    public Integer getVl() {
        return vl;
    }

    public Integer setVl(Integer newVl) {
        PlayerData playerData = Karhu.getInstance().getPlayerDataManager().getPlayerData(player);
        playerData.setCheckVl(newVl, check);
        this.vl = newVl;
        return vl;
    }

    public String getInformation() {
        return information;
    }

}
