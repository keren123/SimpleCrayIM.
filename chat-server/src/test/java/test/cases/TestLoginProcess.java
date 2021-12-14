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
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ServerApplication.class)
@Slf4j
public class TestLoginProcess {
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


    public ProtoMsg.Message buildLoginMsg(User user) {

        ProtoMsg.Message.Builder outer =
                ProtoMsg.Message.newBuilder()
                        .setType(ProtoMsg.HeadType.LOGIN_REQUEST)
                        .setSessionId(UUID.randomUUID().toString())
                        .setSequence(-1);


        ProtoMsg.LoginRequest.Builder lb =
                ProtoMsg.LoginRequest.newBuilder()
                        .setDeviceId(user.getDevId())
                        .setPlatform(user.getPlatform().ordinal())
                        .setToken(user.getToken())
                        .setUid(user.getUid());
        return outer.setLoginRequest(lb).build();
    }


}
