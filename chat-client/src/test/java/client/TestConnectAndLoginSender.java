package client;


import com.crazymakercircle.im.common.bean.User;
import com.crazymakercircle.im.common.codec.SimpleProtobufDecoder;
import com.crazymakercircle.im.common.codec.SimpleProtobufEncoder;
import com.crazymakercircle.imClient.session.ClientSession;
import com.crazymakercircle.imClient.config.SystemConfig;
import com.crazymakercircle.imClient.handler.ExceptionHandler;
import com.crazymakercircle.imClient.handler.LoginResponseHandler;
import com.crazymakercircle.imClient.sender.LoginSender;
import com.crazymakercircle.imClient.starter.ClientApplication;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.UUID;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ClientApplication.class)
@ContextConfiguration(classes=SystemConfig.class)
@Slf4j
public class TestConnectAndLoginSender {


    @Autowired
    private LoginSender loginSender;

    @Autowired
    private SystemConfig systemConfig;


    private Channel channel;

    private Bootstrap b = new Bootstrap();
    private EventLoopGroup g= new NioEventLoopGroup(1);



    @Autowired
    private LoginResponseHandler loginResponseHandler;


    @Autowired
    private ExceptionHandler exceptionHandler;



    private void initBootstrap() {
        b.group(g);
        b.channel(NioSocketChannel.class);
        b.option(ChannelOption.SO_KEEPALIVE, true);
        b.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        b.remoteAddress(systemConfig.getHost(), systemConfig.getPort());
    }


    GenericFutureListener<ChannelFuture> connectedListener_1 = (ChannelFuture f) ->
    {
        final EventLoop eventLoop
                = f.channel().eventLoop();
        if (!f.isSuccess()) {
            log.info("????????????!???10s????????????????????????!");

        } else {
            log.info("???????????? ?????????????????? IM ????????? ????????????!");
            channel = f.channel();
            // ????????????
            ClientSession  session = new ClientSession(channel);
            session.setConnected(true);
//            channel.closeFuture().addListener(closeListener);

        }

    };

    //???????????????????????????
    @Test
    public void testConnectSever() throws IOException
    {
        initBootstrap();
        // ?????????????????????
        b.handler(
                new ChannelInitializer<SocketChannel>() {
                    public void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast("decoder", new SimpleProtobufDecoder());
                        ch.pipeline().addLast("encoder", new SimpleProtobufEncoder());
                    }
                }
        );
        log.info("???????????????????????????????????? [???????????????IM]");
        //??????????????????
        ChannelFuture f = b.connect();
        f.addListener(connectedListener_1);

        try {
            f.sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("????????????????????????");

        f.channel().close();
    }

    GenericFutureListener<ChannelFuture> connectedListener2 = (ChannelFuture f) ->
    {
        final EventLoop eventLoop
                = f.channel().eventLoop();
        if (!f.isSuccess()) {
            log.info("????????????!???10s????????????????????????!");

        } else {
            log.info("???????????? ?????????????????? IM ????????? ????????????!");
            channel = f.channel();
            // ????????????
            ClientSession  session = new ClientSession(channel);
            session.setConnected(true);
//            channel.closeFuture().addListener(closeListener);
            startLogin(session);
        }

    };

    private void startLogin(ClientSession session) {
        //??????

        User user = new User();
        user.setUid("1");
        user.setToken(UUID.randomUUID().toString());
        user.setDevId(UUID.randomUUID().toString());

        loginSender.setUser(user);
        loginSender.setSession(session);
        loginSender.sendLoginMsg();
    }

    //?????????????????????
    @Test
    public void testLoginSender() throws IOException
    {
        initBootstrap();
        // ?????????????????????
        b.handler(
                new ChannelInitializer<SocketChannel>() {
                    public void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast("decoder", new SimpleProtobufDecoder());
                        ch.pipeline().addLast("encoder", new SimpleProtobufEncoder());
                    }
                }
        );
        log.info("???????????????????????????????????? [???????????????IM]");

        ChannelFuture f = b.connect();//??????????????????
        f.addListener(connectedListener2);

        try {
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("????????????????????????");

        f.channel().close();
    }


    //?????????????????????
    @Test
    public void testLoginResponceHandler() throws IOException
    {
        initBootstrap();
        // ?????????????????????
        b.handler(
                new ChannelInitializer<SocketChannel>() {
                    public void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast("encoder", new SimpleProtobufEncoder());
                        ch.pipeline().addLast("decoder", new SimpleProtobufDecoder());
                        ch.pipeline().addLast(loginResponseHandler);
                        ch.pipeline().addLast(exceptionHandler);
                    }
                }
        );
        log.info("???????????????????????????????????? [???????????????IM]");

        ChannelFuture f = b.connect();//??????????????????
        f.addListener(connectedListener2);

        try {
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("????????????????????????");

        f.channel().close();
    }


}
