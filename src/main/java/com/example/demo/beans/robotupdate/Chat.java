package com.example.demo.beans.robotupdate;

import lombok.Data;

/**
 * @author yxs
 * @date 2023/04/23 18:12
 */
@Data
public class Chat {

    private long id;
    /** “private”, “group”, “supergroup” or “channel”*/
    private String type;
    private String title;
    private String username;
    private String firstName;
    private String lastName;
    private boolean isForum;

    public boolean isSuperGroup() {
        return ChatType.SUPER_GROUP.getType().equals(type);
    }

    public boolean isPrivate() {
        return ChatType.PRIVATE.getType().equals(type);
    }
}
