package com.crazymakercircle.imClient.client;

import com.crazymakercircle.im.common.codec.SimpleProtobufDecoder;
import com.crazymakercircle.im.common.codec.SimpleProtobufEncoder;
import com.crazymakercircle.imClient.handler.LowSpeedConsumerHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Data
@Service("lowSpeedClient")
public class LowSpeedConsumerClient {

    // 服务器ip地址
    @Value("${chat.server.ip}")
    private String host;
    // 服务器端口
    @Value("${chat.server.port}")
    private int port;

    @Autowired
    private LowSpeedConsumerHandler lowSpeedConsumerHandler;


    private Channel channel;

    private Bootstrap b;
    private EventLoopGroup g;

    public LowSpeedConsumerClient() {

        /**
         * 客户端的是Bootstrap，服务端的则是 ServerBootstrap。
         * 都是AbstractBootstrap的子类。
         **/

        /**
         * 通过nio方式来接收连接和处理连接
         */

        g = new NioEventLoopGroup();


    }

    /**
     * 重连
     */
    public void doConnect() {
        try {
            b = new Bootstrap();

            b.group(g);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
            b.remoteAddress(host, port);

            // 设置通道初始化
            b.handler(
                    new ChannelInitializer<SocketChannel>() {
                        public void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast("decoder", new SimpleProtobufDecoder());
                            ch.pipeline().addLast("encoder", new SimpleProtobufEncoder());
                            ch.pipeline().addLast(lowSpeedConsumerHandler);
                        }
                    }
            );
            log.info("客户端开始连接 [multiEcho Server]");

            ChannelFuture f = b.connect();//异步发起连接
            f.addListener((ChannelFutureListener) future -> {
                if (!future.isSuccess()) {
                    log.info("客户端 连接 [multiEcho Server] 失败");
                    return;
                }
                log.info("客户端 连接 [echo Server] 成功");
            });
            f.get();

            // 阻塞
            f.channel().closeFuture().sync();

        } catch (Exception e) {
            log.info("客户端连接失败!" + e.getMessage());
        }
    }

    public void close() {
        g.shutdownGracefully();
    }


}
