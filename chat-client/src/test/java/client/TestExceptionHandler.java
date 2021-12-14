package client;

import com.crazymakercircle.im.common.bean.User;
import com.crazymakercircle.im.common.bean.msg.ProtoMsg;
import com.crazymakercircle.im.common.codec.SimpleProtobufDecoder;
import com.crazymakercircle.im.common.codec.SimpleProtobufEncoder;
import com.crazymakercircle.imClient.handler.ExceptionHandler;
import com.crazymakercircle.imClient.protoConverter.HeartBeatMsgConverter;
import com.crazymakercircle.imClient.session.ClientSession;
import com.crazymakercircle.util.Logger;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.MessageToByteEncoder;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * create by 尼恩 @ 疯狂创客圈
 **/
public class TestExceptionHandler {


    static class MockExceptInHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

            Logger.info(" Except throw s in  channelRead");
            throw new Exception("sth wrong");
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            super.exceptionCaught(ctx, cause);
        }
    }


    @Test
    public void testInboundExceptOutHandler() {
        ChannelInitializer i = new ChannelInitializer<EmbeddedChannel>() {
            protected void initChannel(EmbeddedChannel ch) {
                // 半包的处理
//                ch.pipeline().addLast("outHandler", new MockExceptOutHandler());
//                ch.pipeline().addLast("encoder",  SimpleProtobufEncoder());
                ch.pipeline().addLast("inHandler", new MockExceptInHandler());
                //          ch.pipeline().addLast("dummyHandler", new ChannelInboundHandlerAdapter());
                ch.pipeline().addLast("exception", new ExceptionHandler());

            }
        };

        EmbeddedChannel channel = new EmbeddedChannel(i);
        ByteBuf bytebuf = Unpooled.buffer(1024).order(ByteOrder.BIG_ENDIAN);
        ProtoMsg.Message pkg = buildLoginMsg(new User());

        SimpleProtobufEncoder.encode0(pkg, bytebuf);

        channel.writeInbound(bytebuf);
        channel.flush();

        try {
            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static class MockExceptOutHandler extends MessageToByteEncoder<ProtoMsg.Message> {


        @Override
        protected void encode(ChannelHandlerContext ctx,
                              ProtoMsg.Message msg, ByteBuf out)
                throws Exception {

            Logger.info(" Except throw s in  encode");
            throw new Exception("sth wrong");
        }
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            super.exceptionCaught(ctx, cause);
        }
    }

    public class SendHandler extends ChannelInboundHandlerAdapter {
        //心跳的时间间隔，单位为s
        private static final int HEARTBEAT_INTERVAL = 50;

        //在Handler被加入到Pipeline时，开始发送心跳
        @Override
        public void channelActive(ChannelHandlerContext ctx)
                throws Exception {
              HeartBeatMsgConverter builder =
                    new HeartBeatMsgConverter(getUser(), getSession() );

            ProtoMsg.Message message = builder.build();
//            ChannelFuture f = ctx.writeAndFlush(message);
            ChannelFuture f = ctx.pipeline().writeAndFlush(message);
//            ChannelFuture f = ctx.channel().writeAndFlush(message);
            f.addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture future) throws Exception {
                    if(future.isDone()){
                        if(future.isSuccess()){
                            System.out.println("执行成功...");
                        }else if(future.isCancelled()){
                            System.out.println("执行失败...");
                        }else if(future.cause()!=null){
                            System.out.println("执行出错："+future.cause().getMessage());
                        }
                    }
                }
            });
        }
    }
    @Test
    public void testOutboundExceptOutHandler() {
        ChannelInitializer i = new ChannelInitializer<EmbeddedChannel>() {
            protected void initChannel(EmbeddedChannel ch) {
                ch.pipeline().addLast("SendHandler", new SendHandler());

                ch.pipeline().addLast("outHandler", new MockExceptOutHandler());

//                ch.pipeline().addLast("encoder", new SimpleProtobufEncoder());
//                ch.pipeline().addLast("inHandler", new MockExceptInHandler());
                ch.pipeline().addLast("exception", new ExceptionHandler());

            }
        };

        EmbeddedChannel channel = new EmbeddedChannel(i);
        channel.flush();
        try {
            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 构建消息 整体 的 Builder
     */
    public static ProtoMsg.Message.Builder outerBuilder(long seqId) {

        ProtoMsg.Message.Builder mb =
                ProtoMsg.Message
                        .newBuilder()
                        .setType(ProtoMsg.HeadType.LOGIN_REQUEST)
                        .setSessionId("-1")
                        .setSequence(seqId);
        return mb;
    }


    public static ProtoMsg.Message buildLoginMsg(User user) {
        ProtoMsg.Message.Builder outBuilder = outerBuilder(-1);
        ProtoMsg.LoginRequest.Builder lb =
                ProtoMsg.LoginRequest.newBuilder()
                        .setDeviceId(user.getDevId())
                        .setPlatform(user.getPlatform().ordinal())
                        .setToken(user.getToken())
                        .setUid(user.getUid());
        return outBuilder.setLoginRequest(lb).build();
    }

    private ClientSession getSession() {
        // 创建会话
        ClientSession session=new ClientSession(new EmbeddedChannel());

        session.setConnected(true);
        return session;
    }

    private User getUser() {

        User user = new User();
        user.setUid("1");
        user.setToken(UUID.randomUUID().toString());
        user.setDevId(UUID.randomUUID().toString());
        return  user;

    }
}
