package com.ant.little.service;

import com.alibaba.fastjson.JSON;
import com.ant.little.common.model.Response;
import com.ant.little.core.config.EnvConfig;
import com.ant.little.core.dao.RequestLogDOMapper;
import com.ant.little.core.domain.RequestLogDO;
import com.ant.little.model.dto.RequestLogDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author: little-ant
 * 描述:
 * @date: 2022/12/26
 * @Version 1.0
 **/
public class RequestLogService {
    private Logger logger = LoggerFactory.getLogger(RequestLogService.class);
    @Autowired
    private RequestLogDOMapper requestLogDOMapper;
    @Autowired
    private EnvConfig envConfig;

    public Response<RequestLogDTO> insert(RequestLogDTO requestLogDTO) {
        String env = envConfig.getCurEnv();
        requestLogDTO.setEnv(env);
        RequestLogDO requestLogDO = dto2DO(requestLogDTO);
        int effect = requestLogDOMapper.insertSelective(requestLogDO);
        requestLogDTO.setId(requestLogDO.getId());
        if (effect == 1) {
            return Response.newSuccess(requestLogDTO);
        } else {
            logger.error("插入信息失败 {}", JSON.toJSONString(requestLogDTO));
            return Response.newFailure("写入失败", "");
        }
    }

    private RequestLogDO dto2DO(RequestLogDTO requestLogDTO) {
        RequestLogDO requestLogDO = new RequestLogDO();
        requestLogDO.setOpenId(requestLogDTO.getOpenId());
        requestLogDO.setEnv(requestLogDTO.getEnv());
        requestLogDO.setAppid(requestLogDTO.getAppid());
        requestLogDO.setRequestInfo(requestLogDTO.getRequestInfo());
        requestLogDO.setResponseInfo(requestLogDTO.getResponseInfo());
        return requestLogDO;
    }
}
