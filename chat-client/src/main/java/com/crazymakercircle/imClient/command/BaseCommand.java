package com.crazymakercircle.imClient.command;

import java.util.Scanner;

public interface BaseCommand {

    //获取命令的key
    String getKey();

    //获取命令的提示信息
    String getTip();

    //从控制台提取 业务数据
    void exec(Scanner scanner);

}
