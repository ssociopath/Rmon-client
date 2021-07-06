package network.protocol;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import network.SocketClient;

import network.vo.ResponsePacket;
import network.vo.WsMessage;
import utils.*;

import java.nio.charset.StandardCharsets;


/**
 * @author bobo
 * @date 2021/6/29
 */

public class ClientHandler extends ChannelInboundHandlerAdapter {
    private final SocketClient client;
    private volatile boolean isSend;
    private volatile int size = Constant.DEFAULT_SIZE;


    public ClientHandler(SocketClient socketClient) {
        isSend = true;
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
        ResponsePacket requestPacket = (ResponsePacket)msg;
        byte type = requestPacket.getType();
        byte result =  requestPacket.getResult();
        String content = new String(requestPacket.getContent(),StandardCharsets.UTF_8);
        System.out.println("服务端响应："+content);

        switch (type){
            case Constant.LOGIN:{
                client.getILoginListener().onLogin(result,content);
                break;
            }
            case Constant.LOGOUT:{
                isSend = false;
                break;
            }
            case Constant.IMAGE:{
                WsMessage wsMessage = JsonUtil.parseObject(content, WsMessage.class);
                isSend = true;
                ThreadPoolUtil.executor(()->{
                    while (isSend){
                        wsMessage.setContent(new String(ScreenUtil.getDesktopScreen(size), StandardCharsets.ISO_8859_1));
                        client.sendData(Constant.IMAGE, JsonUtil.toJsonString(wsMessage).getBytes(StandardCharsets.UTF_8));
                        wsMessage.setContent(JsonUtil.toJsonString(SystemUtil.getAllTasks()));
                        client.sendData(Constant.TASK, JsonUtil.toJsonString(wsMessage).getBytes(StandardCharsets.UTF_8));
                    }
                });
                break;
            }
            case Constant.CMD: {
                WsMessage wsMessage = JsonUtil.parseObject(content, WsMessage.class);
                SystemUtil.exeCmd(wsMessage.getContent());
                break;
            }
            case Constant.RES_UPDATE:{
                WsMessage wsMessage = JsonUtil.parseObject(content, WsMessage.class);
                size = Integer.parseInt(wsMessage.getContent())*16/9;
                break;
            }
            case Constant.DATA_UPDATE:
            case Constant.DATA_DELETE:
                client.getIRuleListener().onChange(result,content);
                break;
            default: break;
        }


    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        client.getILoginListener().onFail();
        client.doConnect();
    }
}
