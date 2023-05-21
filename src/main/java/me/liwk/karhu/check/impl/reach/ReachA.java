package me.liwk.karhu.check.impl.reach;

import io.github.retrooper.packetevents.event.PacketEvent;
import me.liwk.karhu.Karhu;
import me.liwk.karhu.check.api.Category;
import me.liwk.karhu.check.api.Check;
import me.liwk.karhu.data.PlayerData;
import me.liwk.karhu.event.FlyingEvent;
import me.liwk.karhu.util.AxisAlignedBB;
import me.liwk.karhu.util.MathUtil;
import me.liwk.karhu.util.pair.Pair;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class ReachA extends Check {

    private double buffer;

    public ReachA(final PlayerData playerData) {
        super(Category.COMBAT, playerData);
    }

    @Override
    public void handle(PacketEvent event, Player player) {
        if (event instanceof FlyingEvent) {
            Location to = ((FlyingEvent) event).toLocation();
            if (!player.getGameMode().equals(GameMode.CREATIVE)
                    && playerData.getLastTarget() != null
                    && playerData.getLastAttackTick() <= 1
                    && !playerData.isLagging(System.currentTimeMillis(), 250L)
                    && playerData.getTotalTicks() - playerData.getLastPacketDrop() > 20
                    && playerData.getPastLocations().size() >= 20
                    && playerData.getTeleportLocation() == null
                    && player.getVehicle() == null
                    && System.currentTimeMillis() - playerData.getLastTeleport() > 5000L
                    && System.currentTimeMillis() - playerData.getLastLag() > 100L
                    && System.currentTimeMillis() - playerData.getLastDelayedPacket() > 160L) {

                Player target = playerData.getLastTarget();

                if(Karhu.getInstance().getPlayerDataManager().getPlayerData(target).lastServerPositionTick < 100 || Karhu.getInstance().getPlayerDataManager().getPlayerData(target).getTotalTicks() - Karhu.getInstance().getPlayerDataManager().getPlayerData(target).getLastPacketDrop() < 15) return;

                AxisAlignedBB axisalignedbb = new AxisAlignedBB(0, 0, 0, 0, 0, 0);
                boolean a = false;

                final int nowTicks = playerData.getTotalTicks();
                final int pingTicks = MathUtil.getPingInTicks(playerData.getPing()) + 3;

                final Vector origin = to.toVector();

                double distance = -1;
                int collided = 0;

                for (Pair<AxisAlignedBB, Integer> pair : playerData.getPastLocations()) {
                    if (Math.abs(nowTicks - pair.getY() - pingTicks) < 2) {
                        if (!a) {
                            axisalignedbb = pair.getX();
                            a = true;
                        } else {
                            axisalignedbb.minX = Math.min(axisalignedbb.minX, pair.getX().minX);
                            axisalignedbb.maxX = Math.max(axisalignedbb.maxX, pair.getX().maxX);
                            axisalignedbb.minY = Math.min(axisalignedbb.minY, pair.getX().minY);
                            axisalignedbb.maxY = Math.max(axisalignedbb.maxY, pair.getX().maxY);
                            axisalignedbb.minZ = Math.min(axisalignedbb.minZ, pair.getX().minZ);
                            axisalignedbb.maxZ = Math.max(axisalignedbb.maxZ, pair.getX().maxZ);
                        }
                        double boxX = Math.abs(axisalignedbb.minX - axisalignedbb.maxX) / 2, boxZ = Math.abs(axisalignedbb.minZ - axisalignedbb.maxZ) / 2;
                        Vector loc = new Vector(axisalignedbb.minX + boxX, 0, axisalignedbb.minZ + boxZ);

                        distance = (origin.setY(0).distance(loc) - Math.hypot(boxX, boxZ) - 0.1f) - 0.05f;
                        if(distance > 3.05)
                            ++collided;
                    }
                }

                if(distance > 3.05 && collided > 2) {
                    if ((buffer += 1.5) > 3.5) {
                        handleFlag(player, "Reach A", "§b* §fR: §b" + distance + "\n§b* §fC: §b" + collided + "\n§b* §fE: §b3.0", getBanVL("ReachA"), 30000L);
                    }
                } else {
                    buffer = Math.max(buffer - 0.75, 0);
                }
            }
        }
    }
}
