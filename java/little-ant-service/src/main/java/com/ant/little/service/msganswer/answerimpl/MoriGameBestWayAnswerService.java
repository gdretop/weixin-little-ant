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
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
    private Cache<String, String> localCache = CacheBuilder.newBuilder()
            .concurrencyLevel(4)
            .initialCapacity(100)
            .expireAfterWrite(3, TimeUnit.DAYS)
            .maximumSize(5000)
            .build();
    private final static String FORMAT_INFO = "正确格式示例如下,输入5行,4个点坐标,换行分割点,逗号分割坐标:\n最短路径\n20,200\n101,101\n202,202\n303,303\n";

    @Override
    public boolean isMatch(WxSubMsgDTO wxSubMsgDTO) {
        if (!WxMsgTypeEnum.TEXT.getName().equals(wxSubMsgDTO.getMsgType())) {
            return false;
        }
        if (!wxSubMsgDTO.getContent().startsWith("最佳路线")) {
            return false;
        }
        String[] data = wxSubMsgDTO.getContent().split("\n");
        if (data.length != 5) {
            logger.error("格式不正确 {}", JSON.toJSONString(wxSubMsgDTO));
            throw new RuntimeException(FORMAT_INFO);
        }
        try {
            for (int i = 1; i < 4; i++) {
                data[i] = data[i].trim();
                Character split = DigitalUtil.findFirstNotDigit(data[i]);
                if (split == null || !(split.equals(' ') || split.equals(','))) {
                    logger.error("数字分隔符不正确 {}", JSON.toJSONString(wxSubMsgDTO));
                    throw new RuntimeException("坐标分隔符不正确请使用英文逗号(,)或空格");
                }
                int[] result = DigitalUtil.parseDigit(data[i], "" + split);
                if (result[0] < 1 || result[0] > 301 || result[1] < 1 || result[1] > 301) {
                    logger.error("坐标数字范围错误 {}", JSON.toJSONString(wxSubMsgDTO));
                    throw new RuntimeException("坐标数字范围1到301");
                }
            }
        } catch (Exception e) {
            logger.error("数据解析异常 {}", JSON.toJSONString(wxSubMsgDTO));
            if (e.getMessage().contains("坐标")) {
                throw new RuntimeException(e.getMessage());
            }
            throw new RuntimeException("数据格式有错." + FORMAT_INFO);
        }
        return true;
    }

    public Response<WxSubMsgResponseDTO> answer(WxSubMsgDTO wxSubMsgDTO) {
        String content = wxSubMsgDTO.getContent();
        String[] data = content.split("\n");
        List<String> newData = new ArrayList<>();
        for (int i = 1; i < data.length; i++) {
            data[i] = data[i].trim();
            data[i] = data[i].replace(' ', ',');
            newData.add(data[i]);
        }
        content = String.join("#", newData);
        String cacheResult = localCache.getIfPresent(content);
        if (cacheResult != null) {
            logger.info("找到缓存信息");
            WxSubMsgResponseDTO wxSubMsgResponseDTO = wxSubMsgDTO.toResponse();
            wxSubMsgResponseDTO.setMsgType(WxMsgTypeEnum.TEXT.getName());
            wxSubMsgResponseDTO.setContent(cacheResult);
            return Response.newSuccess(wxSubMsgResponseDTO);
        }
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
            String result = runTimeResponse.getResultString();
            result = result + "\n\n公众号:旺仔小蚂蚁";
            wxSubMsgResponseDTO.setContent(result);
            localCache.put(content, result);
            return Response.newSuccess(wxSubMsgResponseDTO);
        }
        return Response.newFailure(ResponseTemplateConstants.SERVER_ERROR, "");
    }
}
