package com.example.demo.db.entity;

import java.io.Serializable;

public class UserInfoModelKey implements Serializable {
    private Long userId;

    private Long groupId;

    private static final long serialVersionUID = 1L;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }
}