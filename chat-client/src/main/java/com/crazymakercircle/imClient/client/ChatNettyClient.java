package com.crazymakercircle.imClient.client;

import com.crazymakercircle.im.common.bean.User;
import com.crazymakercircle.im.common.codec.SimpleProtobufDecoder;
import com.crazymakercircle.im.common.codec.SimpleProtobufEncoder;
import com.crazymakercircle.imClient.config.SystemConfig;
import com.crazymakercircle.imClient.handler.ExceptionHandler;
import com.crazymakercircle.imClient.handler.LoginResponseHandler;
import com.crazymakercircle.imClient.sender.ChatSender;
import com.crazymakercircle.imClient.sender.LoginSender;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Data
@Service("chatNettyClient")
public class ChatNettyClient {
    // 服务器ip地址
    @Value("${chat.server.ip}")
    private String host;
    // 服务器端口
    @Value("${chat.server.port}")
    private int port;


    @Autowired
    private SystemConfig systemConfig;


      @Autowired
    private LoginResponseHandler loginResponseHandler;


    @Autowired
    private ExceptionHandler exceptionHandler;


    private Channel channel;
    private ChatSender sender;
    private LoginSender l;

    /**
     * 唯一标记
     */
    private boolean initFalg = true;
    private User user;
    private GenericFutureListener<ChannelFuture> connectedListener;

    private Bootstrap bootstrap;
    private EventLoopGroup g;

    public ChatNettyClient() {

        /**
         * 客户端的是Bootstrap，服务端的则是 ServerBootstrap。
         * 都是AbstractBootstrap的子类。
         **/

        /**
         * 通过nio方式来接收连接和处理连接
         */

        g = new NioEventLoopGroup(1);


    }

    /**
     * 重连
     */
    public void doConnect() {
        try {
            bootstrap = new Bootstrap();

            bootstrap.group(g);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
            bootstrap.remoteAddress(host, port);

            // 设置通道初始化
            bootstrap.handler(
                    new ChannelInitializer<SocketChannel>() {
                        public void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast("decoder", new SimpleProtobufDecoder());
                            ch.pipeline().addLast("encoder", new SimpleProtobufEncoder());
                            ch.pipeline().addLast(loginResponseHandler);
//                            ch.pipeline().addLast(chatMsgHandler);
                            ch.pipeline().addLast(exceptionHandler);
                        }
                    }
            );
            log.info("客户端开始连接 [疯狂创客圈IM]");

            ChannelFuture f = bootstrap.connect();//异步发起连接
            f.addListener(connectedListener);


            // 阻塞
//             f.channel().closeFuture().sync();

        } catch (Exception e) {
            log.info("客户端连接失败!" + e.getMessage());
        }
    }

    public void close() {
        g.shutdownGracefully();
    }



}
