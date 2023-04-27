package com.example.demo.updatehandler.group;

import com.alibaba.fastjson.JSON;
import com.example.demo.beans.handlerparam.KeywordReplyParam;
import com.example.demo.beans.robotupdate.Chat;
import com.example.demo.beans.robotupdate.Message;
import com.example.demo.beans.robotupdate.Update;
import com.example.demo.beans.robotupdate.User;
import com.example.demo.config.Configs;
import com.example.demo.utils.DoRequestUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


/**
 * @author yxs
 * @date 2023/04/26 14:07
 */
@Service
public class KeywordReplyHandler implements RobotGroupUpdatesHandler<KeywordReplyParam> {

    @Autowired
    private Configs configs;

    @Override
    public void handle(Update data, KeywordReplyParam param) throws IOException {
        //获取发送的消息
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
        if (param.getExcludeUser().contains(from.getUserName())) {
            return;
        }
        //根据消息进行回复
        KeywordReplyParam.Reply reply = param.getReply().get(message.getText());
        if (reply == null) {
            return;
        }
        String msg = reply.getMsg().replace("{groupTitle}", chat.getTitle())
                .replace("{groupUserName}", chat.getUsername())
                .replace("{targetName}", from.getFirstName() + " " + from.getLastName());
        msg = URLEncoder.encode(msg, StandardCharsets.UTF_8);
        String urlParam;
        if (reply.isReplySpecMsg()) {
            urlParam = String.format("?chat_id=%d&reply_to_message_id=%d&text=%s", chat.getId(), message.getMessageId(), msg);
        } else {
            urlParam = String.format("?chat_id=%d&text=%s", chat.getId(), msg);
        }
        if (StringUtils.isNotBlank(reply.getReplyMarkUp())) {
            urlParam = urlParam + "&reply_markup=" + URLEncoder.encode(reply.getReplyMarkUp(), StandardCharsets.UTF_8);
        }
        ClientHttpRequest request = new OkHttp3ClientHttpRequestFactory().createRequest(URI.create(configs.sendMsgUrl + urlParam), HttpMethod.GET);
        DoRequestUtil.request(request);
    }

    @Override
    public String getType() {
        return "keywordReply";
    }

    @Override
    public KeywordReplyParam parseParam(String param) {
        if (StringUtils.isBlank(param)) {
            return null;
        }
        return JSON.parseObject(param, KeywordReplyParam.class);
    }
}
