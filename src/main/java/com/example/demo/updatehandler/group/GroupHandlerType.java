package com.example.demo.updatehandler.group;

public enum GroupHandlerType {

    WELCOME("welcome"),
    KEYWORD_REPLY("keywordReply"),
    USER_INFO_CHANGE("userInfoChange"),
    USER_SPEAK_TIP("userSpeakTip"),
    GET_GROUP_ID("getGroupId"),
    ;

    private String type;

    GroupHandlerType(String type) {
        this.type = type;
    }

    public String type() {
        return this.type;
    }
}
