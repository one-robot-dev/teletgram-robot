package com.example.demo.beans.response;

import com.example.demo.beans.robotupdate.Chat;
import lombok.Data;

/**
 * @author yxs
 * @date 2023/04/26 23:48
 */
@Data
public class GetChatResponse {
    private boolean ok;
    private Chat result;
}
