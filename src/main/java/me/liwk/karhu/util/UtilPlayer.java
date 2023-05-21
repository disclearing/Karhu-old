package me.liwk.karhu.util;

import me.liwk.karhu.Karhu;
import me.liwk.karhu.data.PlayerData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

public class UtilPlayer {

    public static double getHorizontalDistance(Location to, Location from) {
        double x = Math.abs(Math.abs(to.getX()) - Math.abs(from.getX()));
        double z = Math.abs(Math.abs(to.getZ()) - Math.abs(from.getZ()));

        return Math.sqrt(x * x + z * z);
    }

    public static boolean isOnGroundBB(Player player) {

        if(!player.getLocation().getWorld().isChunkLoaded(player.getLocation().getBlockX() >> 4, player.getLocation().getBlockZ() >> 4)) return true;

        PlayerData playerData = Karhu.getInstance().getPlayerDataManager().getPlayerData(player);

        if(playerData.getLocation() == null) return true;

        if(!player.getLocation().getWorld().isChunkLoaded(playerData.getLocation().getBlockX() >> 4, playerData.getLocation().getBlockZ() >> 4)) return true;

        final me.liwk.karhu.util.BoundingBox boundingBoxNormal = new me.liwk.karhu.util.BoundingBox(playerData.getLocation().getX(), playerData.getLocation().getY(), playerData.getLocation().getZ());
        boundingBoxNormal
                .expand(0.3, 0, 0.3)
                .expandMax(0, 1.0, 0)
                .expandMin(0.0, -(playerData.getDeltaY() < 0 ? 0.5 : 0.06), 0.0);

        final me.liwk.karhu.util.BoundingBox boundingBoxFence = new me.liwk.karhu.util.BoundingBox(playerData.getLocation().getX(), playerData.getLocation().getY(), playerData.getLocation().getZ());
        boundingBoxNormal
                .expand(0.3, 0, 0.3)
                .expandMax(0, 1.0, 0)
                .expandMin(0.0, -0.501, 0.0);

        final World world = player.getWorld();

        final KarhuStream<Block> blocksBelow = new KarhuStream<>(boundingBoxNormal.getCollidingBlocks(playerData), false);
        final KarhuStream<Block> blocksBelowFence = new KarhuStream<>(boundingBoxFence.getCollidingBlocks(playerData), false);

        final boolean touchingGround = (blocksBelow.any(m -> m.getType().isSolid() ||
                m.toString().contains("LILY") ||
                m.toString().contains("POT") ||
                m.toString().contains("CARPET") ||
                m.toString().contains("WATER") ||
                m.toString().contains("BUBBLE") ||
                m.toString().contains("LAVA") ||
                m.toString().contains("SKULL") ||
                m.toString().contains("LADDER") ||
                m.toString().contains("VINE")) || blocksBelowFence.any(m -> m.toString().contains("FENCE") ||
                m.toString().contains("WALL") ||
                m.toString().contains("SHULKER")));

        final boolean touchingIllegalBlock = boundingBoxNormal.checkBlocks(world, material -> material.toString().contains("LILY") || material == Material.BREWING_STAND || material.toString().contains("SKULL"));

        return touchingGround;
    }

    public static boolean isOnIce(Player p) {
        if (!p.getLocation().getWorld().isChunkLoaded(p.getLocation().getBlockX() >> 4, p.getLocation().getBlockZ() >> 4)) {
            return false;
        }
        return p.getLocation().getBlock().getRelative(BlockFace.DOWN).toString().contains("ICE") || p.getLocation().clone().add(0, -0.5, 0).getBlock().toString().contains("ICE");
    }

    public static boolean isOnSoulSand(Player p) {
        if (!p.getLocation().getWorld().isChunkLoaded(p.getLocation().getBlockX() >> 4, p.getLocation().getBlockZ() >> 4)) {
            return false;
        }
        return p.getLocation().getBlock().getRelative(BlockFace.DOWN).toString().contains("SOUL");
    }

