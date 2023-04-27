package com.example.demo.updatehandler.common;

import com.example.demo.beans.robotupdate.Update;

/**
 * @author yxs
 * @date 2023/04/26 21:24
 */
public interface RobotUpdateHandler {

    void handle(Update data) throws Exception;

}
