package com.example.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.example.demo.beans.ApiResponse;
import com.example.demo.db.entity.GroupFunctionModel;
import com.example.demo.service.GroupFunctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;


@RestController
@RequestMapping("/groupFunction")
public class GroupFunctionController {

    @Autowired
    private GroupFunctionService groupFunctionService;

    @RequestMapping("/getAllGroup")
    public ApiResponse getAllGroup() {
        return new ApiResponse("", 0, groupFunctionService.getAllGroup());
    }

    @RequestMapping("/getByGroupId")
    public ApiResponse getByGroupId(Long groupId) {
        if (groupId == null) {
            return new ApiResponse("需要groupId参数", 0, null);
        }
        return new ApiResponse("ok", 0, groupFunctionService.getByGroupId(groupId));
    }

    @RequestMapping("/cloneFromOld")
    public ApiResponse cloneFromOld(Long newGroupId, Long fromGroupId) {
        if (newGroupId == null) {
            return new ApiResponse("需要newGroupId参数", 0, null);
        }
        if (fromGroupId == null) {
            return new ApiResponse("需要fromGroupId参数", 0, null);
        }
        boolean exists = groupFunctionService.isGroupExist(newGroupId);
        if (exists) {
            return new ApiResponse("群组已存在,请先删除在克隆", 500, null);
        }
        exists = groupFunctionService.isGroupExist(fromGroupId);
        if (!exists) {
            return new ApiResponse("模版群组不存在，请检查", 500, null);
        }
        groupFunctionService.addFromOld(newGroupId, fromGroupId);
        return new ApiResponse("ok", 0, groupFunctionService.getByGroupId(newGroupId));
    }

    @RequestMapping("/save")
    public ApiResponse save(@RequestBody List<GroupFunctionModel> functions) {
        if (functions == null || functions.isEmpty()) {
            return new ApiResponse("需要functions对象参数", 0, null);
        }
        for (GroupFunctionModel function : functions) {
            if (function.getGroupId() == null || function.getType() == null) {
                return new ApiResponse("function对象的groupId和type参数都不能为空", 0, null);
            }
        }
        for (GroupFunctionModel function : functions) {
            GroupFunctionModel functionModel = groupFunctionService.save(function);
        }
        return new ApiResponse("ok", 0, functions);
    }

    @RequestMapping("/deleteOne")
    public ApiResponse deleteOne(Long groupId, String type) {
        if (groupId == null) {
            return new ApiResponse("需要groupId参数", 0, null);
        }
        if (type == null) {
            return new ApiResponse("需要type参数", 0, null);
        }
        groupFunctionService.deleteOne(groupId, type);
        return new ApiResponse("ok", 0, null);
    }

    @RequestMapping("/deleteById")
    public ApiResponse deleteById(Long groupId) {
        if (groupId == null) {
            return new ApiResponse("需要groupId参数", 0, null);
        }
        groupFunctionService.deleteById(groupId);
        return new ApiResponse("ok", 0, null);
    }

    public ApiResponse executeCmd(String subCmd, JSONObject param) {
        switch (subCmd) {
            case "getAllGroup":
                return getAllGroup();
            case "getByGroupId":
                return getByGroupId(param.getLong("groupId"));
            case "cloneFromOld":
                return cloneFromOld(param.getLong("newGroupId"), param.getLong("fromGroupId"));
            case "save":
                List<GroupFunctionModel> models;
                try {
                    models = param.getObject("functions", new TypeReference<List<GroupFunctionModel>>(){});
                } catch (Exception e) {
                    return new ApiResponse("functions必须是一个数组对象", 500, null);
                }
                return save(models);
            case "deleteOne":
                return deleteOne(param.getLong("groupId"), param.getString("type"));
            case "deleteById":
                return deleteById(param.getLong("groupId"));
            default:
                return new ApiResponse("没有找到对应的subCmd",400, null);
        }
    }
}
