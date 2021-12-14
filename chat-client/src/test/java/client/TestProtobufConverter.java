package client;

import com.crazymakercircle.im.common.bean.User;
import com.crazymakercircle.im.common.bean.msg.ProtoMsg;
import com.crazymakercircle.imClient.protoConverter.LoginMsgConverter;
import com.crazymakercircle.imClient.session.ClientSession;
import com.crazymakercircle.util.Logger;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
                LoginMsgConverter.build(getUser(), getSession());

        Logger.info("session id:=" + message.getSessionId());
        ProtoMsg.LoginRequest pkg=  message.getLoginRequest();
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
