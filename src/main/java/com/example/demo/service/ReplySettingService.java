package com.example.demo.service;

import com.example.demo.db.dao.GroupReplyModelMapper;
import com.example.demo.db.entity.GroupReplyModel;
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
public class ReplySettingService {

    private Map<Long, Map<String, GroupReplyModel>> groupReplyMap = new ConcurrentHashMap<>();

    @Autowired
    private GroupReplyModelMapper replyModelMapper;

    @PostConstruct
    public void init() {
        List<GroupReplyModel> models = replyModelMapper.selectByExample(null);
        models.forEach(model ->
            groupReplyMap.computeIfAbsent(model.getGroupId(), k -> new HashMap<>()).put(model.getKeyWord(), model)
        );
    }

    public String getReply(long groupId, String keyword) {
        GroupReplyModel model = groupReplyMap.getOrDefault(groupId, Collections.emptyMap()).get(keyword);
        return model == null ? null : model.getReply();
    }

}
