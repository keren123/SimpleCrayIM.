package com.crazymakercircle.imClient.sender;

import com.crazymakercircle.im.common.bean.msg.ProtoMsg;
import com.crazymakercircle.imClient.protoConverter.LoginMsgConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service("loginSender")
public class LoginSender extends BaseSender {
    public void sendLoginMsg() {
        if (!isConnected()) { log.info("还没有建立连接!");return; }

        log.info("构造登录消息");

        ProtoMsg.Message message =
                LoginMsgConverter.build(getUser(), getSession());
        log.info("发送登录消息");
        super.sendMsg(message);
    }


}
