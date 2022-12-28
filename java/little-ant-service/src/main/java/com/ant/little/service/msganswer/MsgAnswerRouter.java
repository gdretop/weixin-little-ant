package com.ant.little.service.msganswer;

import com.alibaba.fastjson.JSON;
import com.ant.little.common.constents.ResponseTemplateConstants;
import com.ant.little.common.model.Response;
import com.ant.little.model.dto.RequestLogDTO;
import com.ant.little.model.dto.WxSubMsgDTO;
import com.ant.little.model.dto.WxSubMsgResponseDTO;
import com.ant.little.model.dto.WxUserDTO;
import com.ant.little.service.msganswer.answerimpl.MoriGameBestWayAnswerService;
import com.ant.little.service.msganswer.answerimpl.MoriGameFindPathAnswerService;
import com.ant.little.service.store.RequestLogService;
import com.ant.little.service.store.WxUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: little-ant
 * 描述:
 * @date: 2022/12/27
 * @Version 1.0
 **/
@Service
public class MsgAnswerRouter {
    private final Logger logger = LoggerFactory.getLogger(MsgAnswerRouter.class);
    private List<MsgAnswerBaseService> msgAnswerBaseServiceList = new ArrayList<>();
    @Autowired
    private RequestLogService requestLogService;
    @Autowired
    private WxUserService wxUserService;

    public MsgAnswerRouter(@Autowired MoriGameBestWayAnswerService moriGameWayPathAnswerService,
                           @Autowired MoriGameFindPathAnswerService moriGameFindPathAnswerService
    ) {
        msgAnswerBaseServiceList.add(moriGameWayPathAnswerService);
        msgAnswerBaseServiceList.add(moriGameFindPathAnswerService);
    }

    public Response<WxSubMsgResponseDTO> process(WxSubMsgDTO wxSubMsgDTO) {
        WxUserDTO wxUserDTO = new WxUserDTO();
        try {
            wxUserDTO.setOpenId(wxSubMsgDTO.getWxOpenId());
            wxUserDTO.setAppid(wxSubMsgDTO.getWxAppid());
            wxUserDTO.setLastLocation(wxSubMsgDTO.getRealIp());
            wxUserDTO.setLastSource(wxSubMsgDTO.getWxSource());
            wxUserService.upsert(wxUserDTO);
        } catch (Exception e) {
            logger.error("保存用户信息失败 {} {}", JSON.toJSONString(wxUserDTO), e, e);
        }

        RequestLogDTO requestLogDTO = new RequestLogDTO();
        requestLogDTO.setOpenId(wxSubMsgDTO.getWxOpenId());
        requestLogDTO.setAppid(wxSubMsgDTO.getWxAppid());
        requestLogDTO.setRequestInfo(JSON.toJSONString(wxSubMsgDTO));
        Response<WxSubMsgResponseDTO> responseAnswer = null;
        try {
            for (MsgAnswerBaseService service : msgAnswerBaseServiceList) {
                if (service.isMatch(wxSubMsgDTO)) {
                    responseAnswer = service.answer(wxSubMsgDTO);
                    return responseAnswer;
                }
            }
            responseAnswer = Response.newFailure(ResponseTemplateConstants.CAN_NOT_FIND_ANSWER_SERVICE, "");
            return responseAnswer;
        } finally {
            try {
                logger.info("保存记录日志");
                requestLogDTO.setResponseInfo(JSON.toJSONString(responseAnswer));
                requestLogService.insert(requestLogDTO);
            } catch (Exception e) {
                logger.error("保存日志信息失败 {} {}", JSON.toJSONString(requestLogDTO), e, e);
            }
        }
    }
}
