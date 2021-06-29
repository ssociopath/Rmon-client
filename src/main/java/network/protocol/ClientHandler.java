package network.protocol;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import network.SocketClient;
import network.vo.ResponsePacket;
import network.vo.WsMessage;
import utils.Constant;
import utils.JsonUtil;
import utils.ScreenUtil;
import utils.ThreadPoolUtil;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Future;


/**
 * @author bobo
 * @date 2021/6/29
 */

public class ClientHandler extends ChannelInboundHandlerAdapter {
    private final SocketClient client;
    private boolean isSend;


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
                isSend = true;
                ThreadPoolUtil.executor(()->{
                    WsMessage wsMessage = JsonUtil.parseObject(content,WsMessage.class);
                    while (isSend){
                        wsMessage.setContent(Base64.getEncoder().encodeToString(ScreenUtil.getDesktopScreen()));
                        client.sendData(Constant.IMAGE, JsonUtil.toJsonString(wsMessage).getBytes(StandardCharsets.UTF_8));
                    }
                });
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
        client.doConnect();
    }
}
