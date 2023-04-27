package com.example.demo.service;

import com.example.demo.db.dao.GroupFunctionModelMapper;
import com.example.demo.db.entity.GroupFunctionModel;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yxs
 * @date 2023/04/26 17:57
 */
@Service
public class FunctionSettingService {

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

    public String getParam(long groupId, String type) {
        GroupFunctionModel model = groupFunctionMap.getOrDefault(groupId, Collections.emptyMap()).get(type);
        return model == null ? "" : model.getParam();
    }
}
