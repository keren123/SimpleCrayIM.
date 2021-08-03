package com.crazymakercircle.imClient.command;

import com.crazymakercircle.util.Logger;
import org.springframework.stereotype.Service;

import java.util.Scanner;


@Service("LogoutConsoleCommand")
public class LogoutConsoleCommand implements BaseCommand {
    public static final String KEY = "10";

    @Override
    public void exec(Scanner scanner) {
        Logger.cfo("退出命令执行成功");
    }


    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public String getTip() {
        return "退出";
    }

}
