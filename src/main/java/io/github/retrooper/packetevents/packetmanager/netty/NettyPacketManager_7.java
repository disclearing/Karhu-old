/*
 * MIT License
 *
 * Copyright (c) 2020 retrooper
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.github.retrooper.packetevents.packetmanager.netty;

import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.utils.nms.NMSUtils;
import net.minecraft.util.io.netty.channel.Channel;
import net.minecraft.util.io.netty.channel.ChannelDuplexHandler;
import net.minecraft.util.io.netty.channel.ChannelHandlerContext;
import net.minecraft.util.io.netty.channel.ChannelPipeline;
import net.minecraft.util.io.netty.channel.ChannelPromise;
import org.bukkit.entity.Player;

final class NettyPacketManager_7 {
    NettyPacketManager_7() {

    }
    /**
     * Inject a player using 1.7.10's netty import location
     * @param player
     */
    public void injectPlayer(final Player player) {
        final ChannelDuplexHandler channelDuplexHandler = new ChannelDuplexHandler() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                Object packet = PacketEvents.getAPI().packetManager.read(player, ctx.channel(), msg);
                if (packet == null) {
                    return;
                }
                super.channelRead(ctx, msg);
                PacketEvents.getAPI().packetManager.postRead(player, packet);
            }

            @Override
            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                Object packet = PacketEvents.getAPI().packetManager.write(player, ctx.channel(),msg);
                if (packet == null) {
                    return;
                }
                super.write(ctx, msg, promise);
                PacketEvents.getAPI().packetManager.postWrite(player, packet);
            }
        };
        final ChannelPipeline pipeline = ((Channel) NMSUtils.getChannel(player)).pipeline();
        pipeline.addBefore("packet_handler", PacketEvents.getAPI().packetManager.getNettyHandlerName(), channelDuplexHandler);
    }

    public void ejectPlayer(Player player) {
        Channel channel = (Channel) NMSUtils.getChannel(player);
        assert channel != null;
        ejectChannel(channel);
    }

   public void ejectChannel(Object ch) {
        Channel channel = (Channel) ch;
        channel.pipeline().remove(PacketEvents.getAPI().packetManager.getNettyHandlerName());
   }

    public void sendPacket(Object rawChannel, Object packet) {
        Channel channel = (Channel) rawChannel;
        channel.pipeline().writeAndFlush(packet);
    }
}