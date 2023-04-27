package com.example.demo.controller;

import com.example.demo.config.Configs;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RequestMapping("/config")
@RestController
public class ConfigController {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private Configs configs;

    @RequestMapping("/getAll")
    public Object getAll(){
        List<Map<String, Object>> result = jdbcTemplate.queryForList("select * from record");
        return objectMapper.createObjectNode().put("status", 0).putPOJO("data", result);
    }

    @RequestMapping("/setting")
    public Object setting(String key, String value){
        if (StringUtils.isAnyBlank(key, value)) {
            return objectMapper.createObjectNode().put("status", 300).putPOJO("msg", "key value param can't empty");
        }
        configs.updateConfig(key, value);
        return objectMapper.createObjectNode().put("status", 0).putPOJO("data", Collections.emptyMap());
    }
}
