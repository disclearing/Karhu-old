package me.liwk.karhu.event;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

@Getter
public class BlockPlaceEvent extends Event {

    private final Vector blockPos;
    private final ItemStack itemStack;

    public BlockPlaceEvent(Vector blockPos, ItemStack itemStack) {
        this.blockPos = blockPos;
        this.itemStack = itemStack;
    }

}
