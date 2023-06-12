package com.example.demo.beans.handlerparam;

import lombok.Data;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * @author yxs
 * @date 2023/04/27 12:13
 */
@Data
public class KeywordReplyParam {

    private Set<String> excludeUser = Collections.emptySet();

    private Map<String, Reply> reply = Collections.emptyMap();

    @Data
    public static
    class Reply{

        private boolean replySpecMsg;

        private String msg;

        private String replyMarkUp;

        private Set<Long> limitUser = Collections.emptySet();
    }
}

