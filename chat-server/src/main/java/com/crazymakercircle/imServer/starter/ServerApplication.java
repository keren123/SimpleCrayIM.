package com.crazymakercircle.imServer.starter;

import com.crazymakercircle.imServer.handler.MultiEchoHandler;
import com.crazymakercircle.imServer.handler.SoulTest1ServerHandler;
import com.crazymakercircle.imServer.server.ChatServer;
import com.crazymakercircle.imServer.server.MultiEchoServer;
import com.crazymakercircle.imServer.server.SoulTest1Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


//自动加载配置信息
@Configuration
@EnableAutoConfiguration
//使包路径下带有@Value的注解自动注入
//使包路径下带有@Autowired的类可以自动注入
@ComponentScan("com.crazymakercircle.imServer")
@SpringBootApplication
@Slf4j
public class ServerApplication {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // 启动并初始化 Spring 环境及其各 Spring 组件
        ApplicationContext context =
                SpringApplication.run(ServerApplication.class, args);


        if (args.length > 0 &&
                (args[0] != null && args[0].equalsIgnoreCase("soultest1"))) {
            //启动 灵魂测试1  服务器
            startSoultest1Server(context, args);

        } else if (args.length > 0 &&
                (args[0] != null && args[0].equalsIgnoreCase("multiEcho"))) {
            //启动重复回显的服务器
            startMultiEchoServer(context, args);
        } else {
            //启动聊天服务器
            startChatServer(context);
        }
    }

    //启动 重复回显的服务器
    private static void startMultiEchoServer(ApplicationContext context, String[] args) {

        MultiEchoServer multiEchoServer = context.getBean(MultiEchoServer.class);
        MultiEchoHandler multiEchoHandler = context.getBean(MultiEchoHandler.class);
        if (args.length > 1 && args[1] != null) {
            //重复回显的次数
            multiEchoHandler.repeat_time = Integer.parseInt(args[1]);
        }
        multiEchoServer.run();

    }

    //启动 灵魂测试 服务端 1
    private static void startSoultest1Server(ApplicationContext context, String[] args) {

        SoulTest1Server soulTest1Server = context.getBean(SoulTest1Server.class);
        SoulTest1ServerHandler soulTest1ServerHandler = context.getBean(SoulTest1ServerHandler.class);

        if (args.length > 1 && args[1] != null) {
            //设置的端口数，如100
            SoulTest1Server.serverPortCount = Long.parseLong(args[1]);
            log.debug("设置的端口数，{}", SoulTest1Server.serverPortCount);

            soulTest1ServerHandler.init();
        }
        soulTest1Server.run();

    }

    //启动聊天服务器
    private static void startChatServer(ApplicationContext context) {
        ChatServer nettyServer =  context.getBean(ChatServer.class);
        nettyServer.run();
    }


}

