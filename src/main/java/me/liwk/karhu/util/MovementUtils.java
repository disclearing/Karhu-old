package me.liwk.karhu.util;

import org.bukkit.Location;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class MovementUtils {

    public static double getHorizontalDistanceSpeed(Location to, Location from, Player p) {
        double x = Math.abs(to.getX()) - Math.abs(from.getX());
        double z = Math.abs(to.getZ()) - Math.abs(from.getZ());
        return Math.sqrt(x * x + z * z);
    }

    public static int getPotionEffectLevel(Player player, PotionEffectType pet) {
        for (PotionEffect pe : player.getActivePotionEffects()) {
            if (pe.getType().getName().equals(pet.getName())) {
                return pe.getAmplifier() + 1;
            }
        }
        return 0;
    }
    public static double offset(Vector from, Vector to) {
        from.setY(0);
        to.setY(0);

        return to.subtract(from).length();
    }

    public static int getDepthStriderLevel(Player player) {
        if (player.getInventory().getBoots() != null && hasEnchantment(player.getInventory().getBoots(), Enchantment.getByName("DEPTH_STRIDER"))) {
            return player.getInventory().getBoots().getEnchantments().get(Enchantment.getByName("DEPTH_STRIDER"));
        }

        return 0;
    }

    public static int getSoulSpeedLevel(Player player) {
        if (player.getInventory().getBoots() != null && hasEnchantment(player.getInventory().getBoots(), Enchantment.getByName("SOUL_SPEED"))) {
            return player.getInventory().getBoots().getEnchantments().get(Enchantment.getByName("SOUL_SPEED"));
        }

        return 0;
    }

    public static boolean hasEnchantment(ItemStack item, Enchantment enchantment) {
        return item.getEnchantments().keySet().contains(enchantment);
    }

}