/**
 * Created by 尼恩 at 疯狂创客圈
 */

package com.crazymakercircle.imClient.protoConverter;

import com.crazymakercircle.im.common.bean.User;
import com.crazymakercircle.im.common.bean.msg.ProtoMsg;
import com.crazymakercircle.imClient.session.ClientSession;


/**
 * 心跳消息 Converter
 */
public class HeartBeatMsgConverter extends BaseConverter {
    private final User user;

    public HeartBeatMsgConverter(User user, ClientSession session) {
        super(ProtoMsg.HeadType.HEART_BEAT, session);
        this.user = user;
    }

    public ProtoMsg.Message build() {

        ProtoMsg.Message.Builder outerBuilder = getOuterBuilder(-1);

        ProtoMsg.MessageHeartBeat.Builder inner =
                ProtoMsg.MessageHeartBeat.newBuilder()
                        .setSeq(0)
                        .setJson("{\"from\":\"client\"}")
                        .setUid(user.getUid());
        return outerBuilder.setHeartBeat(inner).build();
    }

}


