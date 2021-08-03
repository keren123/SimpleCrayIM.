package com.crazymakercircle.imClient.command;

import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

@Data
@Service("ClientCommandMenu")
public class ClientCommandMenu implements BaseCommand {

    public static final String KEY = "0";

    private String allCommandsShow;
    private String commandInput;

    @Override
    public void exec(Scanner scanner) {

        System.err.println("请输入某个操作指令："+allCommandsShow);
        //  获取第一个指令
        commandInput = scanner.next();


    }


    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public String getTip() {
        return "show 所有命令";
    }


    public void setAllCommand(Map<String, BaseCommand> commandMap) {


        Set<Map.Entry<String, BaseCommand>> entrys =
                commandMap.entrySet();
        Iterator<Map.Entry<String, BaseCommand>> iterator =
                entrys.iterator();

        StringBuilder menus = new StringBuilder();
        menus.append("[menu] ");
        while (iterator.hasNext()) {
            BaseCommand next = iterator.next().getValue();

            menus.append(next.getKey())
                    .append("->")
                    .append(next.getTip())
                    .append(" | ");

        }
        allCommandsShow = menus.toString();
    }
}
