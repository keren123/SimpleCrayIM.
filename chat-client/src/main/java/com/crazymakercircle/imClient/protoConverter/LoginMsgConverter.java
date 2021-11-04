/**
 * Created by 尼恩 at 疯狂创客圈
 */

package com.crazymakercircle.imClient.protoConverter;

import com.crazymakercircle.im.common.bean.User;
import com.crazymakercircle.im.common.bean.msg.ProtoMsg;
import com.crazymakercircle.imClient.session.ClientSession;


/**
 * 登陆消息 Converter
 */
public class LoginMsgConverter extends BaseConverter {
    private final User user;

    public LoginMsgConverter(User user, ClientSession session) {
        super(ProtoMsg.HeadType.LOGIN_REQUEST, session);
        this.user = user;
    }

    public ProtoMsg.Message build() {

        ProtoMsg.Message.Builder baseBuilder = getMsgBuilder(-1);


           ProtoMsg.LoginRequest.Builder lb =
                ProtoMsg.LoginRequest.newBuilder()
                        .setDeviceId(user.getDevId())
                        .setPlatform(user.getPlatform().ordinal())
                        .setToken(user.getToken())
                        .setUid(user.getUid());

        ProtoMsg.Message requestMsg = baseBuilder.setLoginRequest(lb).build();

        return requestMsg;
    }


    public static ProtoMsg.Message buildLoginMsg(
            User user, ClientSession session) {
        LoginMsgConverter converter =
                new LoginMsgConverter(user, session);
        return converter.build();

    }


}


