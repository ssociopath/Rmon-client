import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import protocol.MsgPack;
import protocol.MsgPackDecoder;
import protocol.MsgPackEncoder;
import utils.Constant;
import utils.ScreenUtil;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * @author bobo
 * @date 2021/6/25
 */

public class SocketClient {

    private Channel channel;
    private Bootstrap bootstrap;
    private static int pkgId=0;

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

    protected void doConnect() {
        if (channel != null && channel.isActive()) {
            return;
        }

        bootstrap.connect("127.0.0.1", 5555).addListener(future -> {
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
                channel.writeAndFlush(new MsgPack(type,pkgId,Constant.MF, buffer));
                index+=max;
            }
            byte[] buffer = Arrays.copyOfRange(content, index, size);
            channel.writeAndFlush(new MsgPack(type,pkgId,Constant.DF, buffer));
        }else{
            channel.writeAndFlush(new MsgPack(type,pkgId,Constant.DF, content));
        }
        pkgId++;
        if (type==Constant.LOGOUT){
            System.out.println("客户端关闭连接");
            channel.close();
        }
    }

    static class ClientHandler extends ChannelInboundHandlerAdapter {
        private SocketClient client;

        public ClientHandler(SocketClient socketClient) {
            client = socketClient;
        }

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            if (evt instanceof IdleStateEvent) {
                IdleState state = ((IdleStateEvent) evt).state();
                if (state == IdleState.WRITER_IDLE) {
                    client.sendData(Constant.HEART,"心跳连接".getBytes(StandardCharsets.UTF_8));
                }
            } else {
                super.userEventTriggered(ctx, evt);
            }
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            MsgPack msgPack = (MsgPack)msg;
            if(Constant.IMAGE == msgPack.getType()){
                for (int i = 0; i < 500; i++) {
                    client.sendData(Constant.IMAGE,ScreenUtil.getDesktopScreen());
                }
            }
            System.out.println("服务端响应："+new String(msgPack.getContent(), StandardCharsets.UTF_8));
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            super.channelInactive(ctx);
            client.doConnect();
        }
    }

    public static void main(String[] args){
        SocketClient socketClient = new SocketClient();
        socketClient.connect();
    }
}
