package com.example.demo.db.entity;

import com.alibaba.fastjson.JSONObject;
import java.io.Serializable;

public class GroupFunctionModel extends GroupFunctionModelKey implements Serializable {
    private Integer status;

    private JSONObject param;

    private Long createTime;

    private Long updateTime;

    private static final long serialVersionUID = 1L;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public JSONObject getParam() {
        return param;
    }

    public void setParam(JSONObject param) {
        this.param = param;
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