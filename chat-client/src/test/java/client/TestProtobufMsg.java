package client;

import com.crazymakercircle.im.common.bean.User;
import com.crazymakercircle.im.common.bean.msg.ProtoMsg;
import com.crazymakercircle.im.common.codec.SimpleProtobufDecoder;
import com.crazymakercircle.im.common.codec.SimpleProtobufEncoder;
import com.crazymakercircle.util.Logger;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteOrder;

/**
 * create by 尼恩 @ 疯狂创客圈
 **/
public class TestProtobufMsg
{



    static class LoginRequestHandler extends ChannelInboundHandlerAdapter
    {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
        {
            ProtoMsg.Message inMsg = (ProtoMsg.Message) msg;
            ProtoMsg.LoginRequest pkg=  inMsg.getLoginRequest();
            Logger.info("id:=" + pkg.getUid());
            Logger.info("content:=" + pkg.getToken());
        }
    }


    /**
     * 字符串解码器的使用实例
     */
    @Test
    public void testProtobufDecoder()
    {
        ChannelInitializer i = new ChannelInitializer<EmbeddedChannel>()
        {
            protected void initChannel(EmbeddedChannel ch)
            {
                  // 半包的处理
                ch.pipeline().addLast("decoder", new SimpleProtobufDecoder());
                ch.pipeline().addLast("encoder", new SimpleProtobufEncoder());
                ch.pipeline().addLast("inHandler", new LoginRequestHandler());

            }
        };

        EmbeddedChannel channel = new EmbeddedChannel(i);
        for (int j = 0; j < 100; j++)
        {
            ByteBuf bytebuf= Unpooled.buffer(1024).order(ByteOrder.BIG_ENDIAN);;
            ProtoMsg.Message pkg = buildLoginMsg(new User());
            SimpleProtobufEncoder.encode0(pkg,bytebuf);

            channel.writeInbound(bytebuf);
            channel.flush();
        }

        try
        {
            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
    /**
     * 构建消息 基础部分 的 Builder
     */
    public  static ProtoMsg.Message.Builder baseBuilder(long seqId) {

        ProtoMsg.Message.Builder mb =
                ProtoMsg.Message
                        .newBuilder()
                        .setType(ProtoMsg.HeadType.LOGIN_REQUEST)
                        .setSessionId("-1")
                        .setSequence(seqId);
        return mb;
    }


    public  static ProtoMsg.Message buildLoginMsg(User user) {
        ProtoMsg.Message.Builder  baseBuilder = baseBuilder(-1);
        ProtoMsg.LoginRequest.Builder lb =
                ProtoMsg.LoginRequest.newBuilder()
                        .setDeviceId(user.getDevId())
                        .setPlatform(user.getPlatform().ordinal())
                        .setToken(user.getToken())
                        .setUid(user.getUid());
        return baseBuilder.setLoginRequest(lb).build();
    }

    /**
     * 构建消息 基础部分
     */
    public  static ProtoMsg.Message buildCommon(long seqId) {

        ProtoMsg.Message.Builder mb =
                ProtoMsg.Message
                        .newBuilder()
                        .setType(ProtoMsg.HeadType.LOGIN_REQUEST)
                        .setSessionId("-1")
                        .setSequence(seqId);
        return mb.buildPartial();
    }


    //第1种方式:序列化 serialization & 反序列化 Deserialization
    @Test
    public void serAndDesr1() throws IOException
    {
        ProtoMsg.Message message = buildLoginMsg(new User());
        //将Protobuf对象，序列化成二进制字节数组
        byte[] data = message.toByteArray();
        //可以用于网络传输,保存到内存或外存
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(data);
        data = outputStream.toByteArray();
        //二进制字节数组,反序列化成Protobuf 对象
        ProtoMsg.Message inMsg = ProtoMsg.Message.parseFrom(data);
        ProtoMsg.LoginRequest pkg=  inMsg.getLoginRequest();
        Logger.info("id:=" + pkg.getUid());
        Logger.info("content:=" + pkg.getToken());
    }

}
