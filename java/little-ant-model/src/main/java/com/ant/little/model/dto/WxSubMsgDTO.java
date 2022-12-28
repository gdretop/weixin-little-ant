package com.ant.little.model.dto;

/**
 * @author: little-ant
 * 描述:
 * @date: 2022/12/27
 * @Version 1.0
 **/
public class WxSubMsgDTO {
    private String toUserName;
    private String fromUserName;
    private long createTime;
    private String msgType;
    private String content;
    private String wxOpenId;
    private String wxSource;
    private String realIp;
    private String wxAppid;

    public WxSubMsgResponseDTO toResponse() {
        WxSubMsgResponseDTO wxSubMsgResponseDTO = new WxSubMsgResponseDTO();
        wxSubMsgResponseDTO.setToUserName(this.getFromUserName());
        wxSubMsgResponseDTO.setFromUserName(this.getToUserName());
        wxSubMsgResponseDTO.setCreateTime(this.getCreateTime());
        return wxSubMsgResponseDTO;
    }

    public String getToUserName() {
        return toUserName;
    }

    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
    }

    public String getFromUserName() {
        return fromUserName;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getWxOpenId() {
        return wxOpenId;
    }

    public void setWxOpenId(String wxOpenId) {
        this.wxOpenId = wxOpenId;
    }

    public String getWxSource() {
        return wxSource;
    }

    public void setWxSource(String wxSource) {
        this.wxSource = wxSource;
    }

    public String getRealIp() {
        return realIp;
    }

    public void setRealIp(String realIp) {
        this.realIp = realIp;
    }

    public String getWxAppid() {
        return wxAppid;
    }

    public void setWxAppid(String wxAppid) {
        this.wxAppid = wxAppid;
    }
}
