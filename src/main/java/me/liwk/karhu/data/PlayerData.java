package me.liwk.karhu.data;

import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.utils.player.ClientVersion;
import lombok.Getter;
import lombok.Setter;
import me.liwk.karhu.check.api.Check;
import me.liwk.karhu.check.api.manager.CheckManager;
import me.liwk.karhu.util.Observable;
import me.liwk.karhu.util.*;
import me.liwk.karhu.util.evictinglist.EvictingList;
import me.liwk.karhu.util.pair.Pair;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Setter
@Getter
public class PlayerData {

    public static Map<Class<? extends Check>, Constructor<? extends Check>> CONSTRUCTORS;
    private final CheckManager checkManager = new CheckManager(this);
    public final LinkedList<Integer> recentCounts = new LinkedList<>();
    public boolean attackedSinceVelocity;
    /*public static Class[] CHECKS1_8;
    public static Class[] CHECKS1_9;
    public static Class[] CHECKS1_13;*/
    public Map<Short, Long> transactionTime = new HashMap<>();
    public short timerTransactionSent;
    public int timerTransactionReceived;
    public boolean receivedTransaction;
    public int transactionStreak;
    public int entityId;
    private Map<UUID, List<CustomLocation>> recentPlayerPackets;
    private Map<Check, Set<Long>> checkViolationTimes;
    private Map<Class<? extends Check>, Check> checkMap;
    private Map<Integer, Long> keepAliveTimes;
    private Map<Check, Double> checkVlMap;

    private CustomLocation lastCustomLocation;
    private CustomLocation lastMovePacket;
    private long lastMovement, lastMovePkt;
    private int lastFlyTick;
    private Set<CustomLocation> teleportLocations;

    public Location location, lastLocation, lastLastLocation, lastLastLastLocation;

    private boolean verifyingSensitivity;

    private double sensitivity;

    public double reachX, reachY, reachZ;

    public float deltaXZ, deltaXZ2, lastDeltaXZ, lastDeltaY, deltaY;
    public float deltaX, deltaZ, lastDeltaX, lastDeltaZ;

    private int lastOpeningInteract;

    private LocationData locationData, previousLocation;
    private final EvictingList<Pair<AxisAlignedBB, Integer>> pastLocs = new EvictingList<>(20);
    private final EvictingList<Pair<AxisAlignedBB, Integer>> pastLocations = new EvictingList<>(30);
    private final EvictingList<Pair<AxisAlignedBB, Integer>> pastLocsHitBox = new EvictingList<>(20);
    private final List<LocationData> playerLocations = new ArrayList<>();

    private Player lastTarget, lastLastTarget;
    public final Observable<Player> target = new Observable<>(null);
    private int lastAttackedId;
    private long lastTeleport, lastServerTeleport;
    private CustomLocation teleportLocation;
    public int totalTicks = 0, positionTicks;

    private final Map<Short, Long> transactionSentMap = new HashMap<>();
    private final Map<Short, Vector> relmoveSentMap = new HashMap<>();

    private double relmoveX, relmoveY, relmoveZ;

    private HashMap<Short, Vector> velocityIds = new HashMap<>();

    private boolean velocityBeingConfirmed, relMoveBeingConfirmed, transactionBeingConfirmed;
    private long lastDelayed;
    private boolean allowTeleport;
    private boolean inventoryOpen;
    private int invStamp;
    public long lastJoinTime, lastFlying, lastDelayedPacket, lastVehicle;
    public int lastFlyingTicks, lastVehicleTicks;
    public boolean droppedPackets;
    private int lastPacketDrop;
    public boolean underBlock;
    private boolean sprinting;
    private boolean sneaking;
    public boolean inLiquid;
    private boolean instantBreakDigging;
    private boolean fakeDigging;
    private boolean onGround;
    private boolean onGroundPacket, lastOnGroundPacket, lastLastOnGroundPacket;
    private boolean onStairs;
    private boolean onCarpet;
    private boolean OnSlab;
    public boolean onSlime;
    private boolean placing, digging;
    private int lastDig;
    private boolean banned;
    private boolean exempt;
    private boolean inWeb;
    private boolean onIce;
    private boolean belowBlock;
    private boolean onLadder;
    private boolean wasUnderBlock, wasOnGround, wasInLiquid, wasInWeb, wasBelowBlock, wasOnLadder;
    private double lastGroundY;
    //private int velocityId, relMoveId, transactionId;
    private short velocityId;
    private double velocityX, lastVelocityX;
    private double velocityY, lastVelocityY;
    private double velocityZ, lastVelocityZ;
    private double velocityHorizontal;
    private double moveSpeed;
    private double newMoveSpeed;
    private double lastVelocityFlyY;
    private long lastDelayedMovePacket;
    private long lastAttackPacket;
    private long lastDamage;
    private long lastCancelledDamage;
    private long lastVelocity, lastGlide, lastRiptide;
    public long lastWindowClick;
    //private Deque<Long> samplesWindowClick = Lists.newLinkedList();
    private boolean gliding = false, riptiding = false;

