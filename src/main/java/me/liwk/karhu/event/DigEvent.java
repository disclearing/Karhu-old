package me.liwk.karhu.event;

import io.github.retrooper.packetevents.packetwrappers.in.blockdig.WrappedPacketInBlockDig;
import lombok.Getter;
import org.bukkit.util.Vector;


@Getter
public class DigEvent extends Event {

    private final Vector blockPos;
    //private final Direction direction;
    private final WrappedPacketInBlockDig.PlayerDigType digType;

    public DigEvent(Vector blockPos, WrappedPacketInBlockDig.PlayerDigType digType) {
        this.blockPos = blockPos;
        //this.direction = direction;
        this.digType = digType;
    }

}

