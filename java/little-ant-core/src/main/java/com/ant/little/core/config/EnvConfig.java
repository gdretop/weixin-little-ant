package com.ant.little.core.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class EnvConfig {
    Logger logger = LoggerFactory.getLogger(EnvConfig.class);
    @Value(value = "${CUR_ENV}")
    private String curEnv;
    @Autowired
    private Environment environment;

    @PostConstruct
    public void output() {
        logger.info("环境变量CUR_ENV:{}", this.curEnv);
    }

    public String getCurEnv() {
        return curEnv;
    }
}
