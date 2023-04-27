package com.example.demo.service;

import com.alibaba.fastjson.JSON;
import com.example.demo.beans.response.GetChatResponse;
import com.example.demo.beans.robotupdate.Chat;
import com.example.demo.config.Configs;
import com.example.demo.db.dao.GroupInfoModelMapper;
import com.example.demo.db.entity.GroupInfoModel;
import com.example.demo.utils.DoRequestUtil;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yxs
 * @date 2023/04/26 18:44
 */
@Service
public class GroupInfoService {

    private static final Logger logger = LoggerFactory.getLogger(GroupInfoService.class);

    private Map<Long, GroupInfoModel> groupInfoMap = new ConcurrentHashMap<>();

    @Autowired
    private GroupInfoModelMapper groupInfoModelMapper;

    @Autowired
    private Configs configs;

    @PostConstruct
    private void init() {
        List<GroupInfoModel> models = groupInfoModelMapper.selectByExample(null);
        models.forEach(model -> groupInfoMap.put(model.getGroupId(), model));
    }

    /**获取组信息*/
    public GroupInfoModel getGroupInfo(Long groupId) {
        return groupInfoMap.computeIfAbsent(groupId, k -> {
            try {
                //调用接口查询
                String param = "?chat_id="+groupId;
                ClientHttpRequest request = new OkHttp3ClientHttpRequestFactory().createRequest(URI.create(configs.getChatUrl + param), HttpMethod.GET);
                String body = DoRequestUtil.request(request);
                GetChatResponse res = JSON.parseObject(body, GetChatResponse.class);
                if (res == null || !res.isOk() || res.getResult() == null) {
                    return null;
                }
                Chat chat = res.getResult();
                GroupInfoModel model = new GroupInfoModel();
                model.setGroupId(groupId);
                model.setTitle(chat.getTitle());
                long now = System.currentTimeMillis();
                model.setCreateTime(now);
                model.setUpdateTime(now);
                //入库
                groupInfoModelMapper.insert(model);
                return model;
            } catch (Exception e) {
                logger.error("get group info error, groupId:{}", groupId, e);
            }
            return null;
        });
    }


}
