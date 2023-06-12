package com.example.demo.beans.robotupdate;

import lombok.Data;

import java.util.List;

/**
 * @author yxs
 * @date 2023/04/23 18:02
 */
@Data
public class Message {

    private long messageId;
    private User from;
    private Chat chat;
    private List<User> newChatMembers;
    private User leftChatMember;
    private String text;
    private int date;

}
