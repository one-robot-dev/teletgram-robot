package com.example.demo.config;

import com.alibaba.fastjson.JSON;
import com.example.demo.db.dao.RecordModelMapper;
import com.example.demo.db.entity.RecordModel;
import com.example.demo.db.entity.RecordModelExample;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author yxs
 * @date 2023/04/23 17:04
 */
@Component
public class Configs {

    private static final Logger logger = LoggerFactory.getLogger(Configs.class);

    public final int limit = 100;

    public String getUpdatesUrl;

    public String sendMsgUrl;

    public String getChatUrl;

    @Value("${robot.token}")
    public String token;

    @Value("${robot.owner}")
    public String owner;

    public int checkInterval;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private RecordModelMapper recordModelMapper;

    @PostConstruct
    private void init() {
        if (StringUtils.isEmpty(token)) {
            logger.error("token:{} config error", token);
            throw new RuntimeException("token:{} config error");
        }
        //初始化通用配置
        RecordModelExample example = new RecordModelExample();
        example.createCriteria().andKeyIn(Arrays.asList("getUpdatesUrl", "sendMsgUrl", "getChatUrl", "checkInterval"));
        Map<String, String> config = recordModelMapper.selectByExample(example).stream().collect(Collectors.toMap(RecordModel::getKey, RecordModel::getValue));
        getUpdatesUrl = String.format(config.get("getUpdatesUrl"), token) + limit;
        sendMsgUrl = String.format(config.get("sendMsgUrl"), token);
        getChatUrl = String.format(config.get("getChatUrl"), token);
        String interval = config.get("checkInterval");
        checkInterval = Math.max(5, StringUtils.isNumeric(interval) ? Integer.parseInt(interval) : 10) * 1000;
    }

    /**
     * 更新配置
     * @param key       配置key
     * @param value     配置值
     */
    public void updateConfig(String key, String value) {
        switch (key) {
            case "getUpdatesUrl":
                getUpdatesUrl = String.format(value, token);
                break;
            case "sendMsgUrl":
                sendMsgUrl = String.format(value, token);
                break;
            case "getChatUrl":
                getChatUrl = String.format(value, token);
                break;
            case "checkInterval":
                checkInterval = Integer.parseInt(value);
                break;
            default:
                logger.warn("key:{}, value:{} no use ", key, value);
        }
        jdbcTemplate.execute(String.format("replace into record values('%s', '%s')", key, value));
    }
}
