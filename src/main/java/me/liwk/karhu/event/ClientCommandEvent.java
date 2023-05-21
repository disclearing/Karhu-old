package me.liwk.karhu.event;

import io.github.retrooper.packetevents.packetwrappers.in.clientcommand.WrappedPacketInClientCommand;
import lombok.Getter;

@Getter
public class ClientCommandEvent extends Event {

    private final WrappedPacketInClientCommand.ClientCommand clientCommand;

    public ClientCommandEvent(WrappedPacketInClientCommand.ClientCommand clientCommand) {
        this.clientCommand = clientCommand;
    }

}
