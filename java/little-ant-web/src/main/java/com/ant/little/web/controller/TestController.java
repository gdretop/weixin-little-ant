package com.ant.little.web.controller;

import com.ant.little.web.config.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping(value = "/api/test")
public class TestController {

    final Logger logger = LoggerFactory.getLogger(TestController.class);
    @Resource
    private HttpServletRequest httpServletRequest;

    public TestController() {
    }


    @PostMapping(value = "/output")
    @ResponseBody
    public ApiResponse output(@RequestBody Map<String, Object> requestMap) {

        httpServletRequest.getHeaderNames();
        Map<String, String> map = new HashMap<String, String>();
        Enumeration headerNames = httpServletRequest.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = httpServletRequest.getHeader(key);
            map.put(key, value);
        }
        logger.info("接收到测试请求:openId:{} source:{} data:{}", map.get("x-wx-openid"), map.get("x-wx-source"), requestMap);
        Map<String, Object> response = new HashMap<>();
        response.put("header", map);
        response.put("body", requestMap);
        return ApiResponse.ok(response);
    }

}