package me.liwk.karhu.event;

import io.github.retrooper.packetevents.event.PacketEvent;

public class Event extends PacketEvent {
    public boolean isAsyncByDefault() {
        return false;
    }
}