    private long transPing, lastTransPing, averageTransPing;
    private long ping, lastPing;
    private long lastDead;
    private int lastBlockPlaceCancelled;
    private long lastFullblockMoved;
    private long lastHillJump;
    private long lastAimTime;
    private long interact;
    private int velocityH, velocityV;
    private int lastCps;
    private int movementsSinceIce;
    private int movementsSinceUnderBlock;
    private int aboveBlockTicks;
    public int airTicks, clientAirTicks;
    public int climbableTicks;
    public int iceTicks, halfTicks, skullTicks, potTicks, wallTicks;
    public int groundTicks;
    public int slimeTicks;
    public double slimeHeight, fallDistance, fallY;
    public long lastOnSlime;
    public int blockTicks;
    public int velocityTicks;
    public int liquidTicks;
    public int lastAttackTick;
    public Vec3 eyeLocation;
    private Vec3 look, lookMouseDelayFix;
    public int ticksSinceHit;
    public double lastGravity;
    public boolean isNearWater;
    public long lastLag, lastLag2;
    public int lastDroppedPackets;
    public boolean lagTick;
    public long lastKeepAlive, lastServerKeepAlive;
    public int lastServerPositionTick, lastTeleportReset;
    public boolean teleportReset;

    public double TimerABalance, TimerBCount, TimerCBalance;

    public boolean lastOnGround, lastLastOnGround;

    public long TimerALastPacket, TimerBLastPacket;

    public double lastSpeed, lastDist;

    public long lastFlag, lastFlag2;
    public int clickerCClicks, clickerCOutliers, clickerCFlyingCount;
    public boolean clickerCRelease;

    public boolean cinematic;

    public long lastCinematic;

    public Long lastUseEntity;


    public int lastPosition;
    public double attackerX, attackerY, attackerZ;
    public float attackerYaw, attackerPitch;

    public int lastPosition2;
    public double attackerX2, attackerY2, attackerZ2;
    public float attackerYaw2, attackerPitch2;

    public UUID playerUUID;

    private long lastFast;

    private ClientVersion clientVersion;

    private String brand = "vanilla";

    private Player dataPlayer;

    private boolean objectLoaded = false;

    public PlayerData(Player player, Plugin karhu) {
        this.recentPlayerPackets = new HashMap<>();
        this.checkViolationTimes = new HashMap<>();
        this.checkMap = new HashMap<>();
        this.keepAliveTimes = new HashMap<>();
        this.checkVlMap = new HashMap<>();
        this.teleportLocations = Collections.newSetFromMap(new ConcurrentHashMap<>());
        this.banned = false;
        this.clientVersion = PacketEvents.getAPI().getPlayerUtils().getClientVersion(player);
        this.dataPlayer = player;
        this.location = player.getLocation();
        this.lastLocation = this.location;
        this.lastLastLocation = this.lastLocation;
        this.lastJoinTime = System.currentTimeMillis();
        this.entityId = player.getEntityId();
        this.objectLoaded = true;
    }

    private String dogshit11(boolean value) {
        return value ? "%%__TIMESTAMP__%%" : "%%__USER__%%";
    }

    @SuppressWarnings("unchecked")
    public <T extends Check> T getCheck(Class<T> clazz) {
        return (T) this.checkMap.get(clazz);
    }

    public CustomLocation getLastPlayerPacket(UUID playerUUID, int index) {
        List<CustomLocation> customLocations = this.recentPlayerPackets.get(playerUUID);
        if (customLocations != null && customLocations.size() > index) {
            return customLocations.get(customLocations.size() - index);
        }
        return null;
    }
    private String dogshit40(boolean value) {
        return value ? "%%__TIMESTAMP__%%" : "%%__USER__%%";
    }

    public void addPlayerPacket(UUID playerUUID, CustomLocation customLocation) {
        List<CustomLocation> customLocations = this.recentPlayerPackets.get(playerUUID);
        if (customLocations == null) {
            customLocations = new ArrayList<>();
        }
        if (customLocations.size() == 20) {
            customLocations.remove(0);
        }
        customLocations.add(customLocation);
        this.recentPlayerPackets.put(playerUUID, customLocations);
    }

    public boolean isTeleporting() {
        return teleportLocation != null;
    }

    public double getCheckVl(Check check) {
        if (!this.checkVlMap.containsKey(check)) {
            this.checkVlMap.put(check, 0.0);
        }
        return this.checkVlMap.get(check);
    }

    public void setCheckVl(double vl, Check check) {
        if (vl < 0.0) {
            vl = 0.0;
        }
        this.checkVlMap.put(check, vl);
    }


    private boolean hasFast(long timestamp) {
        return lastFlying != 0L && lastFast != 0L && timestamp - lastFast < 100L;
    }


    public boolean isLagging(long currentTime, long length) {
        return currentTime - lastLag < length;
    }


    public boolean isLagging2(long currentTime, long length) {
        return currentTime - lastLag2 < length;
    }

    public boolean hasFast() {
        return hasFast(lastFlying);
    }

    public int getViolations(Check check, Long time) {
        Set<Long> timestamps = this.checkViolationTimes.get(check);
        if (timestamps != null) {
            return (int) timestamps.stream().filter(timestamp -> System.currentTimeMillis() - timestamp <= time).count();
        }
        return 0;
    }

    public void addViolation(Check check) {
        Set<Long> timestamps = this.checkViolationTimes.get(check);
        if (timestamps == null) {
            timestamps = new HashSet<>();
        }
        timestamps.add(System.currentTimeMillis());
        this.checkViolationTimes.put(check, timestamps);
    }
}