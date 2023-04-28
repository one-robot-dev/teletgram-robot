package com.example.demo.utils.iologic;

import com.example.demo.utils.ThrowableRunnable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class IOLogicExecuteUtil {

    private static final Map<Long, IOActionQueue> chatActionQueue = new ConcurrentHashMap<>();

    /**执行聊天相关的io逻辑*/
    public static void exeChatIOLogic(long chatId, ThrowableRunnable logic) {
        chatActionQueue.computeIfAbsent(chatId, i -> new IOActionQueue()).enqueue(logic);
    }

}
