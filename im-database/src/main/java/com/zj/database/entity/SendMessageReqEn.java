package com.zj.database.entity;

import java.util.List;

public class SendMessageReqEn {

    public Long groupId;

    /**
     * 回复的信息id
     */
    public Long msgId;

    /**
     * 客户端信息id
     */
    public String clientMsgId;

    /**
     * text/img/audio/video/cc_video
     */
    public String msgType;


    public String content;

    /**
     * 文件
     */
    public List<String> file;


    /**
     * 是否公开消息 false否 tru是
     */
    public boolean isPublic;

    /**
     * 钻石数
     */
    public Integer diamondNum;
}
