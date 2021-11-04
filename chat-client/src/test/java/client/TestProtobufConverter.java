package client;

import com.crazymakercircle.im.common.bean.User;
import com.crazymakercircle.im.common.bean.msg.ProtoMsg;
import com.crazymakercircle.im.common.codec.SimpleProtobufDecoder;
import com.crazymakercircle.im.common.codec.SimpleProtobufEncoder;
import com.crazymakercircle.imClient.protoConverter.LoginMsgConverter;
import com.crazymakercircle.imClient.session.ClientSession;
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
import java.util.UUID;

/**
 * create by 尼恩 @ 疯狂创客圈
 **/
public class TestProtobufConverter
{


    @Test
    public void testLoginMsgConverter() throws IOException
    {


        ProtoMsg.Message message =
                LoginMsgConverter.buildLoginMsg(getUser(), getSession());
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
