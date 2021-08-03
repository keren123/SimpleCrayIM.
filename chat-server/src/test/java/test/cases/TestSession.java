package test.cases;


import com.crazymakercircle.im.common.bean.User;
import com.crazymakercircle.im.common.bean.msg.ProtoMsg;
import com.crazymakercircle.im.common.codec.SimpleProtobufDecoder;
import com.crazymakercircle.im.common.codec.SimpleProtobufEncoder;
import com.crazymakercircle.imServer.handler.LoginRequestHandler;
import com.crazymakercircle.imServer.server.ServerSession;
import com.crazymakercircle.imServer.server.SessionMap;
import com.crazymakercircle.imServer.starter.ServerApplication;
import com.crazymakercircle.util.JsonUtil;
import com.crazymakercircle.util.Logger;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.util.Attribute;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.management.Query;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.List;

@Slf4j
public class TestSession {


    //测试用例： 会话的双向绑定

    @Test
    public void testSessionBind() throws Exception {
        ServerSession serverSession=new ServerSession(new EmbeddedChannel());
        serverSession.setUser(new User());

        Logger.cfo(JsonUtil.pojoToJson(serverSession.getUser()));
        serverSession.bind();

        Channel channel=serverSession.getChannel();

        Logger.cfo("eg:反向导航");
        Attribute<ServerSession> session = channel.attr(ServerSession.SESSION_KEY);
        Logger.cfo(JsonUtil.pojoToJson(session.get().getUser()));

    }


    //测试用例： 会话的查找

    @Test
    public void testSessionFind() throws Exception {
        User user=new User();
        JsonUtil.pojoToJson(user);
        for (int i = 0; i < 10; i++) {
            ServerSession serverSession=new ServerSession(new EmbeddedChannel());
            serverSession.setUser(user);
            serverSession.bind();
        }
        Logger.cfo("eg:用户查找");
        List<ServerSession> list = SessionMap.inst().getSessionsBy(user.getUid());
        Logger.cfo("找到的session数量："+list.size());
        list.stream().forEach(s->Logger.cfo( s.getSessionId()));
    }

}
