package me.liwk.karhu.handler;


import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.event.PacketEvent;
import io.github.retrooper.packetevents.event.PacketListener;
import io.github.retrooper.packetevents.event.annotation.PacketHandler;
import io.github.retrooper.packetevents.event.impl.PacketReceiveEvent;
import io.github.retrooper.packetevents.event.impl.PacketSendEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.in.blockdig.WrappedPacketInBlockDig;
import io.github.retrooper.packetevents.packetwrappers.in.blockplace.WrappedPacketInBlockPlace;
import io.github.retrooper.packetevents.packetwrappers.in.clientcommand.WrappedPacketInClientCommand;
import io.github.retrooper.packetevents.packetwrappers.in.custompayload.WrappedPacketInCustomPayload;
import io.github.retrooper.packetevents.packetwrappers.in.entityaction.WrappedPacketInEntityAction;
import io.github.retrooper.packetevents.packetwrappers.in.flying.WrappedPacketInFlying;
import io.github.retrooper.packetevents.packetwrappers.in.helditemslot.WrappedPacketInHeldItemSlot;
import io.github.retrooper.packetevents.packetwrappers.in.transaction.WrappedPacketInTransaction;
import io.github.retrooper.packetevents.packetwrappers.in.useentity.WrappedPacketInUseEntity;
import io.github.retrooper.packetevents.packetwrappers.out.entityvelocity.WrappedPacketOutEntityVelocity;
import io.github.retrooper.packetevents.packetwrappers.out.position.WrappedPacketOutPosition;
import io.github.retrooper.packetevents.packetwrappers.out.transaction.WrappedPacketOutTransaction;
import io.github.retrooper.packetevents.utils.player.ClientVersion;
import io.github.retrooper.packetevents.utils.server.ServerVersion;
import me.liwk.karhu.Karhu;
import me.liwk.karhu.check.api.Check;
import me.liwk.karhu.data.PlayerData;
import me.liwk.karhu.event.*;
import me.liwk.karhu.playerhandler.VersionBridgeHelper;
import me.liwk.karhu.util.*;
import me.liwk.karhu.util.check.EnabledUtil;
import me.liwk.karhu.util.pair.Pair;
import me.liwk.karhu.util.task.Tasker;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class PacketProcessor extends MathUtil implements PacketListener {

    @PacketHandler
    public void onPacketReceive(PacketReceiveEvent e) {
        final Player p = e.getPlayer();
        PlayerData playerData = null;

        if (p != null) {
            playerData = Karhu.getInstance().getPlayerDataManager().getPlayerData(e.getPlayer());
        }

        if (playerData != null && playerData.isObjectLoaded()) {
            final long timestamp = e.getTimestamp();
            PacketEvent callEvent = e;
            if (PacketType.Client.Util.isInstanceOfFlying(e.getPacketId())) {
                final WrappedPacketInFlying flying = new WrappedPacketInFlying(e.getNMSPacket());
                long packetDiff = System.currentTimeMillis() - playerData.getLastFlying();
                long now = System.currentTimeMillis();
                int nowTicks = playerData.getTotalTicks();
                double x = flying.getX();
                double y = flying.getY();
                double z = flying.getZ();
                float yaw = flying.getYaw();
                float pitch = flying.getPitch();
                boolean onGround = flying.isOnGround();

                playerData.lagTick = packetDiff < 2L;

                boolean delayed = nowTicks - playerData.lastFlyingTicks > 2;

                final boolean lagging = nowTicks - playerData.lastDroppedPackets < 2;

                playerData.setDroppedPackets(lagging);

                if (packetDiff > 150L) {
                    playerData.lastLag = now;
                }

                if (packetDiff > 300L) {
                    playerData.lastLag2 = now;
                }

                if (packetDiff > 100L) {
                    playerData.setLastDelayed(now);
                }

                if (packetDiff < 25L) {
                    playerData.setLastFast(now);
                }

                if (now - playerData.getLastFlying() >= 110L) {
                    playerData.setLastDelayedPacket(now);
                }

                if (flying.isPosition()) {
                    playerData.setLastMovePkt(now);
                }

                if (flying.isLook()) {
                    playerData.setLastMovement(now);
                }

                playerData.totalTicks++;
                playerData.lastAttackTick++;
                playerData.lastServerPositionTick++;

                Location location = null;

                if (flying.isPosition()) {
                    if (playerData.getLocation() != null) {
                        location = new Location(e.getPlayer().getWorld(), x, y, z, playerData.getLocation().getYaw(), playerData.getLocation().getPitch());
                        Location lastLocation = (playerData.getLocation() != null) ? playerData.getLocation() : location;
                        playerData.setLastLastLastLocation(playerData.getLastLastLocation());
                        playerData.setLastLastLocation(playerData.getLastLocation());
                        playerData.setLastLocation(lastLocation);
                        playerData.setLocation(location);
                        playerData.positionTicks++;
                        callEvent = new FlyingEvent(x, y, z, playerData.getLocation().getYaw(), playerData.getLocation().getPitch(),
                                flying.isPosition(),
                                flying.isLook(),
                                onGround,
                                p.getWorld());
                    } else {
                        location = new Location(e.getPlayer().getWorld(), x, y, z, p.getLocation().getYaw(), p.getLocation().getPitch());
                        Location lastLocation = (playerData.getLocation() != null) ? playerData.getLocation() : location;
                        playerData.setLastLastLastLocation(playerData.getLastLastLocation());
                        playerData.setLastLastLocation(playerData.getLastLocation());
                        playerData.setLastLocation(lastLocation);
                        playerData.setLocation(location);
                        playerData.positionTicks++;
                        callEvent = new FlyingEvent(x, y, z, playerData.getLocation().getYaw(), playerData.getLocation().getPitch(),
                                flying.isPosition(),
                                flying.isLook(),
                                onGround,
                                p.getWorld());
                    }
                } else if (flying.isLook()) {
                    if (playerData.getLocation() != null) {
                        location = new Location(p.getWorld(), playerData.getLocation().getX(), playerData.getLocation().getY(), playerData.getLocation().getZ(), flying.getYaw(), flying.getPitch());
                        Location lastLocation = playerData.getLocation() != null ? playerData.getLocation() : location;
                        playerData.setLastLastLastLocation(playerData.getLastLastLocation());
                        playerData.setLastLastLocation(playerData.getLastLocation());
                        playerData.setLastLocation(lastLocation);
                        playerData.setLocation(location);
                        callEvent = new FlyingEvent(playerData.getLocation().getX(), playerData.getLocation().getY(), playerData.getLocation().getZ(), flying.getYaw(), flying.getPitch(),
                                flying.isPosition(),
                                flying.isLook(),
                                onGround,
                                p.getWorld());
                    } else {
                        location = new Location(p.getWorld(), p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ(), flying.getYaw(), flying.getPitch());
                        Location lastLocation = playerData.getLocation() != null ? playerData.getLocation() : location;
                        playerData.setLastLastLastLocation(playerData.getLastLastLocation());
                        playerData.setLastLastLocation(playerData.getLastLocation());
                        playerData.setLastLocation(lastLocation);
                        playerData.setLocation(location);
                        callEvent = new FlyingEvent(p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ(), flying.getYaw(), flying.getPitch(),
                                flying.isPosition(),
                                flying.isLook(),
                                onGround,
                                p.getWorld());
                    }
                }
                if (!flying.isLook() && !flying.isPosition()) {
                    if (playerData.getLocation() != null) {
                        callEvent = new FlyingEvent(playerData.getLocation().getX(), playerData.getLocation().getY(), playerData.getLocation().getZ(), playerData.getLocation().getYaw(), playerData.getLocation().getPitch(),
                                flying.isPosition(),
                                flying.isLook(),
                                onGround,
                                p.getWorld());
                    }
                }

                if (flying.isPosition() && playerData.teleportReset) {
                    playerData.teleportReset = false;
                    playerData.setTeleportLocation(null);
                    playerData.lastServerPositionTick = 600;
                    playerData.lastTeleportReset = playerData.getTotalTicks();
                }

                if (flying.isPosition() && playerData.getTeleportLocation() != null) {
                    double xTP = Math.abs(playerData.getLocation().getX() - playerData.getTeleportLocation().getX());
                    double yTP = Math.abs(playerData.getLocation().getY() - playerData.getTeleportLocation().getY());
                    double zTP = Math.abs(playerData.getLocation().getZ() - playerData.getTeleportLocation().getZ());
                    if (xTP <= 0.005 || yTP <= 0.005 || zTP <= 0.005 && playerData.getTotalTicks() > 100) {
                        playerData.teleportReset = true;
                    }
                }

                playerData.setLastLastOnGroundPacket(playerData.isLastOnGroundPacket());
                playerData.setLastOnGroundPacket(playerData.isOnGroundPacket());
                playerData.setOnGroundPacket(onGround);
                playerData.setLastOnGround(playerData.isOnGround());
                playerData.setOnGround(UtilPlayer.isOnGroundBB(p));

                if (Karhu.getInstance().getFileManager().isTransaction()) {
                    if (playerData.getTransPing() > 40000) {
                        Tasker.run(() -> {
                            p.kickPlayer("Internal Exception : java.io.IOExpection : An existing Connection was forcibly closed by the remote host.");
                        });
                    }
                }

                if (lagging) playerData.setLastPacketDrop(playerData.getTotalTicks());


                if (p.isFlying()) {
                    playerData.setLastFlyTick(playerData.getTotalTicks());
                }

                Player target = playerData.getLastTarget();

                if (target != null && target.isOnline()) {

                    PlayerData targetData = Karhu.getInstance().getPlayerDataManager().getPlayerData(target);

                    if (targetData == null) return;

                    double x1 = (int) (targetData.getLocation().getX() * 32.0D);
                    double y1 = (int) (targetData.getLocation().getY() * 32.0D);
                    double z1 = (int) (targetData.getLocation().getZ() * 32.0D);

                    double x2 = x1 / 32.0D;
                    double y2 = y1 / 32.0D;
                    double z2 = z1 / 32.0D;

                    AxisAlignedBB box = getEntityBoundingBox(x2, y2, z2);
                    AxisAlignedBB box2 = getEntityBoundingBox(x2, y2, z2);

                    AxisAlignedBB axisalignedbb;
                    AxisAlignedBB axisalignedbb2;

                    if (playerData.getClientVersion().isHigherThan(ClientVersion.v_1_8)) {
                        axisalignedbb = box.expand(0.03, 0.03, 0.03);
                    } else if (playerData.lastPosition > 0) {
                        axisalignedbb = box.expand(0.13, 0.13, 0.13);
                    } else {
                        axisalignedbb = box.expand(0.1, 0.1, 0.1);
                    }

                    if (playerData.getClientVersion().isHigherThan(ClientVersion.v_1_8)) {
                        axisalignedbb2 = box2.expand(0.23, 0.23, 0.23);
                    } else if (playerData.lastPosition2 > 0) {
                        axisalignedbb2 = box.expand(0.33, 0.33, 0.33);
                    } else {
                        axisalignedbb2 = box2.expand(0.3, 0.3, 0.3);
                    }
                    if (!playerData.isTeleporting() && !p.isInsideVehicle() && !target.isInsideVehicle()) {
                        playerData.getPastLocs().add(new Pair<>(axisalignedbb, playerData.getTotalTicks()));
                        playerData.getPastLocations().add(new Pair<>(axisalignedbb, playerData.getTotalTicks()));
                        playerData.getPastLocsHitBox().add(new Pair<>(axisalignedbb2, playerData.getTotalTicks()));
                    } else {
                        playerData.getPastLocs().clear();
                        playerData.getPastLocations().clear();
                        playerData.getPastLocsHitBox().clear();
                    }
                }

                if (playerData.getLocation() != null && playerData.getLastLocation() != null) {

                    float disX = (float) ((float) playerData.getLocation().getX() - playerData.getLastLocation().getX());
                    float disZ = (float) ((float) playerData.getLocation().getZ() - playerData.getLastLocation().getZ());

                    float disXZ = disX * disX + disZ * disZ;
                    float deltaXZ = (float) Math.sqrt(disXZ);
                    float deltaXZ2 = (float) MathUtil.hypot(playerData.getLocation().getX() - playerData.getLastLocation().getX(), playerData.getLocation().getZ() - playerData.getLastLocation().getZ());

                    playerData.setDeltaX(disX);
                    playerData.setDeltaZ(disZ);

                    playerData.setDeltaX(disX);
                    playerData.setDeltaZ(disZ);

                    playerData.setLastDeltaXZ(playerData.getDeltaXZ());
                    playerData.setDeltaXZ(deltaXZ);
                    playerData.setDeltaXZ2(deltaXZ2);

                    playerData.setLastDeltaY(playerData.getDeltaY());
                    //double deltaMCP = playerData.getLocation().getY() - playerData.getLastLocation().getY() >= 0.005 ? playerData.getLocation().getY() - playerData.getLastLocation().getY() : 0.0;
                    playerData.setDeltaY((float) (playerData.getLocation().getY() - playerData.getLastLocation().getY()));
                }

                playerData.setPlacing(false);

                playerData.lastDroppedPackets = delayed ? nowTicks : playerData.lastDroppedPackets;
                playerData.setLastFlying(now);
                playerData.setLastFlyingTicks(nowTicks);
                playerData.setInventoryOpen(false);
            } else if (e.getPacketId() == PacketType.Client.KEEP_ALIVE) {
                playerData.setLastKeepAlive(System.currentTimeMillis());
                playerData.setLastPing(playerData.getPing());
                playerData.setPing((int) Math.abs(System.currentTimeMillis() - playerData.getLastServerKeepAlive()));
            } else if (e.getPacketId() == PacketType.Client.USE_ENTITY) {
                final WrappedPacketInUseEntity use = new WrappedPacketInUseEntity(e.getNMSPacket());
                if (use.getAction().equals(WrappedPacketInUseEntity.EntityUseAction.ATTACK)) {
                    callEvent = new AttackEvent(use.getEntityID(), use.getEntity());
                    if (!playerData.isAttackedSinceVelocity()) {
                        playerData.setVelocityX(playerData.getVelocityX() * 0.6);
                        playerData.setVelocityZ(playerData.getVelocityZ() * 0.6);
                        playerData.setAttackedSinceVelocity(true);
                    }
                    playerData.lastAttackTick = 0;
                    playerData.setLastAttackPacket(System.currentTimeMillis());
                    if (use.getEntity() instanceof Player) {
                        Player target = (Player) use.getEntity();
                        if (playerData.getLastTarget() != null) {
                            playerData.setLastLastTarget(playerData.getLastTarget());
                            if (use.getEntity() != playerData.getLastTarget()) {
                                playerData.getPastLocs().clear();
                                playerData.getPastLocations().clear();
                                playerData.getPastLocsHitBox().clear();
                            }
                        }
                        playerData.setLastTarget(target);
                        playerData.target.set(target);
                    } else {
                        playerData.setLastTarget(null);
                        playerData.target.set(null);
                    }
                } else if (use.getAction() == WrappedPacketInUseEntity.EntityUseAction.INTERACT || use.getAction() == WrappedPacketInUseEntity.EntityUseAction.INTERACT_AT) {
                    if (use.getEntity() instanceof LivingEntity) {
                        callEvent = new InteractEvent(use.getEntityID(), use.getEntity());
                    }
                }
            } else if (e.getPacketId() == PacketType.Client.ARM_ANIMATION) {
                callEvent = new SwingEvent();
            } else if (e.getPacketId() == PacketType.Client.WINDOW_CLICK) {
                callEvent = new WindowEvent();
            } else if (e.getPacketId() == PacketType.Client.CLIENT_COMMAND) {
                final WrappedPacketInClientCommand clientCommand = new WrappedPacketInClientCommand(e.getNMSPacket());
                callEvent = new ClientCommandEvent(clientCommand.getClientCommand());
                if (clientCommand.getClientCommand().equals(WrappedPacketInClientCommand.ClientCommand.OPEN_INVENTORY_ACHIEVEMENT)) {
                    playerData.setInventoryOpen(true);
                    playerData.setInvStamp(playerData.getTotalTicks());
                }
            } else if (e.getPacketId() == PacketType.Client.BLOCK_DIG) {
                final WrappedPacketInBlockDig blockDig = new WrappedPacketInBlockDig(e.getNMSPacket());
                if (blockDig.getDigType() == WrappedPacketInBlockDig.PlayerDigType.START_DESTROY_BLOCK) {
                    playerData.setDigging(true);
                    playerData.setLastDig(playerData.getTotalTicks());
                } else if (blockDig.getDigType() == WrappedPacketInBlockDig.PlayerDigType.ABORT_DESTROY_BLOCK || blockDig.getDigType() == WrappedPacketInBlockDig.PlayerDigType.STOP_DESTROY_BLOCK) {
                    playerData.setDigging(false);
                }
                callEvent = new DigEvent(new Vector(blockDig.getX(), blockDig.getY(), blockDig.getZ()), blockDig.getDigType());
            } else if (e.getPacketId() == PacketType.Client.ENTITY_ACTION) {
                final WrappedPacketInEntityAction action = new WrappedPacketInEntityAction(e.getNMSPacket());

                switch (action.getAction()) {
                    case START_SPRINTING:
                        playerData.setSprinting(true);
                        break;
                    case STOP_SPRINTING:
                        playerData.setSprinting(false);
                        break;
                    case START_SNEAKING:
                        playerData.setSneaking(true);
                        break;
                    case STOP_SNEAKING:
                        playerData.setSneaking(false);
                        break;
                }

            } else if ((e.getPacketId() == PacketType.Client.BLOCK_PLACE && Karhu.SERVER_VERSION.isLowerThan(ServerVersion.v_1_9)) || (e.getPacketId() == PacketType.Client.USE_ITEM && Karhu.SERVER_VERSION.isHigherThan(ServerVersion.v_1_8_8))) {
                final WrappedPacketInBlockPlace packet = new WrappedPacketInBlockPlace(e.getNMSPacket());
                final ItemStack stack = VersionBridgeHelper.getStackInHand(playerData);
                playerData.setPlacing(true);
                callEvent = new BlockPlaceEvent(new Vector(packet.getX(), packet.getY(), packet.getZ()), stack);
            } else if (e.getPacketId() == PacketType.Client.HELD_ITEM_SLOT) {
                final WrappedPacketInHeldItemSlot packet = new WrappedPacketInHeldItemSlot(e.getNMSPacket());
                callEvent = new HeldItemSlotEvent(packet.getCurrentSelectedSlot());
            } else if (e.getPacketId() == PacketType.Client.ABILITIES) {
                callEvent = new AbilityEvent();
            } else if (e.getPacketId() == PacketType.Client.STEER_VEHICLE) {
                playerData.setLastVehicle(System.currentTimeMillis());
            } else if (e.getPacketId() == PacketType.Client.TRANSACTION) {
                final WrappedPacketInTransaction packet = new WrappedPacketInTransaction(e.getNMSPacket());
                final long now = System.currentTimeMillis();
                if (playerData.getVelocityIds().containsKey(packet.getActionNumber())) {
                    //Vector
                    Vector velocity = playerData.getVelocityIds().get(packet.getActionNumber());

                    //Ticks
                    playerData.setVelocityH((int) (((velocity.getX() + velocity.getZ()) / 2.0D + 2.0D) * 15.0D));
                    playerData.setVelocityV((int) (Math.pow(velocity.getY() + 2.0D, 2.0D) * 5.0D));

                    //Velocities
                    playerData.setLastVelocityX(playerData.getVelocityX());
                    playerData.setLastVelocityY(playerData.getVelocityY());
                    playerData.setLastVelocityZ(playerData.getVelocityZ());

                    playerData.setVelocityX(velocity.getX());
                    playerData.setVelocityY(velocity.getY());
                    playerData.setVelocityZ(velocity.getZ());

                    playerData.setVelocityTicks(playerData.getTotalTicks());

                    playerData.setVelocityHorizontal(Math.hypot(velocity.getX(), velocity.getZ()));

                    //Remove processed id to prevent weird stuff / mem leaks
                    playerData.getVelocityIds().remove(packet.getActionNumber());
                } else if (playerData.getTransactionTime().containsKey(packet.getActionNumber())) {

                    //Map
                    long transactionStamp = playerData.getTransactionTime().get(packet.getActionNumber());
                    playerData.setTimerTransactionReceived(playerData.getTimerTransactionReceived() + 1);

                    //Ping
                    playerData.setTransPing(now - transactionStamp);

                    //Remove processed id to prevent weird stuff / mem leaks
                    playerData.getTransactionSentMap().remove(packet.getActionNumber());
                }
            } else if (e.getPacketId() == PacketType.Client.CUSTOM_PAYLOAD) {

                final WrappedPacketInCustomPayload packet = new WrappedPacketInCustomPayload(e.getNMSPacket());

                if (packet.getTag() != null) {

                    if (packet.getTag().equals("MC|Brand")) {
                        String brand = new String(packet.getData());
                        playerData.setBrand(brand);

                    }

                }
            }

            PacketEvent finalCallEvent = callEvent;
            if (playerData.getCheckManager().getChecks() != null) {
                for (Check check : playerData.getCheckManager().getChecks()) {
                    if (EnabledUtil.checkIfIsEnabled(check.getName().replace(" ", ""))) {
                        check.handle(finalCallEvent, p);
                    }
                }
            }
        }
    }

    @PacketHandler
    public void onPacketSend(PacketSendEvent e) {
        final Player p = e.getPlayer();
        PlayerData playerData = null;

        if (p != null) {
            playerData = Karhu.getInstance().getPlayerDataManager().getPlayerData(e.getPlayer());
        }

        if (playerData != null && playerData.isObjectLoaded()) {
            if (e.getPacketId() == PacketType.Server.KEEP_ALIVE) {
                playerData.setLastServerKeepAlive(e.getTimestamp());
            } else if (e.getPacketId() == PacketType.Server.POSITION) {
                final WrappedPacketOutPosition packet = new WrappedPacketOutPosition(e.getNMSPacket());
                if (playerData.getCheckManager().getChecks() != null) {
                    for (Check check : playerData.getCheckManager().getChecks()) {
                        if (EnabledUtil.checkIfIsEnabled(check.getName().replace(" ", ""))) {
                            check.handle(new PositionEvent(-1, -1, -1, -1, -1), e.getPlayer());
                        }
                    }
                }
                playerData.lastServerPositionTick = 0;
                playerData.setTeleportLocation(new CustomLocation(packet.getX(), packet.getY(), packet.getZ(), 0F, 0F));
                playerData.setLastTeleport(System.currentTimeMillis());
            } else if (e.getPacketId() == PacketType.Server.OPEN_WINDOW) {
                playerData.setInventoryOpen(true);
                playerData.setInvStamp(playerData.getTotalTicks());
            } else if (e.getPacketId() == PacketType.Server.ENTITY_VELOCITY) {
                final WrappedPacketOutEntityVelocity velocity = new WrappedPacketOutEntityVelocity(e.getNMSPacket());
                if (velocity.getEntityId() == p.getEntityId()) {
                    playerData.setVelocityBeingConfirmed(true);

                    RandomShort random = new RandomShort();
                    short randomID = random.nextShort();
                    playerData.getVelocityIds().put((short) -(randomID), new Vector(velocity.getVelocityX(), velocity.getVelocityY(), velocity.getVelocityZ()));

                    Tasker.run(() -> {
                        PacketEvents.getAPI().getPlayerUtils().sendPacket(e.getPlayer(), new WrappedPacketOutTransaction(0, (short) -(randomID), false));
                    });
                }
            } else if (e.getPacketId() == PacketType.Server.TRANSACTION) {

                final WrappedPacketOutTransaction transaction = new WrappedPacketOutTransaction(e.getNMSPacket());

                playerData.getTransactionSentMap().put(transaction.getActionNumber(), System.currentTimeMillis());

            }
        }
    }

    public static AxisAlignedBB getEntityBoundingBox(double x, double y, double z) {
        float f = 0.6F / 2.0F;
        float f1 = 1.8F;
        return (new AxisAlignedBB(x - (double)f, y, z - (double)f, x + (double)f, y + (double)f1, z + (double)f));
    }
}

