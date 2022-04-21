package network;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.Data;
import network.vo.RequestPacket;
import network.protocol.ClientHandler;
import network.protocol.MsgPackDecoder;
import network.protocol.MsgPackEncoder;
import utils.Constant;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * @author bobo
 * @date 2021/6/25
 */

@Data
public class SocketClient {
    public Channel channel;
    public Bootstrap bootstrap;
    public int pkgId=0;

    public ILoginListener iLoginListener;
    public IRuleListener iRuleListener;

    public void connect(){
        NioEventLoopGroup workGroup = new NioEventLoopGroup(4);
        bootstrap = new Bootstrap();
        bootstrap.group(workGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new IdleStateHandler(0, 5, 0))
                                //用LengthFieldPrepender给数据添加报文头Length字段，接受方使用LengthFieldBasedFrameDecoder进行解码
                                .addLast(new LengthFieldBasedFrameDecoder(65535, 0,4,0,4))
                                .addLast(new MsgPackDecoder())
                                //在自定义消息类型前加上4字节长度消息头
                                .addLast(new LengthFieldPrepender(4))
                                .addLast(new MsgPackEncoder())
                                .addLast(new ClientHandler(SocketClient.this));
                    }
                });
        doConnect();
    }

    public void doConnect() {
        if (channel != null && channel.isActive()) {
            return;
        }

        bootstrap.connect("bobooi.com", 5555).addListener(future -> {
            if (future.isSuccess()) {
                channel = ((ChannelFuture) future).channel();
                System.out.println("连接成功!");
            } else {
                System.err.println("连接失败!");
                ((ChannelFuture) future).channel().eventLoop().schedule(this::doConnect, 2, TimeUnit.SECONDS);
            }
        });
    }

    public void sendData(byte type, byte[] content){
        int size=content.length,index=0,max=Constant.MAX_PKG_BYTE_LENGTH;
        if(size>max){
            while(index+max<size){
                byte[] buffer = Arrays.copyOfRange(content, index, index + max);
                channel.writeAndFlush(new RequestPacket(type,pkgId,Constant.MF, buffer));
                index+=max;
            }
            byte[] buffer = Arrays.copyOfRange(content, index, size);
            channel.writeAndFlush(new RequestPacket(type,pkgId,Constant.DF, buffer));
        }else{
            channel.writeAndFlush(new RequestPacket(type,pkgId,Constant.DF, content));
        }
        pkgId++;
        if (type==Constant.LOGOUT){
            System.out.println("客户端关闭连接");
            channel.close();
        }
    }

}