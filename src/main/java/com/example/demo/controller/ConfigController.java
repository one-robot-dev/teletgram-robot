package com.example.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.beans.ApiResponse;
import com.example.demo.config.Configs;
import com.example.demo.db.dao.RecordModelMapper;
import com.example.demo.db.entity.RecordModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/config")
@RestController
public class ConfigController {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private RecordModelMapper recordModelMapper;

    @Autowired
    private Configs configs;

    @RequestMapping("/getAll")
    public ApiResponse getAll(){
        List<RecordModel> models = recordModelMapper.selectByExample(null);
        return new ApiResponse("", 0, models);
    }

    @RequestMapping("/set")
    public ApiResponse set(String key, String value){
        if (StringUtils.isAnyBlank(key, value)) {
            return new ApiResponse("key value都不能为空", 300, null);
        }
        configs.updateConfig(key, value);
        return new ApiResponse("设置成功", 0, null);
    }

    public ApiResponse executeCmd(String subCmd, JSONObject param) {
        switch (subCmd) {
            case "getAll":
                return getAll();
            case "set":
                return set(param.getString("key"), param.getString("value"));
            default:
                return new ApiResponse("没有找到对应的subCmd", 400, null);
        }
    }

}
