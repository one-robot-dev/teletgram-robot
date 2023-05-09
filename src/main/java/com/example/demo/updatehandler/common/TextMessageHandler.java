package com.example.demo.updatehandler.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.beans.ApiResponse;
import com.example.demo.beans.robotupdate.Chat;
import com.example.demo.beans.robotupdate.Message;
import com.example.demo.beans.robotupdate.Update;
import com.example.demo.beans.robotupdate.User;
import com.example.demo.config.Configs;
import com.example.demo.controller.ConfigController;
import com.example.demo.controller.GroupFunctionController;
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
import java.util.Arrays;
import java.util.Optional;

/**
 * @author yxs
 * @date 2023/04/26 21:25
 */
@Service
public class TextMessageHandler implements RobotUpdateHandler {

    @Autowired
    private GroupFunctionController groupFunctionController;

    @Autowired
    private ConfigController configController;

    @Autowired
    private Configs configs;

    @Override
    public void handle(Update data) throws Exception {
        Message message = data.getMessage();
        if (message == null) {
            return;
        }
        Chat chat = message.getChat();
        if (chat == null || !chat.isPrivate()) {
            return;
        }
        User from = message.getFrom();
        if (from == null || from.isBot()) {
            return;
        }
        processText(chat, from, message.getText());
    }

    private void processText(Chat chat, User from, String text) {
        if (StringUtils.isBlank(text)) {
            return;
        }
        if (text.equals("/getSelfId")) {
            IOLogicExecuteUtil.exeChatIOLogic(chat.getId(), () -> {
                String msg = URLEncoder.encode("你的id是：" + from.getId(), StandardCharsets.UTF_8);
                String urlParam = String.format("?chat_id=%d&text=%s", chat.getId(), msg);
                ClientHttpRequest request = new OkHttp3ClientHttpRequestFactory().createRequest(URI.create(configs.sendMsgUrl + urlParam), HttpMethod.GET);
                DoRequestUtil.request(request);
            });
            return;
        }
        if (!JSON.isValidObject(text)) {
            return;
        }
        JSONObject info = JSON.parseObject(text);
        String masterCmd = info.getString("masterCmd");
        String subCmd = info.getString("subCmd");
        if (StringUtils.isNotEmpty(masterCmd) && StringUtils.isNotEmpty(subCmd)) {
            configSet(from, chat, masterCmd, subCmd, info.getJSONObject("param"));
        }
    }

    private void configSet(User from, Chat chat, String masterCmd, String subCmd, JSONObject param) {
        if (StringUtils.isNotEmpty(configs.owner)) {
            boolean isAdmin = Arrays.asList(configs.owner.split(",")).contains(String.valueOf(from.getId()));
            if (!isAdmin) {
                IOLogicExecuteUtil.exeChatIOLogic(chat.getId(), () -> {
                    String msg = URLEncoder.encode("非管理员", StandardCharsets.UTF_8);
                    String urlParam = String.format("?chat_id=%d&text=%s", chat.getId(), msg);
                    ClientHttpRequest request = new OkHttp3ClientHttpRequestFactory().createRequest(URI.create(configs.sendMsgUrl + urlParam), HttpMethod.GET);
                    DoRequestUtil.request(request);
                });
                return;
            }
        }
        ApiResponse[] response = {null};
        param = Optional.ofNullable(param).orElse(new JSONObject());
        if ("groupFunction".equals(masterCmd)) {
            response[0] = groupFunctionController.executeCmd(subCmd, param);
        } else if ("config".equals(masterCmd)) {
            response[0] = configController.executeCmd(subCmd, param);
        }

        if (response[0] != null) {
            IOLogicExecuteUtil.exeChatIOLogic(chat.getId(), () -> {
                String msg = URLEncoder.encode(JSON.toJSONString(response[0], true), StandardCharsets.UTF_8);
                String urlParam = String.format("?chat_id=%d&text=%s", chat.getId(), msg);
                ClientHttpRequest request = new OkHttp3ClientHttpRequestFactory().createRequest(URI.create(configs.sendMsgUrl + urlParam), HttpMethod.GET);
                DoRequestUtil.request(request);
            });
        }
    }
}
