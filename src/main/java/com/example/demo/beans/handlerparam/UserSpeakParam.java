package com.example.demo.beans.handlerparam;

import lombok.Data;

import java.util.Collections;
import java.util.Set;

/**
 * @author yxs
 * @date 2023/04/27 11:12
 */
@Data
public class UserSpeakParam {

    private String tipMsg;

    private long receiveId;

    private int checkInterval;

    private Set<String> kefu = Collections.emptySet();
}
