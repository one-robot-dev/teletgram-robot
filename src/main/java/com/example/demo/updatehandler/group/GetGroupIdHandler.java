package com.example.demo.updatehandler.group;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.beans.robotupdate.Chat;
import com.example.demo.beans.robotupdate.Message;
import com.example.demo.beans.robotupdate.Update;
import com.example.demo.beans.robotupdate.User;
import com.example.demo.config.Configs;
import com.example.demo.utils.DoRequestUtil;
import com.example.demo.utils.iologic.IOLogicExecuteUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class GetGroupIdHandler implements RobotGroupUpdatesHandler<Object>{

    @Autowired
    private Configs configs;

    @Override
    public void handle(Update data, Object param) throws Exception {
        Message message = data.getMessage();
        if (message == null) {
            return;
        }
        Chat chat = message.getChat();
        if (chat == null || !chat.isSuperGroup()) {
            return;
        }
        User from = message.getFrom();
        if (from == null || from.isBot()) {
            return;
        }
        if (StringUtils.isBlank(message.getText())) {
            return;
        }
        if (!"/getGroupId".equals(message.getText())) {
            return;
        }
        //发送改名的提示
        IOLogicExecuteUtil.exeChatIOLogic(chat.getId(), () -> {
            String urlParam = String.format("?chat_id=%d&text=%s", chat.getId(), URLEncoder.encode("群id:" + chat.getId(), StandardCharsets.UTF_8));
            ClientHttpRequest request = new OkHttp3ClientHttpRequestFactory().createRequest(URI.create(configs.sendMsgUrl + urlParam), HttpMethod.GET);
            DoRequestUtil.request(request);
        });
    }

    @Override
    public GroupHandlerType getType() {
        return GroupHandlerType.GET_GROUP_ID;
    }

    @Override
    public Object parseParam(JSONObject param) {
        return null;
    }

    @Override
    public boolean isOpen(long groupId) {
        return true;
    }
}
