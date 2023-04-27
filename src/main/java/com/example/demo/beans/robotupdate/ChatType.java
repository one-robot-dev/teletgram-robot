package com.example.demo.beans.robotupdate;

/**
 * @author yxs
 * @date 2023/04/23 19:46
 */
public enum ChatType {
    PRIVATE("private"),
    GROUP("group"),
    SUPER_GROUP("supergroup"),
    CHANNEL("channel"),
    ;

    private String type;

    ChatType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
