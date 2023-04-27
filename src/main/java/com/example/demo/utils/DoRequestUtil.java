package com.example.demo.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author yxs
 * @date 2023/04/27 17:02
 */
public class DoRequestUtil {

    private static final Logger logger = LoggerFactory.getLogger(DoRequestUtil.class);

    public static String request(ClientHttpRequest request) throws IOException {
        ClientHttpResponse response = request.execute();
        String body = new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8);
        response.close();
        if (StringUtils.contains(body, "\"ok\":false")) {
            logger.error("request:{} fail, body:{}", request.getURI(), body);
        }
        return body;
    }
}
