package com.example.demo.beans.robotupdate;

import lombok.Data;

/**
 * @author yxs
 * @date 2023/04/23 18:00
 */
@Data
public class Update {
    /**更新唯一标识*/
    private long updateId;

    private Message message;

}
