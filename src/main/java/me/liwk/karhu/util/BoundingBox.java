package me.liwk.karhu.util;

import lombok.Getter;
import me.liwk.karhu.data.PlayerData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@Getter
public final class BoundingBox {

    private double minX, minY, minZ;

    private double maxX, maxY, maxZ;

    private boolean worldLoaded;

    private long timestamp;

    public BoundingBox(final Location position) {
        this(position.getX(), position.getY(), position.getZ());
    }

    public BoundingBox(final double x, final double y, final double z) {
        this(x, x, y, y, z, z);

        timestamp = System.currentTimeMillis();
    }

    public BoundingBox(double minX, double maxX, double minY, double maxY, double minZ, double maxZ) {
        if (minX < maxX) {
            this.minX = minX;
            this.maxX = maxX;
        } else {
            this.minX = maxX;
            this.maxX = minX;
        }
        if (minY < maxY) {
            this.minY = minY;
            this.maxY = maxY;
        } else {
            this.minY = maxY;
            this.maxY = minY;
        }
        if (minZ < maxZ) {
            this.minZ = minZ;
            this.maxZ = maxZ;
        } else {
            this.minZ = maxZ;
            this.maxZ = minZ;
        }
    }

    public double distance(final Location location) {
        return Math.sqrt(Math.min(Math.pow(location.getX() - this.minX, 2), Math.pow(location.getX() - this.maxX, 2)) + Math.min(Math.pow(location.getZ() - this.minZ, 2), Math.pow(location.getZ() - this.maxZ, 2)));
    }

    public double distance(final double x, final double z) {

        final double dx = Math.min(Math.pow(x - minX, 2), Math.pow(x - maxX, 2));
        final double dz = Math.min(Math.pow(z - minZ, 2), Math.pow(z - maxZ, 2));

        return Math.sqrt(dx + dz);
    }

    public double distance(final BoundingBox box) {

        final double dx = Math.min(Math.pow(box.minX - minX, 2), Math.pow(box.maxX - maxX, 2));
        final double dz = Math.min(Math.pow(box.minZ - minZ, 2), Math.pow(box.maxZ - maxZ, 2));

        return Math.sqrt(dx + dz);
    }

    public double angle(final World world, final BoundingBox box) {
        final Vector homeVector = new Vector(minX, minY, minZ);
        final Vector outVector = new Vector(box.minX, box.minY, box.minZ);

        return outVector.subtract(homeVector).setY(0).angle(outVector.toLocation(world).getDirection().setY(0));
    }

    public Vector getDirection(final World world) {
        return new Location(world, minX, minY, minZ).getDirection();
    }

    public BoundingBox add(final BoundingBox box) {

        this.minX += box.minX;
        this.minY += box.minY;
        this.minZ += box.minZ;

        this.maxX += box.maxX;
        this.maxY += box.maxY;
        this.maxZ += box.maxZ;

        return this;
    }

    public BoundingBox move(final double x, final double y, final double z) {

        this.minX += x;
        this.minY += y;
        this.minZ += z;

        this.maxX += x;
        this.maxY += y;
        this.maxZ += z;

        return this;
    }

    public BoundingBox expand(final double x, final double y, final double z) {

        this.minX -= x;
        this.minY -= y;
        this.minZ -= z;

        this.maxX += x;
        this.maxY += y;
        this.maxZ += z;

        return this;
    }

    public BoundingBox expandMin(final double x, final double y, final double z) {
        this.minX += x;
        this.minY += y;
        this.minZ += z;
        return this;
    }

    public BoundingBox expandMax(double x, double y, double z) {
        this.maxX += x;
        this.maxY += y;
        this.maxZ += z;

        return this;
    }


    public boolean checkBlocks(final World world, final Predicate<Material> predicate) {
        final int first = (int) Math.floor(this.minX);
        final int second = (int) Math.ceil(this.maxX);
        final int third = (int) Math.floor(this.minY);
        final int forth = (int) Math.ceil(this.maxY);
        final int fifth = (int) Math.floor(this.minZ);
        final int sixth = (int) Math.ceil(this.maxZ);

        final ArrayList<Block> list = new ArrayList<>();
        worldLoaded = true;

        Location locMin = new Location(world, first, third, fifth);
        Location locMax = new Location(world, second, forth, sixth);

        if(!locMin.getWorld().isChunkLoaded(locMin.getBlockX() >> 4, locMin.getBlockZ() >> 4) || !locMax.getWorld().isChunkLoaded(locMax.getBlockX() >> 4, locMax.getBlockZ() >> 4)) {
            worldLoaded = false;
        }

        if(worldLoaded) list.add(world.getBlockAt(first, third, fifth));

        for (int i = first; i < second; ++i) {
            for (int j = third; j < forth; ++j) {
                for (int k = fifth; k < sixth; ++k) {

                    Location locShit = new Location(world, i, j, k);
                    if(locShit.getWorld().isChunkLoaded(locShit.getBlockX() >> 4, locShit.getBlockZ() >> 4)) {
                        list.add(world.getBlockAt(i, j, k));
                    } else {
                        worldLoaded = false;
                    }
                }
            }
        }

        return list.stream().allMatch(block -> predicate.test(block.getType()));
    }

    public List<Block> getCollidingBlocks(PlayerData data) {

        final List<Block> blocks = new ArrayList<>();

        final int xFloor = (int) Math.floor(minX);
        final int xCeil = (int) Math.ceil(maxX);

        final int yFloor = (int) Math.floor(minY);
        final int yCeil = (int) Math.ceil(maxY);

        final int zFloor = (int) Math.floor(minZ);
        final int zCeil = (int) Math.ceil(maxZ);

        for (int x = xFloor; x < xCeil; x++) {

            for (int y = yFloor; y < yCeil; y++) {

                for (int z = zFloor; z < zCeil; z++) {

                    final Location loc = new Location(data.getDataPlayer().getWorld(), x, y, z);

                    if (loc.getWorld().isChunkLoaded(loc.getBlockX() >> 4, loc.getBlockZ() >> 4)) {
                        final Block b = loc.getBlock();
                        blocks.add(b);
                    }

                }

            }

        }

        return blocks;
    }

    public double getCenterX() {
        return (this.minX + this.maxX) / 2.0;
    }

    public double getCenterY() {
        return (this.minY + this.maxY) / 2.0;
    }

    public double getCenterZ() {
        return (this.minZ + this.maxZ) / 2.0;
    }

    public long getTimestamp() {
        return timestamp;
    }
}

final class BlockPosition {

    private int x, y, z;


    public BlockPosition(final int x, final int y, final int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public void setX(final int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(final int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(final int z) {
        this.z = z;
    }

}
