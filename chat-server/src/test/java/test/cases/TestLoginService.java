package test.cases;


import com.crazymakercircle.im.common.bean.User;
import com.crazymakercircle.im.common.bean.msg.ProtoMsg;
import com.crazymakercircle.im.common.codec.SimpleProtobufDecoder;
import com.crazymakercircle.im.common.codec.SimpleProtobufEncoder;
import com.crazymakercircle.imServer.handler.LoginRequestHandler;
import com.crazymakercircle.imServer.starter.ServerApplication;
import com.crazymakercircle.util.Logger;
import com.crazymakercircle.util.ThreadUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.embedded.EmbeddedChannel;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteOrder;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ServerApplication.class)
@Slf4j
public class TestLoginService {
    @Autowired
    private LoginRequestHandler loginRequestHandler;


    //测试用例： 测试登录处理

    @Test
    public void testLoginProcess() throws Exception {
        ChannelInitializer i = new ChannelInitializer<EmbeddedChannel>() {
            protected void initChannel(EmbeddedChannel ch) {
                // 管理pipeline中的Handler
                ch.pipeline().addLast(new SimpleProtobufDecoder());
                // 在流水线中添加handler来处理登录,登录后删除
                ch.pipeline().addLast("login",loginRequestHandler);
            }
        };
        EmbeddedChannel channel = new EmbeddedChannel(i);

        User user=new User();

        ProtoMsg.Message loginMsg = buildLoginMsg(user);

        ByteBuf bytebuf= Unpooled.buffer(1024).order(ByteOrder.BIG_ENDIAN);;
        SimpleProtobufEncoder.encode0(loginMsg,bytebuf);
        channel.writeInbound(bytebuf);
        channel.flush();

        //无线等待
        ThreadUtil.sleepSeconds(Integer.MAX_VALUE);
    }
    //第1种方式:序列化 serialization & 反序列化 Deserialization
    @Test
    public void serAndDesr1() throws IOException
    {

        User user=new User();
        ProtoMsg.Message  message = buildLoginMsg(user);
        //将Protobuf对象，序列化成二进制字节数组
        byte[] data = message.toByteArray();
        //可以用于网络传输,保存到内存或外存
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(data);
        data = outputStream.toByteArray();
        //二进制字节数组,反序列化成Protobuf 对象
        ProtoMsg.Message  inMsg =  ProtoMsg.Message.parseFrom(data);

        ProtoMsg.LoginRequest info = inMsg.getLoginRequest();
        long seqNo = inMsg.getSequence();

        User user2 = User.fromMsg(info);

        Logger.info("user2:=" + user2);
    }


    public ProtoMsg.Message buildLoginMsg(User user) {
        ProtoMsg.Message message = buildCommon(-1);
        ProtoMsg.LoginRequest.Builder lb =
                ProtoMsg.LoginRequest.newBuilder()
                        .setDeviceId(user.getDevId())
                        .setPlatform(user.getPlatform().ordinal())
                        .setToken(user.getToken())
                        .setUid(user.getUid());
        return message.toBuilder().setLoginRequest(lb).build();
    }

       /**
     * 构建消息 基础部分
     */
    public ProtoMsg.Message buildCommon(long seqId) {

        ProtoMsg.Message.Builder mb =
                ProtoMsg.Message
                        .newBuilder()
                        .setType(ProtoMsg.HeadType.LOGIN_REQUEST)
                        .setSessionId("-1")
                        .setSequence(seqId);
        return mb.buildPartial();
    }
}
