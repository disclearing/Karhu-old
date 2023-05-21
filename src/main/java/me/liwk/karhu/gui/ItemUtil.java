package me.liwk.karhu.gui;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ItemUtil {

    public static ItemStack makeItem(Material mat, int amount, String displayName, List<String> lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
        meta.setLore(lore);
        item.setAmount(amount);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack makeItem(Material mat, int amount, String displayName) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
        item.setAmount(amount);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack makeItem(Material mat, int amount) {
        ItemStack item = new ItemStack(mat, amount);
        return item;
    }

    public static ItemStack makeItem(Material mat) {
        return new ItemStack(mat);
    }
}
