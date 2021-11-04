package client;

import com.crazymakercircle.imClient.command.BaseCommand;
import com.crazymakercircle.imClient.command.ClientCommandMenu;
import com.crazymakercircle.imClient.command.LoginConsoleCommand;
import com.crazymakercircle.imClient.command.LogoutConsoleCommand;
import com.crazymakercircle.util.Logger;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by 尼恩 at 疯狂创客圈
 */

public class TestClientCommand {



    Map<String, BaseCommand> commandMap;
    ClientCommandMenu clientCommandMenu = new ClientCommandMenu();

    @Test
    public void testLoginCommand() {
        LoginConsoleCommand loginConsoleCommand = new LoginConsoleCommand();
        //处理命令
        while (true) {
            Scanner scanner = new Scanner(System.in);
            loginConsoleCommand.exec(scanner);
            Logger.info("本次输入的 username 为：" +
                    loginConsoleCommand.getUserName());
            Logger.info("本次输入的 password 为：" +
                    loginConsoleCommand.getPassword());
        }
    }

    @Test
    public void testClientCommandMenu() {
        ClientCommandMenu commandMenu = new ClientCommandMenu();
        commandMenu.setAllCommandsShow("[menu] 0->show 所有命令 | 1->登录 | ...");
        Scanner scanner = new Scanner(System.in);
        //处理命令
        while (true) {
            commandMenu.exec(scanner);
            Logger.info("本次输入的 菜单 为：" + commandMenu.getCommandInput());

        }
    }

    @Test
    public void testCommandController() {
        initCommandMap();
        //处理命令
        while (true) {

            //菜单输入
            Scanner scanner = new Scanner(System.in);
            clientCommandMenu.exec(scanner);
            String key = clientCommandMenu.getCommandInput();

            //根据菜单输入，选择正确的命令收集器
            BaseCommand command = commandMap.get(key);

            if (null == command) {
                System.err.println("无法识别[" + key + "]指令，请重新输入!");
                continue;
            }

            //执行命令收集器
            switch (key) {
                case LoginConsoleCommand.KEY:
                    command.exec(scanner);
                    Logger.info("本次输入的 username 为：");
                    Logger.info(((LoginConsoleCommand) command).getUserName());
                    Logger.info("本次输入的 password 为：");
                    Logger.info(((LoginConsoleCommand) command).getPassword());

//                    startLogin((LoginConsoleCommand) command);
                    break;

                case LogoutConsoleCommand.KEY:
                    command.exec(scanner);
//                    startLogout((LoginConsoleCommand) command);
                    break;


            }
        }
    }

    public void initCommandMap() {
        commandMap = new HashMap<>();
        LoginConsoleCommand loginConsoleCommand = new LoginConsoleCommand();
        LogoutConsoleCommand logoutConsoleCommand = new LogoutConsoleCommand();
        commandMap.put(clientCommandMenu.getKey(), clientCommandMenu);
        commandMap.put(loginConsoleCommand.getKey(), loginConsoleCommand);
        commandMap.put(logoutConsoleCommand.getKey(), logoutConsoleCommand);


        clientCommandMenu.setAllCommand(commandMap);


    }

}
