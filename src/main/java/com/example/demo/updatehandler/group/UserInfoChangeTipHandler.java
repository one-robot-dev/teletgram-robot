package com.example.demo.updatehandler.group;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.beans.handlerparam.UserInfoChangeParam;
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

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @author yxs
 * @date 2023/04/26 14:10
 */
@Service
public class UserInfoChangeTipHandler implements RobotGroupUpdatesHandler<UserInfoChangeParam> {

    private UserInfoModelMapper userInfoModelMapper;

    @Autowired
    private GroupFunctionService groupFunctionService;

    @Autowired
    private Configs configs;

    public UserInfoChangeTipHandler(UserInfoModelMapper userInfoModelMapper, Configs configs) {
        this.userInfoModelMapper = userInfoModelMapper;
        this.configs = configs;
    }

    /**
     * 提示用户信息修改
     * @param data    新数据
     */
    @Override
    public void handle(Update data, UserInfoChangeParam param)throws Exception {
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
        UserInfoModelKey key = new UserInfoModelKey();
        key.setUserId(from.getId());
        key.setGroupId(chat.getId());
        UserInfoModel dbModel = userInfoModelMapper.selectByPrimaryKey(key);
        long now = System.currentTimeMillis();
        if (dbModel == null) {
            UserInfoModel userInfoModel = new UserInfoModel();
            userInfoModel.setUserId(from.getId());
            userInfoModel.setGroupId(chat.getId());
            userInfoModel.setFirstName(from.getFirstName());
            userInfoModel.setLastName(from.getLastName());
            userInfoModel.setUserName(from.getUserName());
            userInfoModel.setCreateTime(now);
            userInfoModel.setUpdateTime(now);
            userInfoModelMapper.insert(userInfoModel);
        } else {
            boolean haveUpdate = false;
            String dbFirstName = StringUtils.defaultIfEmpty(dbModel.getFirstName(), ""), dbLastName = StringUtils.defaultIfEmpty(dbModel.getLastName(), "");
            String newFirstName = StringUtils.defaultIfEmpty(from.getFirstName(), ""), newLastName = StringUtils.defaultIfEmpty(from.getLastName(), "");
            if (!dbFirstName.equals(newFirstName) || !dbLastName.equals(newLastName)) {
                String newName = newFirstName + (StringUtils.isNotBlank(newLastName) ? " " + newLastName : "");
                String oldName = dbFirstName + " " + (StringUtils.isNotBlank(dbLastName) ? " " + dbLastName : "");
                dbModel.setFirstName(from.getFirstName());
                dbModel.setLastName(from.getLastName());
                //发送改名的提示
                IOLogicExecuteUtil.exeChatIOLogic(chat.getId(), () -> {
                    String tip = param.getChangeUserNameTip()
                            .replace("{new}", newName)
                            .replace("{old}", oldName)
                            .replace("{groupTitle}", StringUtils.defaultString(chat.getTitle()))
                            .replace("{groupUserName}", StringUtils.defaultString(chat.getUsername()))
                            .replace("{userId}", String.valueOf(from.getId()));
                    String msg = URLEncoder.encode(tip, StandardCharsets.UTF_8);
                    String urlParam = String.format("?chat_id=%d&text=%s", chat.getId(), msg);
                    ClientHttpRequest request = new OkHttp3ClientHttpRequestFactory().createRequest(URI.create(configs.sendMsgUrl + urlParam), HttpMethod.GET);
                    DoRequestUtil.request(request);
                });
                haveUpdate = true;
            }
            String oldUserName = StringUtils.defaultIfEmpty(dbModel.getUserName(), "");
            String newUserName = StringUtils.defaultIfEmpty(from.getUserName(), "");
            if (!oldUserName.equals(newUserName)) {
                dbModel.setUserName(newUserName);
                //发送改唯一名的提示
                IOLogicExecuteUtil.exeChatIOLogic(chat.getId(), () -> {
                    String tip = param.getChangeUserNameTip()
                            .replace("{new}", newUserName)
                            .replace("{old}", oldUserName)
                            .replace("{groupTitle}", StringUtils.defaultString(chat.getTitle()))
                            .replace("{groupUserName}", StringUtils.defaultString(chat.getUsername()))
                            .replace("{userId}", String.valueOf(from.getId()));
                    String msg = URLEncoder.encode(tip, StandardCharsets.UTF_8);
                    String urlParam = String.format("?chat_id=%d&text=%s", chat.getId(), msg);
                    ClientHttpRequest request = new OkHttp3ClientHttpRequestFactory().createRequest(URI.create(configs.sendMsgUrl + urlParam), HttpMethod.GET);
                    DoRequestUtil.request(request);
                });
                haveUpdate = true;
            }
            if (haveUpdate) {
                dbModel.setUpdateTime(now);
                userInfoModelMapper.updateByPrimaryKey(dbModel);
            }
        }
    }

    @Override
    public GroupHandlerType getType() {
        return GroupHandlerType.USER_INFO_CHANGE;
    }

    @Override
    public UserInfoChangeParam parseParam(JSONObject param) {
        return param.toJavaObject(UserInfoChangeParam.class);
    }

    @Override
    public boolean isOpen(long groupId) {
        return groupFunctionService.isFunctionOpen(groupId, getType().type());
    }
}
