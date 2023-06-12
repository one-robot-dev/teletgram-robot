package com.example.demo.beans.enums;

public enum GroupUserStatusType {

    JOIN(0),

    LEFT(1),

    ;

    public final int id;

    GroupUserStatusType(int id) {
        this.id = id;
    }

}