    public static boolean isOnStair(Player p) {
        if (!p.getLocation().getWorld().isChunkLoaded(p.getLocation().getBlockX() >> 4, p.getLocation().getBlockZ() >> 4)) {
            return false;
        }

        if (p.getLocation().getBlock().getRelative(BlockFace.DOWN).toString().contains("STAIR")
                || p.getLocation().clone().add(0, -0.5, 0).getBlock().getRelative(BlockFace.DOWN).toString().contains("STAIR")
                || p.getLocation().clone().add(0, -0.25, 0).getBlock().getRelative(BlockFace.DOWN).toString().contains("STAIR")) {
            return true;
        }
        return false;
    }

    public static boolean isOnStair2(Player player) {
        Location loc = player.getLocation();

        if (!loc.getWorld().isChunkLoaded(loc.getBlockX() >> 4, loc.getBlockZ() >> 4)) {
            return false;
        }

        double expand = 0.3;
        for (double x = -expand; x <= expand; x += expand) {
            for (double z = -expand; z <= expand; z += expand) {
                if (getThreadSafe(loc.clone().add(z, -0.05, x)).toString().contains("STAIR")
                        || getThreadSafe(loc.clone().add(z, -0.75, x)).toString().contains("STAIR")
                        || getThreadSafe(loc.clone().add(z, -0.1, x)).toString().contains("STAIR")
                        || getThreadSafe(loc.clone().add(z, -0.3, x)).toString().contains("STAIR")
                        || getThreadSafe(loc.clone().add(z, -0.5, x)).toString().contains("STAIR")
                        || getThreadSafe(loc.clone().add(z, -0.25, x)).toString().contains("STAIR")) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isOnWall(Player player) {
        Location loc = player.getLocation();

        if (!loc.getWorld().isChunkLoaded(loc.getBlockX() >> 4, loc.getBlockZ() >> 4)) {
            return false;
        }

        double expand = 0.3;
        for (double x = -expand; x <= expand; x += expand) {
            for (double z = -expand; z <= expand; z += expand) {
                if (getThreadSafe(loc.clone().add(z, -0.05, x)).toString().contains("WALL")
                        || getThreadSafe(loc.clone().add(z, -0.75, x)).toString().contains("WALL")
                        || getThreadSafe(loc.clone().add(z, -0.1, x)).toString().contains("WALL")
                        || getThreadSafe(loc.clone().add(z, -0.3, x)).toString().contains("WALL")
                        || getThreadSafe(loc.clone().add(z, -0.5, x)).toString().contains("WALL")
                        || getThreadSafe(loc.clone().add(z, -0.25, x)).toString().contains("WALL")) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isOnFence(Player player) {
        Location loc = player.getLocation();

        if (!loc.getWorld().isChunkLoaded(loc.getBlockX() >> 4, loc.getBlockZ() >> 4)) {
            return false;
        }

        double expand = 0.3;
        for (double x = -expand; x <= expand; x += expand) {
            for (double z = -expand; z <= expand; z += expand) {
                if (getThreadSafe(loc.clone().add(z, -0.05, x)).toString().contains("FENCE")
                        || getThreadSafe(loc.clone().add(z, -0.75, x)).toString().contains("FENCE")
                        || getThreadSafe(loc.clone().add(z, -0.1, x)).toString().contains("FENCE")
                        || getThreadSafe(loc.clone().add(z, -0.3, x)).toString().contains("FENCE")
                        || getThreadSafe(loc.clone().add(z, -0.5, x)).toString().contains("FENCE")
                        || getThreadSafe(loc.clone().add(z, -0.25, x)).toString().contains("FENCE")) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isOnSkull(Player player) {
        Location loc = player.getLocation();

        if (!loc.getWorld().isChunkLoaded(loc.getBlockX() >> 4, loc.getBlockZ() >> 4)) {
            return false;
        }

        double expand = 0.3;
        for (double x = -expand; x <= expand; x += expand) {
            for (double z = -expand; z <= expand; z += expand) {
                if (getThreadSafe(loc.clone().add(z, -0.05, x)).toString().contains("SKULL") || getThreadSafe(loc.clone().add(z, -0.05, x)).toString().contains("HEAD")
                        || getThreadSafe(loc.clone().add(z, -0.75, x)).toString().contains("SKULL") || getThreadSafe(loc.clone().add(z, -0.75, x)).toString().contains("HEAD")
                        || getThreadSafe(loc.clone().add(z, -0.1, x)).toString().contains("SKULL") || getThreadSafe(loc.clone().add(z, -0.1, x)).toString().contains("HEAD")
                        || getThreadSafe(loc.clone().add(z, -0.3, x)).toString().contains("SKULL") || getThreadSafe(loc.clone().add(z, -0.3, x)).toString().contains("HEAD")
                        || getThreadSafe(loc.clone().add(z, -0.5, x)).toString().contains("SKULL") ||getThreadSafe(loc.clone().add(z, -0.5, x)).toString().contains("HEAD")
                        || getThreadSafe(loc.clone().add(z, -0.25, x)).toString().contains("SKULL") || getThreadSafe(loc.clone().add(z, -0.25, x)).toString().contains("HEAD")) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isOnPot(Player player) {
        Location loc = player.getLocation();

        if (!loc.getWorld().isChunkLoaded(loc.getBlockX() >> 4, loc.getBlockZ() >> 4)) {
            return false;
        }

        double expand = 0.3;
        for (double x = -expand; x <= expand; x += expand) {
            for (double z = -expand; z <= expand; z += expand) {
                if (getThreadSafe(loc.clone().add(z, -0.05, x)).toString().contains("POT")
                        || getThreadSafe(loc.clone().add(z, -0.75, x)).toString().contains("POT")
                        || getThreadSafe(loc.clone().add(z, -0.1, x)).toString().contains("POT")
                        || getThreadSafe(loc.clone().add(z, -0.3, x)).toString().contains("POT")
                        || getThreadSafe(loc.clone().add(z, -0.5, x)).toString().contains("POT")
                        || getThreadSafe(loc.clone().add(z, -0.25, x)).toString().contains("POT")) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isOnSlab(Player p) {

        if (!p.getLocation().getWorld().isChunkLoaded(p.getLocation().getBlockX() >> 4, p.getLocation().getBlockZ() >> 4)) {
            return false;
        }

        if (p.getLocation().getBlock().getRelative(BlockFace.DOWN).toString().contains("STEP")
                || p.getLocation().getBlock().getRelative(BlockFace.DOWN).toString().contains("SLAB")

                || p.getLocation().clone().add(0, -0.1, 0).toString().contains("STEP")
                || p.getLocation().clone().add(0, -0.1, 0).getBlock().toString().contains("SLAB")

                || getThreadSafe(p.getLocation()).toString().contains("STEP")
                || getThreadSafe(p.getLocation()).toString().contains("SLAB")) {
            return true;
        }
        return false;
    }

    public static void pullback(Location lastLastLocation, final Location location, final Player player) {
        lastLastLocation.setPitch(location.getPitch());
        lastLastLocation.setYaw(location.getYaw());
        player.teleport(lastLastLocation, PlayerTeleportEvent.TeleportCause.UNKNOWN);
    }

    public static boolean isOnBadJesusBlock(Player player) {
        Location loc = player.getLocation();

        if (!loc.getWorld().isChunkLoaded(loc.getBlockX() >> 4, loc.getBlockZ() >> 4)) {
            return false;
        }

        double expand = 0.5;
        for (double x = -expand; x <= expand; x += expand) {
            for (double z = -expand; z <= expand; z += expand) {
                if (getThreadSafe(loc.clone().add(z, -0.01, x)).toString().contains("LILY")
                        || getThreadSafe(loc.clone().add(z, -0.09, x)).toString().contains("DAYLIGHT")
                        || getThreadSafe(loc.clone().add(z, -0.09, x)).toString().contains("STAIR")
                        || getThreadSafe(loc.clone().add(z, -0.09, x)).toString().contains("CAMP")
                        || getThreadSafe(loc.clone().add(z, -0.09, x)).toString().contains("STEP")
                        || getThreadSafe(loc.clone().add(z, -0.09, x)).toString().contains("TRAP")
                        || getThreadSafe(loc.clone().add(z, -0.09, x)).toString().contains("PATH")
                        || getThreadSafe(loc.clone().add(z, -0.09, x)).toString().contains("REDSTONE")
                        || getThreadSafe(loc.clone().add(z, -0.01, x)).toString().contains("CARPET")) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isOnLily(Player player) {
        Location loc = player.getLocation();

        if (!loc.getWorld().isChunkLoaded(loc.getBlockX() >> 4, loc.getBlockZ() >> 4)) {
            return false;
        }

        double expand = 0.5;
        for (double x = -expand; x <= expand; x += expand) {
            for (double z = -expand; z <= expand; z += expand) {
                if (getThreadSafe(loc.clone().add(z, -0.09, x)).toString().contains("LILY")
                        || getThreadSafe(loc.clone().add(z, -0.01, x)).toString().contains("LILY")
                        || getThreadSafe(loc.clone().add(z, -0.005, x)).toString().contains("LILY")
                        || getThreadSafe(loc.clone().add(z, -0.05, x)).toString().contains("LILY")) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isOnCarpet(Player player) {
        Location loc = player.getLocation();

        if (!loc.getWorld().isChunkLoaded(loc.getBlockX() >> 4, loc.getBlockZ() >> 4)) {
            return false;
        }

        double expand = 0.5;
        for (double x = -expand; x <= expand; x += expand) {
            for (double z = -expand; z <= expand; z += expand) {
                if (getThreadSafe(loc.clone().add(z, -0.09, x)).toString().contains("CARPET")
                        || getThreadSafe(loc.clone().add(z, 0, x)).toString().contains("CARPET")
                        || getThreadSafe(loc.clone().add(z, -0.01, x)).toString().contains("CARPET")
                        || getThreadSafe(loc.clone().add(z, -0.015, x)).toString().contains("CARPET")
                        || getThreadSafe(loc.clone().add(z, -0.015, x)).toString().contains("CARPET")
                        || getThreadSafe(loc.clone().add(z, -0.03, x)).toString().contains("CARPET")
                        || getThreadSafe(loc.clone().add(z, -0.03, x)).toString().contains("SNOW")
                        || getThreadSafe(loc.clone().add(z, -0.05, x)).toString().contains("CARPET")) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isNearLiquid(Player player) {
        Location loc = player.getLocation();

        if (!loc.getWorld().isChunkLoaded(loc.getBlockX() >> 4, loc.getBlockZ() >> 4)) {
            return false;
        }

        double expand = 0.3;
        for (double x = -expand; x <= expand; x += expand) {
            for (double z = -expand; z <= expand; z += expand) {
                if (getThreadSafe(loc.clone().add(z, 0, x)).toString().contains("WATER") || getThreadSafe(loc.clone().add(z, 0, x)).toString().contains("LAVA") || getThreadSafe(loc.clone().add(z, -0.5, x)).toString().contains("LAVA") || getThreadSafe(loc.clone().add(z, -0.1, x)).toString().contains("BUBBLE")) {
                    return true;
                } else if (getThreadSafe(loc.clone().add(z, -0.1, x)).toString().contains("WATER") || getThreadSafe(loc.clone().add(z, -0.1, x)).toString().contains("LAVA") || getThreadSafe(loc.clone().add(z, -0.25, x)).toString().contains("LAVA")  || getThreadSafe(loc.clone().add(z, -0.1, x)).toString().contains("BUBBLE")) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isNearTrapdoor(Location location) {
        double expand = 0.45;

        if (!location.getWorld().isChunkLoaded(location.getBlockX() >> 4, location.getBlockZ() >> 4)) {
            return false;
        }

        for (double x = -expand; x <= expand; x += expand) {
            for (double z = -expand; z <= expand; z += expand) {
                if (getThreadSafe(location.clone().add(x, -0.16, z)).toString().toLowerCase().contains("trapdoor")) {
                    return true;
                } else if (getThreadSafe(location.clone().add(x, -0.1, z)).toString().contains("TRAPDOOR")) {
                    return true;
                }
                else if (getThreadSafe(location.clone().add(x, 1.90, z)).toString().contains("TRAPDOOR")) {
                    return true;
                }
                else if (getThreadSafe(location.clone().add(x, -0.012500048, z)).toString().contains("TRAPDOOR")) {
                    return true;
                }
                else if (getThreadSafe(location.clone().add(x, -0.18750, z)).toString().contains("TRAPDOOR")) {
                    return true;
                }
                else if (getThreadSafe(location.clone().add(x, -0.2, z)).toString().contains("TRAPDOOR")) {
                    return true;
                }
            }

        }
        return false;
    }

    public static boolean isOnFlyABad(Player player) {
        Location loc = player.getLocation();

        if (!loc.getWorld().isChunkLoaded(loc.getBlockX() >> 4, loc.getBlockZ() >> 4)) {
            return false;
        }

        double expand = 0.3;
        for (double x = -expand; x <= expand; x += expand) {
            for (double z = -expand; z <= expand; z += expand) {
                if (getThreadSafe(loc.clone().add(z, -0.125, x)).toString().contains("DAYLIGHT")) {
                    return true;
                } else if (getThreadSafe(loc.clone().add(z, -0.1, x)).toString().contains("TRAP")) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isNearSlime(Location location) {

        if (!location.getWorld().isChunkLoaded(location.getBlockX() >> 4, location.getBlockZ() >> 4)) {
            return false;
        }

        double expand = 0.4;

        for (double x = -expand; x <= expand; x += expand) {
            for (double z = -expand; z <= expand; z += expand) {
                if (getThreadSafe(location.clone().add(x, -0.1, z)).toString().contains("SLIME")) {
                    return true;
                }
                if (getThreadSafe(location.clone().add(x, -0.2, z)).toString().contains("SLIME")) {
                    return true;
                }

            }

        }
        return false;
    }

    public static boolean isLandingSoonOnSlime(Location location) {

        if (!location.getWorld().isChunkLoaded(location.getBlockX() >> 4, location.getBlockZ() >> 4)) {
            return false;
        }

        double expand = 0.3;
        for (double x = -expand; x <= expand; x += expand) {
            for (double z = -expand; z <= expand; z += expand) {
                if (getThreadSafe(location.clone().add(x, -0.75, z)).toString().contains("SLIME")) {
                    return true;
                }
                if (getThreadSafe(location.clone().add(x, -1, z)).toString().contains("SLIME")) {
                    return true;
                }
                if (getThreadSafe(location.clone().add(x, -2, z)).toString().contains("SLIME")) {
                    return true;
                }

            }

        }
        return false;
    }

    public static boolean isInWeb(Player player) {

        if (!player.getLocation().getWorld().isChunkLoaded(player.getLocation().getBlockX() >> 4, player.getLocation().getBlockZ() >> 4)) {
            return false;
        }

        double expand = 0.3;
        for (double x = -expand; x <= expand; x += expand) {
            for (double z = -expand; z <= expand; z += expand) {
                if (getThreadSafe(player.getLocation().clone().add(z, 0, x)).toString().contains("WEB")) {
                    return true;
                }else if (getThreadSafe(player.getLocation().clone().add(z, +1.6, x)).toString().contains("WEB")) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isNearGround(Location location) {

        if (!location.getWorld().isChunkLoaded(location.getBlockX() >> 4, location.getBlockZ() >> 4)) {
            return false;
        }

        double expand = 0.3;
        for (double x = -expand; x <= expand; x += expand) {
            for (double z = -expand; z <= expand; z += expand) {
                if (getThreadSafe(location.clone().add(x, -0.5001, z)).isSolid()) {
                    return true;
                }
            }

        }
        return false;
    }

    public static boolean isNearPiston(Location location) {

        if (!location.getWorld().isChunkLoaded(location.getBlockX() >> 4, location.getBlockZ() >> 4)) {
            return false;
        }

        double expand = 5;
        for (double x = -expand; x <= expand; x += expand) {
            for (double z = -expand; z <= expand; z += expand) {
                if (getThreadSafe(location.clone().add(x, 0, z)).toString().contains("PISTON")) {
                    return true;
                }
            }

        }
        return false;
    }


    public static boolean isAboveLiquid(Player player) {

        if (!player.getLocation().getWorld().isChunkLoaded(player.getLocation().getBlockX() >> 4, player.getLocation().getBlockZ() >> 4)) {
            return false;
        }

        Location water1 = player.getLocation().clone();
        Location water2 = player.getLocation().clone();
        Location air = player.getLocation().clone();
        water1.setZ(water1.getZ() + 0.3);
        water1.setY(water1.getY());
        water1.setX(water1.getX() + 0.3);

        water2.setZ(water2.getZ() - 0.3);
        water2.setY(water2.getY());
        water2.setX(water2.getX() - 0.3);

        air.setY(air.getY() + 0.1);

        return water1.getBlock().getRelative(BlockFace.DOWN).isLiquid()
                && water2.getBlock().getRelative(BlockFace.DOWN).isLiquid()
                && getThreadSafe(air) == Material.AIR;
    }


    public static boolean isNextToWall(Player p) {
        Location loc = p.getLocation();

        if (!loc.getWorld().isChunkLoaded(loc.getBlockX() >> 4, loc.getBlockZ() >> 4)) {
            return false;
        }

        Location xp = new Location(loc.getWorld(), loc.getX() + 0.5, loc.getY(), loc.getZ());
        Location xn = new Location(loc.getWorld(), loc.getX() - 0.5, loc.getY(), loc.getZ());
        Location zp = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ() + 0.5);
        Location zn = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ() - 0.5);

        if(getThreadSafe(xp) == null || getThreadSafe(xn) == null || getThreadSafe(zp) == null || getThreadSafe(zn) == null) {
            return false;
        }

        return getThreadSafe(xp).isSolid() || getThreadSafe(xn).isSolid() || getThreadSafe(zp).isSolid() || getThreadSafe(zn).isSolid();
    }

    public static boolean blockNearHead(Player player) {

        if (!player.getLocation().getWorld().isChunkLoaded(player.getLocation().getBlockX() >> 4, player.getLocation().getBlockZ() >> 4)) {
            return false;
        }

        double expand = 0.3;
        for (double x = -expand; x <= expand; x += expand) {
            for (double z = -expand; z <= expand; z += expand) {
                if (getThreadSafe(player.getLocation().clone().add(z, 2, x)).isSolid()) {
                    return true;
                } else if (getThreadSafe(player.getLocation().clone().add(z, 1.5001, x)).isSolid()) {
                    return true;
                } else if (getThreadSafe(player.getLocation().clone().add(z, 1.5001, x)).toString().contains("SKULL") || getThreadSafe(player.getLocation().clone().add(z, 1.5001, x)).toString().contains("HEAD")) {
                    return true;
                } else if (getThreadSafe(player.getLocation().clone().add(z, 1.75, x)).toString().contains("SKULL") || getThreadSafe(player.getLocation().clone().add(z, 1.75, x)).toString().contains("HEAD")) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isNearWall(Player player) {

        if (!player.getLocation().getWorld().isChunkLoaded(player.getLocation().getBlockX() >> 4, player.getLocation().getBlockZ() >> 4)) {
            return false;
        }

        double expand = 1;
        for (double x = -expand; x <= expand; x += expand) {
            for (double z = -expand; z <= expand; z += expand) {
                if(getThreadSafe(player.getLocation().clone().add(z, 0, x)) == null) {
                    return false;
                } else if (getThreadSafe(player.getLocation().clone().add(z, 0, x)).isSolid()) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isNearScaffolding(Player player) {

        if (!player.getLocation().getWorld().isChunkLoaded(player.getLocation().getBlockX() >> 4, player.getLocation().getBlockZ() >> 4)) {
            return false;
        }

        double expand = 1;
        for (double x = -expand; x <= expand; x += expand) {
            for (double z = -expand; z <= expand; z += expand) {
                if (getThreadSafe(player.getLocation().clone().add(z, 0, x)).toString().contains("SCAFFOLD")) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isNearAnvil(Player player) {

        if (!player.getLocation().getWorld().isChunkLoaded(player.getLocation().getBlockX() >> 4, player.getLocation().getBlockZ() >> 4)) {
            return false;
        }

        double expand = 0.5;
        for (double x = -expand; x <= expand; x += expand) {
            for (double z = -expand; z <= expand; z += expand) {
                if (getThreadSafe(player.getLocation().clone().add(z, 0, x)).toString().contains("ANVIL")) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isNearHoney(Player player) {

        if (!player.getLocation().getWorld().isChunkLoaded(player.getLocation().getBlockX() >> 4, player.getLocation().getBlockZ() >> 4)) {
            return false;
        }

        double expand = 0.5;
        for (double x = -expand; x <= expand; x += expand) {
            for (double z = -expand; z <= expand; z += expand) {
                //Bukkit.broadcastMessage(getThreadSafe(player.getLocation().clone().add(z, 0, x)).toString());
                if (getThreadSafe(player.getLocation().clone().add(z, 0, x)).toString().contains("HONEY")) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isNearWeb(Player player) {

        if (!player.getLocation().getWorld().isChunkLoaded(player.getLocation().getBlockX() >> 4, player.getLocation().getBlockZ() >> 4)) {
            return false;
        }

        double expand = 0.5;
        for (double x = -expand; x <= expand; x += expand) {
            for (double z = -expand; z <= expand; z += expand) {
                if (getThreadSafe(player.getLocation().clone().add(z, 0, x)).toString().contains("WEB")) {
                    return true;
                } else if (getThreadSafe(player.getLocation().clone().add(z, 0.5, x)).toString().contains("WEB")) {
                    return true;
                } else if (getThreadSafe(player.getLocation().clone().add(z, 0.75, x)).toString().contains("WEB")) {
                    return true;
                } else if (getThreadSafe(player.getLocation().clone().add(z, 1, x)).toString().contains("WEB")) {
                    return true;
                } else if (getThreadSafe(player.getLocation().clone().add(z, 1.3, x)).toString().contains("WEB")) {
                    return true;
                } else if (getThreadSafe(player.getLocation().clone().add(z, 1.5, x)).toString().contains("WEB")) {
                    return true;
                } else if (getThreadSafe(player.getLocation().clone().add(z, 1.75, x)).toString().contains("WEB")) {
                    return true;
                } else if (getThreadSafe(player.getLocation().clone().add(z, 2, x)).toString().contains("WEB")) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isNearIce(Player player) {

        if (!player.getLocation().getWorld().isChunkLoaded(player.getLocation().getBlockX() >> 4, player.getLocation().getBlockZ() >> 4)) {
            return false;
        }

        double expand = 0.5;
        for (double x = -expand; x <= expand; x += expand) {
            for (double z = -expand; z <= expand; z += expand) {
                if (getThreadSafe(player.getLocation().clone().add(z, -0.5, x)).toString().contains("ICE")) {
                    return true;
                } else if (getThreadSafe(player.getLocation().clone().add(z, -0.25, x)).toString().contains("ICE")) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isNearClimbable(Player player) {

        if (!player.getLocation().getWorld().isChunkLoaded(player.getLocation().getBlockX() >> 4, player.getLocation().getBlockZ() >> 4)) {
            return false;
        }

        double expand = 0.5;
        for (double x = -expand; x <= expand; x += expand) {
            for (double z = -expand; z <= expand; z += expand) {
                if (getThreadSafe(player.getLocation().clone().add(z, 0, x)).toString().contains("LADDER") || getThreadSafe(player.getLocation().clone().add(z, player.getLocation().getY(), x)).toString().contains("VINE")) {
                    return true;
                }
            }
        }
        return false;
    }


    public static boolean isClimbableBlock(Player player) {

        if (!player.getLocation().getWorld().isChunkLoaded(player.getLocation().getBlockX() >> 4, player.getLocation().getBlockZ() >> 4)) {
            return false;
        }

        return getThreadSafe(player.getLocation()).toString().contains("LADDER") || getThreadSafe(player.getLocation()).toString().contains("VINE");
    }

    public static double getBlockFriction(Player player) {
        return isNearIce(player) ? 0.98 : 0.60;
    }

    public static Material getThreadSafe(Location loc) {
        if (loc.getWorld().isChunkLoaded(loc.getBlockX() >> 4, loc.getBlockZ() >> 4)) {
            return loc.getBlock().getType();
        }
        return Material.AIR;
    }
}
