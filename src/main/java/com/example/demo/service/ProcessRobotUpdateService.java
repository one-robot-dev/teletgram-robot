package com.example.demo.service;

import com.alibaba.fastjson.JSON;
import com.example.demo.beans.response.UpdateResponse;
import com.example.demo.beans.robotupdate.*;
import com.example.demo.config.Configs;
import com.example.demo.db.dao.RecordModelMapper;
import com.example.demo.db.entity.RecordModel;
import com.example.demo.updatehandler.common.RobotUpdateHandler;
import com.example.demo.updatehandler.group.RobotGroupUpdatesHandler;
import com.example.demo.utils.DoRequestUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author yxs
 * @date 2023/04/26 16:06
 */
@Service
public class ProcessRobotUpdateService {

    private Logger logger = LoggerFactory.getLogger(ProcessRobotUpdateService.class);

    private long lastCheckTime = System.currentTimeMillis();

    @Autowired
    private Configs configs;

    @Autowired
    private RecordModelMapper recordModelMapper;

    @Autowired
    private GroupFunctionService groupFunctionService;

    @Autowired
    private List<RobotUpdateHandler> handlers;

    @Autowired
    private List<RobotGroupUpdatesHandler> groupHandlers;

    public void process() throws Exception {
        if (System.currentTimeMillis() - lastCheckTime < configs.checkInterval) {
            return;
        }
        lastCheckTime = System.currentTimeMillis();
        RecordModel recordModel = recordModelMapper.selectByPrimaryKey("lastUpdateId");
        String value = recordModel == null ? null : recordModel.getValue();
        long lastUpdateId = StringUtils.isBlank(value) ? 0 : Integer.parseInt(value), oriLastUpdateId = lastUpdateId;
        while (true) {
            List<Update> updates = getNewUpdate(lastUpdateId);
            if (updates == null || updates.isEmpty()) {
                return;
            }
            for (Update update : updates) {
                lastUpdateId = Math.max(lastUpdateId, update.getUpdateId());
                handlers.forEach(handler -> {
                    try {
                        handler.handle(update);
                    } catch (Exception e) {
                        logger.error("common handle update error,data:{}", update, e);
                    }
                });
                Long groupId = Optional.ofNullable(update.getMessage()).map(Message::getChat)
                        .filter(Chat::isSuperGroup).map(Chat::getId).orElse(null);
                if (groupId != null) {
                    groupHandlers.forEach(handler -> {
                        try {
                            String param = groupFunctionService.getParam(groupId, handler.getType());
                            handler.process(groupId, update, param);
                        } catch (Exception e) {
                            logger.error("group handle update error,data:{}", update, e);
                        }
                    });
                }
            }
            if (updates.size() < configs.limit) {
                break;
            }
        }
        if (lastUpdateId > oriLastUpdateId) {
            if (recordModel == null) {
                recordModel = new RecordModel();
                recordModel.setKey("lastUpdateId");
            }
            recordModel.setValue(String.valueOf(lastUpdateId));
            recordModelMapper.updateByPrimaryKey(recordModel);
        }
    }

    /**获取新数据*/
    private List<Update> getNewUpdate(long lastUpdateId) throws Exception {
        String param = lastUpdateId == 0 ? "" : ("&offset="+ (lastUpdateId + 1));
        ClientHttpRequest request = new OkHttp3ClientHttpRequestFactory().createRequest(URI.create(configs.getUpdatesUrl + param), HttpMethod.GET);
        String body = DoRequestUtil.request(request);
        UpdateResponse updateData = JSON.parseObject(body, UpdateResponse.class);
        return updateData.isOk() ? updateData.getResult() : Collections.emptyList();
    }
}
