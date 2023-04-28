package com.example.demo.updatehandler.group;

import com.example.demo.beans.robotupdate.Update;

public interface RobotGroupUpdatesHandler<T> {

    default void process(long groupId, Update data, String param) throws Exception {
        if (isOpen(groupId)) {
            handle(data, parseParam(param));
        }
    }

    void handle(Update data, T param) throws Exception;

    GroupHandlerType getType();

    T parseParam(String param);

    boolean isOpen(long groupId);
}
