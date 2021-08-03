package com.crazymakercircle.imServer.handler;

import com.crazymakercircle.im.common.bean.msg.ProtoMsg;
import com.crazymakercircle.util.ThreadUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service("soulTest1ServerHandler")
@ChannelHandler.Sharable
public class SoulTest1ServerHandler
        extends ChannelInboundHandlerAdapter {
    //连接数
    private AtomicInteger receivedConnecitons = new AtomicInteger();

    public SoulTest1ServerHandler() {
    }

    public void init() {

        //每秒打印一次  当前的连接数
        ThreadUtil.scheduleAtFixedRate(() -> {
            log.info(" 当前的连接数 {} ", receivedConnecitons.get());
        }, 1, TimeUnit.SECONDS);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        receivedConnecitons.incrementAndGet();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        receivedConnecitons.decrementAndGet();
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

        //立即发送
        ctx.writeAndFlush(pkg);

    }


}
