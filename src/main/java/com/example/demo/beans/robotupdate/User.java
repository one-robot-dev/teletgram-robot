package com.example.demo.beans.robotupdate;

import lombok.Data;

/**
 * @author yxs
 * @date 2023/04/23 18:07
 */
@Data
public class User {

    private long id;
    private boolean isBot;
    private String firstName;
    private String lastName;
    private String userName;
    private boolean isPremium;

}
