package me.liwk.karhu.check.impl.phase;

public class PhaseA /*extends Check*/ {

    /*private int pullbackTicks;
    private double buffer;

    public PhaseA(final PlayerData playerData) {
        super(Category.MOVEMENT, playerData);
    }

    @Override
    public void handle(PacketEvent event, Player player) {
        if (event instanceof FlyingEvent) {

            if (((FlyingEvent) event).hasLooked() || ((FlyingEvent) event).hasMoved()) {
                Location to = ((FlyingEvent) event).toLocation();
                Location from = playerData.getLastLocation();

                if (playerData.getDeltaY() < -0.0979D && playerData.getDeltaY() > -0.0981D && playerData.getDeltaXZ() < 0.5D && (player.getGameMode().equals(GameMode.SURVIVAL) || player.getGameMode().equals(GameMode.ADVENTURE)) && playerData.getTotalTicks() - pullbackTicks < 2 + MathUtil.getPingInTicks(playerData.getTransPing())) return;

                if(isInBlock(to.clone().subtract(0.175, 0, 0.175)) && !UtilPlayer.isNearPiston(player.getLocation()) && !UtilPlayer.isNearTrapdoor(player.getLocation())) {
                    if(playerData.getTotalTicks() - playerData.getLastOpeningInteract() < 15) return;
                    //player.teleport(new Location(player.getWorld(), from.getX() - 0.55, from.getY(), from.getZ() - 0.35, playerData.getLastLastLocation().getPitch(), playerData.getLastLastLocation().getYaw()));
                    this.pullbackTicks = playerData.getTotalTicks();
                    if(++buffer > 6) {
                        handleFlag(player, "Phase A §4^" , "§f* Attempting to go trough blocks\n§f* dXZ=§b" + playerData.getDeltaXZ() + "\n§f* dY=§b" + playerData.getDeltaY(), getBanVL("PhaseA"), 30000L);
                    }
                } else {
                    buffer = Math.max(buffer - 0.5, 0);
                }
            }
        }
    }
    public static boolean isInBlock(Location location) {
        if (location.getWorld().isChunkLoaded(location.getBlockX() >> 4, location.getBlockZ() >> 4)) {
            return true;
        }
        Block block = location.getWorld().getBlockAt(location.getBlockX(), location.getBlockY() + 1, location.getBlockZ());
        return block.getType().isSolid() && !block.getType().toString().contains("SIGN") && !block.getType().toString().contains("TRAPDOOR") && !block.getType().toString().contains("DOOR") && !block.getType().toString().contains("VINE") && !block.getType().toString().contains("LADDER") && !block.getType().toString().contains("OBSIDIAN");
    }*/
}
