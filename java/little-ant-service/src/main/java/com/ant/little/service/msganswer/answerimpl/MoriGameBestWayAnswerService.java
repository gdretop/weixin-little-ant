package com.ant.little.service.msganswer.answerimpl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ant.little.common.constents.ResponseTemplateConstants;
import com.ant.little.common.constents.WxMsgTypeEnum;
import com.ant.little.common.model.Response;
import com.ant.little.common.model.RunTimeResponse;
import com.ant.little.common.util.DigitalUtil;
import com.ant.little.common.util.RuntimeUtil;
import com.ant.little.core.config.EnvConfig;
import com.ant.little.model.dto.WxSubMsgDTO;
import com.ant.little.model.dto.WxSubMsgResponseDTO;
import com.ant.little.service.msganswer.MsgAnswerBaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author: little-ant
 * 描述:
 * @date: 2022/12/27
 * @Version 1.0
 **/
@Component
public class MoriGameBestWayAnswerService implements MsgAnswerBaseService {

    private final Logger logger = LoggerFactory.getLogger(MoriGameBestWayAnswerService.class);
    @Autowired
    private EnvConfig envConfig;

    @Override
    public boolean isMatch(WxSubMsgDTO wxSubMsgDTO) {
        if (!WxMsgTypeEnum.TEXT.getName().equals(wxSubMsgDTO.getMsgType())) {
            return false;
        }
        String[] data = wxSubMsgDTO.getContent().split("\n");
        if (data.length == 5) {
            if (!"最佳路线".equals(data[0])) {
                return false;
            }
            try {
                for (int i = 1; i < 4; i++) {
                    if (data[i].indexOf(',') == -1) {
                        logger.error("数字分隔符不正确 {}", JSON.toJSONString(wxSubMsgDTO));
                        throw new RuntimeException("数字分隔符不正确请使用英文逗号(,)");
                    }
                    int[] result = DigitalUtil.parseDigit(data[i], ",");
                    if (result[0] < 1 || result[0] > 301 || result[1] < 1 || result[1] > 301) {
                        logger.error("坐标数字范围错误 {}", JSON.toJSONString(wxSubMsgDTO));
                        throw new RuntimeException("坐标数字范围1到301");
                    }
                }
            } catch (Exception e) {
                logger.error("数据解析异常 {}", JSON.toJSONString(wxSubMsgDTO));
                throw new RuntimeException("数据解析异常请参考相关文档重新输入");
            }
            return true;
        }
        return false;
    }

    public Response<WxSubMsgResponseDTO> answer(WxSubMsgDTO wxSubMsgDTO) {
        String content = wxSubMsgDTO.getContent();
        content = content.substring("最佳路线\n".length());
        content = content.replace('\n', '#');
        String command = String.format("%s %s/mori_map_best_way_service.py %s", envConfig.getPythonInc(), envConfig.getPythonCodeDir(), content);
        Response<List<String>> response = RuntimeUtil.synCall(command);
        if (response.isFailed() || CollectionUtils.isEmpty(response.getData())) {
            logger.error("计算最佳路线失败 {}", response.getErrMsg());
            return Response.newFailure(ResponseTemplateConstants.SERVER_ERROR, "");
        }
        List<String> output = response.getData();
        String resultString = output.get(output.size() - 1);
        RunTimeResponse runTimeResponse = JSONObject.parseObject(resultString, RunTimeResponse.class);
        if (runTimeResponse.getResultCode() == 0) {
            WxSubMsgResponseDTO wxSubMsgResponseDTO = wxSubMsgDTO.toResponse();
            wxSubMsgResponseDTO.setMsgType(WxMsgTypeEnum.TEXT.getName());
            wxSubMsgResponseDTO.setContent(runTimeResponse.getResultString());
            return Response.newSuccess(wxSubMsgResponseDTO);
        }
        return Response.newFailure(ResponseTemplateConstants.SERVER_ERROR, "");
    }
}
