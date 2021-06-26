package protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.msgpack.MessagePack;

import java.util.List;

/**
 * @author bobo
 * @date 2021/6/26
 */

public class MsgPackDecoder extends MessageToMessageDecoder<ByteBuf> {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        final byte[] array;
        final int length=msg.readableBytes();
        array=new byte[length];
        msg.getBytes(msg.readerIndex(), array,0,length);
        MessagePack msgpack=new MessagePack();
        out.add(msgpack.read(array, MsgPack.class));
    }
}
