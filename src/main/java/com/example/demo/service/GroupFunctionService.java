package com.example.demo.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.db.dao.GroupFunctionModelMapper;
import com.example.demo.db.entity.GroupFunctionModel;
import com.example.demo.db.entity.GroupFunctionModelExample;
import com.example.demo.db.entity.GroupFunctionModelKey;
import com.example.demo.updatehandler.group.GroupHandlerType;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yxs
 * @date 2023/04/26 17:57
 */
@Service
public class GroupFunctionService {

    private Map<Long, Map<String, GroupFunctionModel>> groupFunctionMap = new ConcurrentHashMap<>();

    @Autowired
    private GroupFunctionModelMapper functionModelMapper;

    @PostConstruct
    public void init() {
        List<GroupFunctionModel> models = functionModelMapper.selectByExample(null);
        models.forEach(model -> {
            groupFunctionMap.computeIfAbsent(model.getGroupId(), groupId -> new HashMap<>())
                    .put(model.getType(), model);
        });
    }

    public boolean isFunctionOpen(long groupId, String type) {
        GroupFunctionModel model = groupFunctionMap.getOrDefault(groupId, Collections.emptyMap()).get(type);
        return model != null && model.getStatus() == 1;
    }

    public JSONObject getParam(long groupId, GroupHandlerType type) {
        GroupFunctionModel model = groupFunctionMap.getOrDefault(groupId, Collections.emptyMap()).get(type.type());
        return model == null ? new JSONObject() : model.getParam();
    }

    public Collection<Long> getAllGroup() {
        List<Long> allGroup = new ArrayList<>();
        groupFunctionMap.forEach((k, v) ->{
            if (!v.isEmpty()) {
                allGroup.add(k);
            }
        });
        return allGroup;
    }

    public Collection<GroupFunctionModel> getByGroupId(long groupId) {
        return groupFunctionMap.getOrDefault(groupId, Collections.emptyMap()).values();
    }

    public boolean isGroupExist(long groupId) {
        return !groupFunctionMap.getOrDefault(groupId, Collections.emptyMap()).isEmpty();
    }

    public void addFromOld(long newGroupId, long fromGroupId) {
        long now = System.currentTimeMillis();
        groupFunctionMap.getOrDefault(fromGroupId, Collections.emptyMap()).values().forEach(model -> {
            GroupFunctionModel newModel = JSON.parseObject(JSON.toJSONString(model), GroupFunctionModel.class);
            newModel.setGroupId(newGroupId);
            newModel.setCreateTime(now);
            newModel.setUpdateTime(now);
            functionModelMapper.insert(newModel);
            groupFunctionMap.computeIfAbsent(newGroupId, k -> new HashMap<>()).put(newModel.getType(), newModel);
        });
    }

    public GroupFunctionModel save(GroupFunctionModel function) {
        boolean exists = groupFunctionMap.getOrDefault(function.getGroupId(), Collections.emptyMap()).containsKey(function.getType());
        long now = System.currentTimeMillis();
        function.setUpdateTime(now);
        if (!exists) {
            function.setCreateTime(now);
            functionModelMapper.insert(function);
        } else {
            functionModelMapper.updateByPrimaryKey(function);
        }
        groupFunctionMap.computeIfAbsent(function.getGroupId(), k -> new HashMap<>()).put(function.getType(), function);
        return function;
    }

    public void deleteOne(long groupId, String type) {
        GroupFunctionModelKey key = new GroupFunctionModelKey();
        key.setGroupId(groupId);
        key.setType(type);
        functionModelMapper.deleteByPrimaryKey(key);
        Optional.ofNullable(groupFunctionMap.get(groupId)).ifPresent(map -> map.remove(type));
    }

    public void deleteById(long groupId) {
        GroupFunctionModelExample example = new GroupFunctionModelExample();
        example.createCriteria().andGroupIdEqualTo(groupId);
        functionModelMapper.deleteByExample(example);
        groupFunctionMap.remove(groupId);
    }
}
