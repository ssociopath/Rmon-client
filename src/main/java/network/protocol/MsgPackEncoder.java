package network.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.msgpack.MessagePack;

/**
 * @author bobo
 * @date 2021/6/26
 */

public class MsgPackEncoder extends MessageToByteEncoder<Object> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object msg, ByteBuf out) throws Exception {
        MessagePack msgPack = new MessagePack();
        byte[] raw = msgPack.write(msg);
        out.writeBytes(raw);
    }
}
