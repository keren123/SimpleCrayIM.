package com.crazymakercircle.imServer.handler;

import com.crazymakercircle.im.common.bean.msg.ProtoMsg;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service("multiEchoHandler")
@ChannelHandler.Sharable
public class MultiEchoHandler
        extends ChannelInboundHandlerAdapter {

    //重复次数
    public static int repeat_time = 0;


    /**
     * 重复5次
     *
     * @param ctx
     * @param pkg
     */
    private void repeatEchoBack(ChannelHandlerContext ctx, ProtoMsg.Message pkg) {
        log.info(" 重复 发送 {} 次  消息 to client",repeat_time);
        //先发一次
        ctx.writeAndFlush(pkg);
        //再重复来
        for (int i = 1; i < repeat_time; i++) {
            ctx.writeAndFlush(pkg);
        }
    }


    /**
     * 收到消息
     */
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        if (null == msg || !(msg instanceof ProtoMsg.Message)) {
            super.channelRead(ctx, msg);
            return;
        }
        //处理消息的内容
        ProtoMsg.Message pkg = (ProtoMsg.Message) msg;
        repeatEchoBack(ctx, pkg);

    }


}
