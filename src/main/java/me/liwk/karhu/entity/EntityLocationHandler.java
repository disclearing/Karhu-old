package me.liwk.karhu.entity;

import me.liwk.karhu.data.PlayerData;
import me.liwk.karhu.event.FlyingEvent;

public class EntityLocationHandler {
    public static void updateFlyingLocations(PlayerData data, FlyingEvent flying) {
        if(flying.hasMoved()) {
            data.lastPosition = 0;
            data.attackerX = flying.getX();
            data.attackerY = flying.getY();
            data.attackerZ = flying.getZ();
        }

        if(!flying.hasMoved()) {
            data.lastPosition++;
        }

        if(flying.hasLooked()) {
            data.attackerYaw = flying.getYaw();
            data.attackerPitch = flying.getPitch();
        }
    }

    public static void updateFlyingLocations2(PlayerData data, FlyingEvent flying) {
        if(flying.hasMoved()) {
            data.lastPosition2 = 0;
            data.attackerX2 = flying.getX();
            data.attackerY2 = flying.getY();
            data.attackerZ2 = flying.getZ();
        }

        if(!flying.hasMoved()) {
            data.lastPosition2++;
        }

        if(flying.hasLooked()) {
            data.attackerYaw2 = flying.getYaw();
            data.attackerPitch2 = flying.getPitch();
        }
    }
}
