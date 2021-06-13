package com.crazymakercircle.imClient.handler;


import com.crazymakercircle.im.common.bean.msg.ProtoMsg;
import com.crazymakercircle.im.common.protoBuilder.NotificationMsgBuilder;
import com.crazymakercircle.util.ThreadUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@ChannelHandler.Sharable
@Service("lowSpeedConsumerHandler")
public class LowSpeedConsumerHandler extends ChannelInboundHandlerAdapter {


    //模拟业务执行时间，单位为ms
    public static int mock_business_time = 1000;

    //待发送的字符串种子

    String toSendContent = null;
    //待复制的字符串种子
    String seed = "疯狂创客圈  Java ，一个最佳的Java 高并发交流社群";

    Channel channel = null;

    public LowSpeedConsumerHandler() {
        //一次发送4000个字符
        StringBuilder sb = new StringBuilder(4096);
        while (sb.length() < 4000) {
            sb.append(seed);
        }
        this.toSendContent = sb.toString();
    }


    //在Handler被加入到Pipeline时，开始发送心跳
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        channel = ctx.channel();
        ThreadUtil.scheduleAtFixedRate(() ->
                repeatSend(), 500, TimeUnit.MILLISECONDS);
    }

    //使用定时器，发送消息
    public void repeatSend() {
        ProtoMsg.Message message =
                NotificationMsgBuilder.buildNotification(toSendContent);
        //发送心跳  消息

        if (channel.isActive()) {
            log.info(" 发送消息 to server");
            channel.writeAndFlush(message);
        }


    }

    /**
     * 接受到服务器的心跳回写
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //判断消息实例
        if (null == msg || !(msg instanceof ProtoMsg.Message)) {
            super.channelRead(ctx, msg);
            return;
        }

        //处理消息的内容
        ProtoMsg.Message notificationPkg = (ProtoMsg.Message) msg;

        String json = notificationPkg.getNotification().getJson();

        log.info("{} 收到回复的消息： {}", ctx.channel().id(), json);

        //等待一下，模拟业务处理
        ThreadUtil.sleepMilliSeconds(mock_business_time);
    }

}
