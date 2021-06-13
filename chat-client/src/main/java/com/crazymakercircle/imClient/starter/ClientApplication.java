package com.crazymakercircle.imClient.starter;

import com.crazymakercircle.imClient.client.CommandController;
import com.crazymakercircle.imClient.client.LowSpeedConsumerClient;
import com.crazymakercircle.imClient.client.SoulTest1Client;
import com.crazymakercircle.imClient.handler.LowSpeedConsumerHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
//自动加载配置信息
@EnableAutoConfiguration
//使包路径下带有@Value的注解自动注入
//使包路径下带有@Autowired的类可以自动注入
@ComponentScan("com.crazymakercircle.imClient")
@SpringBootApplication

public class ClientApplication {


    /**
     * @param args
     */
    public static void main(String[] args) {

        // 启动并初始化 Spring 环境及其各 Spring 组件
        ApplicationContext context = SpringApplication.run(ClientApplication.class, args);

        if (args.length > 0 && (args[0] != null && args[0].equalsIgnoreCase("soultest1"))) {
//      jvm选项    -Xms2G -Xmx8G
//      命令 参数   soultest1 10 1000000 cdh1
            //启动 灵魂测试 客户端 1
            startSoultest1Client(context, args);

        } else if (args.length > 0 && (args[0] != null && args[0].equalsIgnoreCase("multiEcho"))) {
            //启动低速客户端
            startLowSpeedClient(context, args);

        } else {
            //启动聊天客户端

            startChatClient(context);
        }
    }

    //启动低速客户端
    private static void startLowSpeedClient(ApplicationContext context, String[] args) {

        LowSpeedConsumerClient lowSpeedConsumerClient = context.getBean(LowSpeedConsumerClient.class);
        LowSpeedConsumerHandler lowSpeedConsumerHandler = context.getBean(LowSpeedConsumerHandler.class);

        if (args.length > 1 && args[1] != null) {
            //模拟业务处理的时间，单位为ms，默认为1000毫秒
            lowSpeedConsumerHandler.mock_business_time = Integer.parseInt(args[1]);
        }
        lowSpeedConsumerClient.doConnect();

    }

    //启动 灵魂测试 客户端 1
    private static void startSoultest1Client(ApplicationContext context, String[] args) {

        SoulTest1Client soulTest1Client = context.getBean(SoulTest1Client.class);

        if (args.length > 1 && args[1] != null) {
            //请求的时间间隔，单位为ms
            SoulTest1Client.serverPortCount = Long.parseLong(args[1]);
        }
        if (args.length > 2 && args[2] != null) {
            //连接上限
            SoulTest1Client.maxConnection = Long.parseLong(args[2]);
        }

        if (args.length > 3 && args[3] != null) {
            //服务器ip地址
            SoulTest1Client.serverHost = args[3];
        }
        soulTest1Client.doConnect();

    }

    //启动聊天客户端
    private static void startChatClient(ApplicationContext context) {
        CommandController commandClient = context.getBean(CommandController.class);

        commandClient.initCommandMap();
        try {
            commandClient.commandThreadRunning();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
