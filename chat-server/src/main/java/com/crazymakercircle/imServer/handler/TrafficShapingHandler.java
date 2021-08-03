package com.crazymakercircle.imServer.handler;

import com.crazymakercircle.im.common.bean.msg.ProtoMsg;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service("trafficShapingHandler")
@ChannelHandler.Sharable
public class TrafficShapingHandler
        extends ChannelInboundHandlerAdapter {

    long in = 0, out = 0;
    //等待时间参数
    public static long wait_time = 0;

    //消息暂存的列表
    Queue<ProtoMsg.Message> receivedQueue = new LinkedBlockingQueue();

    //在Handler被加入到Pipeline时，开始启动发送
    @Override
    public void handlerAdded(ChannelHandlerContext ctx)
            throws Exception {
        if (wait_time > 0) {
            //整流
            waitThenSend(ctx);
        }
    }

    //等待后发送报文
    public void waitThenSend(ChannelHandlerContext ctx) {
        ctx.executor().schedule(() -> {
            ProtoMsg.Message pkg = null;
            pkg = receivedQueue.remove();
            if (null != pkg) {
                log.info(" 发送  消息 to client, in {} out {} ", in, out);
                repeatEchoBack(ctx, pkg);
                out++;
                //递归调用，发送下一次的回复

                waitThenSend(ctx);
            }

        }, wait_time, TimeUnit.MILLISECONDS);
    }

    /**
     * 重复5次
     *
     * @param ctx
     * @param pkg
     */
    private void repeatEchoBack(ChannelHandlerContext ctx, ProtoMsg.Message pkg) {
        for (int i = 0; i < 5; i++) {
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

        in++;

        if (wait_time <= 0) {
            //立即发送,重复5次
            repeatEchoBack(ctx, pkg);


        } else {
            //等待一定时间发送,放到暂存消息的列表
            receivedQueue.add(pkg);

            return;
        }

    }


}
