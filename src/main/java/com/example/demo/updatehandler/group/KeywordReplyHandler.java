package com.example.demo.updatehandler.group;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.beans.enums.GroupUserStatusType;
import com.example.demo.beans.handlerparam.KeywordReplyParam;
import com.example.demo.beans.robotupdate.Chat;
import com.example.demo.beans.robotupdate.Message;
import com.example.demo.beans.robotupdate.Update;
import com.example.demo.beans.robotupdate.User;
import com.example.demo.config.Configs;
import com.example.demo.db.dao.UserInfoModelMapper;
import com.example.demo.db.entity.UserInfoModelExample;
import com.example.demo.service.GroupFunctionService;
import com.example.demo.utils.DoRequestUtil;
import com.example.demo.utils.iologic.IOLogicExecuteUtil;
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
import java.util.StringJoiner;


/**
 * @author yxs
 * @date 2023/04/26 14:07
 */
@Service
public class KeywordReplyHandler implements RobotGroupUpdatesHandler<KeywordReplyParam> {

    @Autowired
    private Configs configs;

    @Autowired
    private GroupFunctionService groupFunctionService;

    @Autowired
    private UserInfoModelMapper userMapper;

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
        if (from == null || from.getIsBot()) {
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
        if (!reply.getLimitUser().isEmpty() && !reply.getLimitUser().contains(from.getId())) {
            return;
        }
        IOLogicExecuteUtil.exeChatIOLogic(chat.getId(), () -> {
            String msg = reply.getMsg();
            if (msg.contains("{@all}")) {
                StringJoiner atAll = new StringJoiner(" @");
                UserInfoModelExample example = new UserInfoModelExample();
                example.createCriteria().andGroupIdEqualTo(chat.getId()).andStatusEqualTo(GroupUserStatusType.JOIN.id);
                userMapper.selectByExample(example).forEach(u -> atAll.add(u.getUserName()));
                msg = msg.replace("{@all}", " @" + atAll);
            }
            msg = msg.replace("{groupTitle}", StringUtils.defaultString(chat.getTitle()))
                    .replace("{groupUserName}", StringUtils.defaultString(chat.getUsername()))
                    .replace("{userFirstName}", StringUtils.defaultString(from.getFirstName()))
                    .replace("{userLastName}", StringUtils.defaultString(from.getLastName()))
                    .replace("{userName}", StringUtils.defaultString(from.getUserName()))
                    .replace("{userId}", String.valueOf(from.getId()));
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
            String url = configs.sendMsgUrl + urlParam;
            ClientHttpRequest request = new OkHttp3ClientHttpRequestFactory().createRequest(URI.create(url), HttpMethod.GET);
            DoRequestUtil.request(request);
        });
    }

    @Override
    public GroupHandlerType getType() {
        return GroupHandlerType.KEYWORD_REPLY;
    }

    @Override
    public KeywordReplyParam parseParam(JSONObject param) {
        return param.toJavaObject(KeywordReplyParam.class);
    }

    @Override
    public boolean isOpen(long groupId) {
        return groupFunctionService.isFunctionOpen(groupId, getType().type());
    }
}
