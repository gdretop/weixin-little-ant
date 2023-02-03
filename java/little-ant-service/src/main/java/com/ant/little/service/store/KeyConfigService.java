package com.ant.little.service.store;

import com.alibaba.fastjson.JSON;
import com.ant.little.common.model.Response;
import com.ant.little.core.config.EnvConfig;
import com.ant.little.core.dao.KeyConfigDOMapper;
import com.ant.little.core.domain.KeyConfigDO;
import com.ant.little.core.domain.KeyConfigDOExample;
import com.ant.little.model.dto.KeyConfigDTO;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author: little-ant
 * 描述:
 * @date: 2022/12/26
 * @Version 1.0
 **/
@Service
public class KeyConfigService {
    private Logger logger = LoggerFactory.getLogger(KeyConfigService.class);
    @Resource
    private KeyConfigDOMapper keyConfigDOMapper;
    @Autowired
    private EnvConfig envConfig;

    private Cache<String, CacheObj> localCache = CacheBuilder.newBuilder()
            .concurrencyLevel(4)
            .initialCapacity(100)
            .expireAfterWrite(3, TimeUnit.DAYS)
//            .expireAfterAccess(3, TimeUnit.DAYS)
            .maximumSize(5000)
            .build();

    public Response<KeyConfigDTO> insert(KeyConfigDTO keyConfigDTO) {
        String env = envConfig.getCurEnv();
        keyConfigDTO.setEnv(env);

        KeyConfigDO keyConfigDO = dto2DO(keyConfigDTO);
        int effect = keyConfigDOMapper.insertSelective(keyConfigDO);
        if (effect == 1) {
            localCache.invalidateAll();
            return Response.newSuccess(keyConfigDTO);
        } else {
            logger.error("插入信息失败 {}", JSON.toJSONString(keyConfigDTO));
            return Response.newFailure("写入失败", "");
        }
    }

    public Response<KeyConfigDTO> getKey(String type, String key) {
        String cacheKey = String.format("%s#%s", type, key);
        CacheObj cacheObj = localCache.getIfPresent(cacheKey);
        if (cacheObj != null) {
            if (cacheObj.keyConfigDTO != null) {
                return Response.newSuccess(cacheObj.keyConfigDTO);
            }
            return Response.newSuccess(null);
        }
        KeyConfigDOExample example = new KeyConfigDOExample();
        example.createCriteria().andTypeEqualTo(type).andConfigKeyEqualTo(key).andEnvEqualTo(envConfig.getCurEnv());
        example.setOrderByClause(" gmt_create desc limit 1");
        List<KeyConfigDO> result = keyConfigDOMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(result)) {
            localCache.put(cacheKey, new CacheObj());
            return Response.newSuccess(null);
        }
        KeyConfigDTO keyConfigDTO = do2Dto(result.get(0));
        CacheObj cacheObj1 = new CacheObj();
        cacheObj1.keyConfigDTO = keyConfigDTO;
        localCache.put(cacheKey, cacheObj1);
        return Response.newSuccess(keyConfigDTO);
    }

    private static class CacheObj {
        KeyConfigDTO keyConfigDTO = null;
    }

    private KeyConfigDTO do2Dto(KeyConfigDO keyConfigDO) {
        KeyConfigDTO keyConfigDTO = new KeyConfigDTO();
        keyConfigDTO.setId(keyConfigDO.getId());
        keyConfigDTO.setGmtCreate(keyConfigDO.getGmtCreate());
        keyConfigDTO.setGmtModifier(keyConfigDO.getGmtModifier());
        keyConfigDTO.setEnv(keyConfigDO.getEnv());
        keyConfigDTO.setAppid(keyConfigDO.getAppid());
        keyConfigDTO.setOpenId(keyConfigDO.getOpenId());
        keyConfigDTO.setType(keyConfigDO.getType());
        keyConfigDTO.setKey(keyConfigDO.getConfigKey());
        keyConfigDTO.setValue(keyConfigDO.getValue());
        return keyConfigDTO;
    }

    private KeyConfigDO dto2DO(KeyConfigDTO keyConfigDTO) {
        KeyConfigDO keyConfigDO = new KeyConfigDO();
        keyConfigDO.setEnv(keyConfigDTO.getEnv());
        keyConfigDO.setAppid(keyConfigDTO.getAppid());
        keyConfigDO.setOpenId(keyConfigDTO.getOpenId());
        keyConfigDO.setType(keyConfigDTO.getType());
        keyConfigDO.setConfigKey(keyConfigDTO.getKey());
        keyConfigDO.setValue(keyConfigDTO.getValue());
        return keyConfigDO;
    }
}
