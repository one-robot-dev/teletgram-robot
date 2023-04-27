package com.example.demo.db.entity;

import java.io.Serializable;

public class GroupReplyModel extends GroupReplyModelKey implements Serializable {
    private String reply;

    private Long createTime;

    private Long updateTime;

    private static final long serialVersionUID = 1L;

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply == null ? null : reply.trim();
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }
}