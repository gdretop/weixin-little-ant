package com.ant.little.web.vo;

/**
 * @author: little-ant
 * 描述:
 * @date: 2022/12/27
 * @Version 1.0
 **/
public class WxMsgVO {
    public String ToUserName;
    public String FromUserName;
    public long CreateTime;
    public String MsgType;
    public String Content;
    public long MsgId;

    public WxMsgVO transDirection() {
        WxMsgVO wxMsgVO = new WxMsgVO();
        wxMsgVO.ToUserName = this.FromUserName;
        wxMsgVO.FromUserName = this.ToUserName;
        wxMsgVO.CreateTime = this.CreateTime;
        wxMsgVO.MsgType = this.MsgType;
        wxMsgVO.Content = this.Content;
        wxMsgVO.MsgId = this.MsgId;
        return wxMsgVO;
    }


}
