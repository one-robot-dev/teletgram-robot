package com.example.demo.updatehandler.group;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.beans.enums.GroupUserStatusType;
import com.example.demo.beans.robotupdate.Chat;
import com.example.demo.beans.robotupdate.Message;
import com.example.demo.beans.robotupdate.Update;
import com.example.demo.beans.robotupdate.User;
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

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @author yxs
 * @date 2023/06/10
 */
@Service
public class GroupMemberLeftHandler implements RobotGroupUpdatesHandler<JSONObject> {

    @Autowired
    private UserInfoModelMapper userInfoModelMapper;

    @Override
    public void handle(Update data, JSONObject param) {
        //获取加入信息
        Message message = data.getMessage();
        if (message == null) {
            return;
        }
        User leftMember = message.getLeftChatMember();
        if (leftMember == null || leftMember.getIsBot()) {
            return;
        }
        Chat chat = message.getChat();
        if (chat == null || !chat.isSuperGroup()) {
            return;
        }
        //玩家信息入库
        IOLogicExecuteUtil.exeChatIOLogic(chat.getId(), () -> {
            UserInfoModelKey key = new UserInfoModelKey();
            key.setGroupId(chat.getId());
            key.setUserId(leftMember.getId());
            UserInfoModel model = userInfoModelMapper.selectByPrimaryKey(key);
            if (model != null) {
                long now = System.currentTimeMillis();
                model.setUpdateTime(now);
                model.setStatus(GroupUserStatusType.LEFT.id);
                userInfoModelMapper.updateByPrimaryKey(model);
            }
        });
    }

    @Override
    public GroupHandlerType getType() {
        return GroupHandlerType.WELCOME;
    }

    @Override
    public JSONObject parseParam(JSONObject param) {
        return param;
    }

    @Override
    public boolean isOpen(long groupId) {
        return true;
    }
}
