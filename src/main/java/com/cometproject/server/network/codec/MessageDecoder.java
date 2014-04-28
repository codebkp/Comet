package com.cometproject.server.network.codec;

import com.cometproject.server.network.messages.types.Event;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

public class MessageDecoder extends FrameDecoder {
    @Override
    public Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer) throws Exception {
        try {
            if (buffer.readableBytes() < 6) {
                return null;
            }

            buffer.markReaderIndex();
            int length = buffer.readInt();

            if (!(buffer.readableBytes() >= length)) {
                buffer.resetReaderIndex();
                return null;
            }

            length -= 2;

            return new Event(buffer.readShort(), buffer.readBytes(length));
        } catch (Exception e) {
            e.printStackTrace(); // for debugging!
        }

        return null;
    }
}
