package com.crazymakercircle.imServer.server;

import com.crazymakercircle.im.common.codec.SimpleProtobufDecoder;
import com.crazymakercircle.im.common.codec.SimpleProtobufEncoder;
import com.crazymakercircle.imServer.handler.SoulTest1ServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Data
@Slf4j
@Service("soulTest1Server")
public class SoulTest1Server {

    /* 通过nio方式来接收连接和处理连接*/
    // 通过nio方式来接收连接和处理连接
    private EventLoopGroup bg ;
    private EventLoopGroup wg ;

    // 启动引导器
    private ServerBootstrap b = new ServerBootstrap();

    @Autowired
    private SoulTest1ServerHandler soulTest1ServerHandler;


    //请求的时间间隔，单位为ms
    public static long serverPortCount = 10;


    public void run() {
        //连接监听线程组
        bg = new NioEventLoopGroup(1);
        //传输处理线程组
        wg = new NioEventLoopGroup();
        try {   //1 设置reactor 线程
            b.group(bg, wg);
            //2 设置nio类型的channel
            b.channel(NioServerSocketChannel.class);

            //4 设置通道选项
            b.childOption(ChannelOption.ALLOCATOR,
                    PooledByteBufAllocator.DEFAULT);

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
            b.childOption(ChannelOption.SO_REUSEADDR, true);

            //5 装配流水线
            b.childHandler(new ChannelInitializer<SocketChannel>() {
                //有连接到达时会创建一个channel
                protected void initChannel(SocketChannel ch) throws Exception {
                    // 管理pipeline中的Handler
                    ch.pipeline().addLast(new SimpleProtobufDecoder());
                    ch.pipeline().addLast(new SimpleProtobufEncoder());
                    ch.pipeline().addLast(soulTest1ServerHandler);
                }
            });
            // 6 开始绑定server
            // 通过调用sync同步方法阻塞直到绑定成功

            ChannelFuture firstChannelFuture = null;
            int start = 20000;
            int count = (int) serverPortCount;
            for (int i = 0; i < count; i++) {
                int port = start + i;
                //3 设置监听端口
                ChannelFuture f = b.bind(port).addListener((ChannelFutureListener) future -> {
                    System.out.println("bind success in port: " + port);
                });
                if (i == 0) {
                    firstChannelFuture = f;
                }
            }
            log.info("Soul Test 1 Server started! ");

            // 7 监听通道关闭事件
            // 应用程序会一直等待，直到channel关闭
            ChannelFuture closeFuture =
                    firstChannelFuture.sync().channel().closeFuture();
            closeFuture.sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 8 优雅关闭EventLoopGroup，
            // 释放掉所有资源包括创建的线程
            wg.shutdownGracefully();
            bg.shutdownGracefully();
        }

    }

}
