package com.example.demo.db.entity;

import java.io.Serializable;

public class GroupFunctionModelKey implements Serializable {
    private Long groupId;

    private String type;

    private static final long serialVersionUID = 1L;

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }
}