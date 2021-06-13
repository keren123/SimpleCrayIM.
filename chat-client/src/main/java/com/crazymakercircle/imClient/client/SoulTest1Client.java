package com.crazymakercircle.imClient.client;

import com.crazymakercircle.im.common.codec.SimpleProtobufDecoder;
import com.crazymakercircle.im.common.codec.SimpleProtobufEncoder;
import com.crazymakercircle.imClient.handler.SoulTest1ClientHandler;
import com.crazymakercircle.util.ThreadUtil;
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
import org.springframework.stereotype.Service;

@Slf4j
@Data
@Service("soulTest1Client")
public class SoulTest1Client {
    //请求的时间间隔，单位为ms
    public static long serverPortCount = 10;

    //连接上限
    public static long maxConnection = 100;

    // 服务器ip地址
    public static String serverHost = "127.0.0.1";


    @Autowired
    private SoulTest1ClientHandler soulTest1ClientHandler;


    private Channel channel;

    private Bootstrap b;
    private EventLoopGroup g;

    public SoulTest1Client() {

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
            b.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);


            /**
             * SO_REUSEADDR提供如下四个功能：
             *
             *     SO_REUSEADDR允许启动一个监听服务器并捆绑其众所周知端口，即使以前建立的将此端口用做他们的本地端口的连接仍存在。这通常是重启监听服务器时出现，若不设置此选项，则bind时将出错。
             *
             *     SO_REUSEADDR允许在同一端口上启动同一服务器的多个实例，只要每个实例捆绑一个不同的本地IP地址即可。对于TCP，我们根本不可能启动捆绑相同IP地址和相同端口号的多个服务器。
             *
             *     SO_REUSEADDR允许单个进程捆绑同一端口到多个套接口上，只要每个捆绑指定不同的本地IP地址即可。这一般不用于TCP服务器。
             *
             *     SO_REUSEADDR允许完全重复的捆绑：当一个IP地址和端口绑定到某个套接口上时，还允许此IP地址和端口捆绑到另一个套接口上。一般来说，这个特性仅在支持多播的系统上才有，而且只对UDP套接口而言（TCP不支持多播）。
             */
            b.option(ChannelOption.SO_REUSEADDR, true);

            // 设置通道初始化
            b.handler(
                    new ChannelInitializer<SocketChannel>() {
                        public void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast("decoder", new SimpleProtobufDecoder());
                            ch.pipeline().addLast("encoder", new SimpleProtobufEncoder());
                            ch.pipeline().addLast(soulTest1ClientHandler);
                        }
                    }
            );
            log.info("客户端开始连接 [soul test 1 Server]");

            //客户端无限制发起连接，直到上限
            int indexOfServerPort = 0;
            int serverPort;
            int connectionCount = 0;
            while (!Thread.interrupted()) {
                serverPort = 20000 + indexOfServerPort;
                try {
                    ChannelFuture channelFuture = b.connect(serverHost, serverPort);
                    channelFuture.addListener((ChannelFutureListener) future -> {
                        if (!future.isSuccess()) {
                            log.debug("connect failed, exit!");
                            System.exit(0);
                        } else {
                            log.debug("connect success!!");
                        }
                    });
                    channelFuture.get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ++indexOfServerPort;
                if (indexOfServerPort == serverPortCount) indexOfServerPort = 0;
                if (++connectionCount == maxConnection) break;
            }

            ThreadUtil.sleepMilliSeconds(Integer.MAX_VALUE);

        } catch (Exception e) {
            log.info("客户端连接失败!" + e.getMessage());
        }
    }

    public void close() {
        g.shutdownGracefully();
    }


}
