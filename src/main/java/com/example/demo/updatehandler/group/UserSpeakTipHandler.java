package com.example.demo.updatehandler.group;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.beans.handlerparam.UserSpeakParam;
import com.example.demo.beans.robotupdate.Chat;
import com.example.demo.beans.robotupdate.Message;
import com.example.demo.beans.robotupdate.Update;
import com.example.demo.beans.robotupdate.User;
import com.example.demo.config.Configs;
import com.example.demo.service.GroupFunctionService;
import com.example.demo.utils.DoRequestUtil;
import com.example.demo.utils.iologic.IOLogicExecuteUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yxs
 * @date 2023/04/26 14:10
 */
@Service
public class UserSpeakTipHandler implements RobotGroupUpdatesHandler<UserSpeakParam> {

    /**群组对应的上次校验时间*/
    Map<Long, Long> lastCheckTime = new ConcurrentHashMap<>();

    @Autowired
    private Configs configs;

    @Autowired
    private GroupFunctionService groupFunctionService;

    @Override
    public void handle(Update data, UserSpeakParam param) {
        //获取发消息的人
        Message message = data.getMessage();
        if (message == null) {
            return;
        }
        Chat chat = message.getChat();
        if (chat == null || !chat.isSuperGroup()) {
            return;
        }
        User from = message.getFrom();
        if (from == null || from.getIsBot()) {
            return;
        }
        if (StringUtils.isBlank(message.getText())) {
            return;
        }
        if (param.getKefu().contains(from.getUserName())) {
            lastCheckTime.put(chat.getId(), System.currentTimeMillis());
            return;
        }
        if (System.currentTimeMillis() - lastCheckTime.getOrDefault(chat.getId(), 0L) < param.getCheckInterval() * 1000) {
            return;
        }
        //对配置的群进行提示
        param.getReceiveId().forEach(receiveId -> {
            IOLogicExecuteUtil.exeChatIOLogic(receiveId, () -> {
                String tip = param.getTipMsg()
                        .replace("{groupTitle}", StringUtils.defaultString(chat.getTitle()))
                        .replace("{groupUserName}", StringUtils.defaultString(chat.getUsername()))
                        .replace("{userFirstName}", StringUtils.defaultString(from.getFirstName()))
                        .replace("{userLastName}", StringUtils.defaultString(from.getLastName()))
                        .replace("{userName}", StringUtils.defaultString(from.getUserName()))
                        .replace("{userId}", String.valueOf(from.getId()));
                String msg = URLEncoder.encode(tip, StandardCharsets.UTF_8);
                String urlParam = String.format("?chat_id=%d&text=%s", receiveId, msg);
                ClientHttpRequest request = new OkHttp3ClientHttpRequestFactory().createRequest(URI.create(configs.sendMsgUrl + urlParam), HttpMethod.GET);
                DoRequestUtil.request(request);
            });
        });

        lastCheckTime.put(chat.getId(), System.currentTimeMillis());
    }

    @Override
    public GroupHandlerType getType() {
        return GroupHandlerType.USER_SPEAK_TIP;
    }

    @Override
    public UserSpeakParam parseParam(JSONObject param) {
        return param.toJavaObject(UserSpeakParam.class);
    }

    @Override
    public boolean isOpen(long groupId) {
        return groupFunctionService.isFunctionOpen(groupId, getType().type());
    }
}
