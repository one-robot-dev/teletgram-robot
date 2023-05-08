package com.example.demo.updatehandler.group;

import com.alibaba.fastjson.JSON;
import com.example.demo.beans.handlerparam.GroupWelcomeParam;
import com.example.demo.beans.robotupdate.*;
import com.example.demo.config.Configs;
import com.example.demo.db.dao.UserInfoModelMapper;
import com.example.demo.db.entity.UserInfoModel;
import com.example.demo.db.entity.UserInfoModelKey;
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
import java.util.List;

/**
 * @author yxs
 * @date 2023/04/26 14:09
 */
@Service
public class GroupWelcomeHandler implements RobotGroupUpdatesHandler<GroupWelcomeParam> {

    @Autowired
    private UserInfoModelMapper userInfoModelMapper;

    @Autowired
    private GroupFunctionService groupFunctionService;

    @Autowired
    private Configs configs;

    @Override
    public void handle(Update data, GroupWelcomeParam param) {
        //获取加入信息
        Message message = data.getMessage();
        if (message == null) {
            return;
        }
        List<User> newMembers = message.getNewChatMembers();
        if (newMembers == null || newMembers.isEmpty()) {
            return;
        }
        Chat chat = message.getChat();
        if (chat == null || !chat.isSuperGroup()) {
            return;
        }
        //玩家信息入库
        newMembers.forEach(user -> {
            UserInfoModelKey key = new UserInfoModelKey();
            key.setGroupId(chat.getId());
            key.setUserId(user.getId());
            UserInfoModel model = userInfoModelMapper.selectByPrimaryKey(key);
            long now = System.currentTimeMillis();
            boolean insert = model == null;
            if (insert) {
                model = new UserInfoModel();
                model.setGroupId(chat.getId());
                model.setUserId(user.getId());
                model.setCreateTime(now);
            }
            model.setFirstName(user.getFirstName());
            model.setLastName(user.getLastName());
            model.setUserName(user.getUserName());
            model.setUpdateTime(now);
            if (insert) {
                userInfoModelMapper.insert(model);
            } else {
                userInfoModelMapper.updateByPrimaryKey(model);
            }
        });
        //根据功能设置，提示欢迎语
        IOLogicExecuteUtil.exeChatIOLogic(chat.getId(), () -> {
            String msg = URLEncoder.encode(param.getWelcomeTip().replace("{groupTitle}", StringUtils.defaultString(chat.getTitle())).replace("{groupUserName}", StringUtils.defaultString(chat.getUsername())), StandardCharsets.UTF_8);
            String urlParam = String.format("?chat_id=%d&text=%s", chat.getId(), msg);
            ClientHttpRequest request = new OkHttp3ClientHttpRequestFactory().createRequest(URI.create(configs.sendMsgUrl + urlParam), HttpMethod.GET);
            DoRequestUtil.request(request);
        });
    }

    @Override
    public GroupHandlerType getType() {
        return GroupHandlerType.WELCOME;
    }

    @Override
    public GroupWelcomeParam parseParam(String param) {
        if (StringUtils.isBlank(param)) {
            return null;
        }
        return JSON.parseObject(param, GroupWelcomeParam.class);
    }

    @Override
    public boolean isOpen(long groupId) {
        return groupFunctionService.isFunctionOpen(groupId, getType().type());
    }
}
