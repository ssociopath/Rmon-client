

import com.sun.security.ntlm.Client;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * @author bobo
 * @date 2021/6/25
 */

public class SocketClient {
    private final NioEventLoopGroup workGroup = new NioEventLoopGroup(4);
    private Channel channel;
    private Bootstrap bootstrap;

    public void connect() throws IOException {
        bootstrap = new Bootstrap();
        bootstrap.group(workGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new IdleStateHandler(0, 5, 0));
                        ch.pipeline().addLast(new FirstClientHandler());
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
                //重新连接
                ((ChannelFuture) future).channel().eventLoop().schedule(() -> doConnect(), 2, TimeUnit.SECONDS);
            }
        });
    }

    public void sendData() throws Exception {
        Scanner in = new Scanner(System.in);
        while (true){
            String cmd = in.nextLine();
            switch (cmd){
                case "close" :
                    System.out.println("客户端关闭连接");
                    channel.close();
                    break;
                default:
                    ByteBuf buffer = channel.alloc().buffer();
                    byte[] bytes = ("客户端:"+cmd).getBytes(StandardCharsets.UTF_8);
                    buffer.writeBytes(bytes);
                    channel.writeAndFlush(buffer);
                    break;
            }
        }
    }

    static class FirstClientHandler extends ChannelInboundHandlerAdapter {


        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            if (evt instanceof IdleStateEvent) {
                IdleState state = ((IdleStateEvent) evt).state();
                if (state == IdleState.WRITER_IDLE) {
                    ctx.writeAndFlush(Unpooled.unreleasableBuffer(
                            Unpooled.copiedBuffer("heart", StandardCharsets.UTF_8)).duplicate());
                }
            } else {
                super.userEventTriggered(ctx, evt);
            }
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            ByteBuf byteBuf = (ByteBuf) msg;
            //接收服务端的消息并打印
            System.out.println(byteBuf.toString(StandardCharsets.UTF_8));
        }
    }

    public static void main(String[] args) throws Exception {
        SocketClient socketClient = new SocketClient();
        socketClient.connect();
        socketClient.sendData();
    }
}
