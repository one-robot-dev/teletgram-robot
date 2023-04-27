package com.example.demo.updatehandler.group;

import com.example.demo.beans.robotupdate.Update;

public interface RobotGroupUpdatesHandler<T> {

    void handle(Update data, T param) throws Exception;

    String getType();

    T parseParam(String param);
}
