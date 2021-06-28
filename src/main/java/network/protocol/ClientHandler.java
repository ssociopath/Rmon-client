package network.protocol;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import network.SocketClient;
import network.entity.MsgPack;
import network.entity.Rule;
import ui.views.MainFrame;
import utils.Constant;
import utils.JsonUtil;
import utils.ScreenUtil;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author bobo
 * @date 2021/6/29
 */

public class ClientHandler extends ChannelInboundHandlerAdapter {
    private final SocketClient client;

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
        byte type = msgPack.getType();
        String content = new String(msgPack.getContent(),StandardCharsets.UTF_8);
        System.out.println("服务端响应："+content);
        switch (type){
            case Constant.IMAGE:{
                for (int i = 0; i < 500; i++) {
                    client.sendData(Constant.IMAGE, ScreenUtil.getDesktopScreen());
                }
                break;
            }
            case Constant.LOGIN:{
                String failRes = "登录失败";
                if(failRes.equals(content)){
                    client.getILoginListener().onLogin(null);
                }else{
                    List<Rule> ruleList = JsonUtil.parseList(content, Rule.class);
                    List<String[]> strList = ruleList.stream()
                            .map(rule -> new String[]{rule.getRuleId().toString(),rule.getUsername(),rule.getPermission()})
                            .collect(Collectors.toList());
                    client.getILoginListener().onLogin(strList);
                }
                break;
            }
            default: break;
        }


    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        client.doConnect();
    }
}
