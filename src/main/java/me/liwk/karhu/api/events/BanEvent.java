package me.liwk.karhu.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BanEvent extends Event implements Cancellable {
    private static final HandlerList HANDLER_LIST;
    private final Player player;
    private final String information;
    private final String reason;
    private boolean cancelled;

    public static HandlerList getHandlerList() {
        return BanEvent.HANDLER_LIST;
    }

    @Override
    public HandlerList getHandlers() {
        return BanEvent.HANDLER_LIST;
    }

    @Override
    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }

    public Player getPlayer() {
        return this.player;
    }

    public String getReason() {
        return this.reason;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    public BanEvent(final Player player, final String reason, final String information) {
        this.player = player;
        this.information = information;
        this.reason = reason;
    }

    static {
        HANDLER_LIST = new HandlerList();
    }
}
