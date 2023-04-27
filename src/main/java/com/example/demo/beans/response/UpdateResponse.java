package com.example.demo.beans.response;

import com.example.demo.beans.robotupdate.Update;
import lombok.Data;

import java.util.List;

/**
 * @author yxs
 * @date 2023/04/23 18:25
 */
@Data
public class UpdateResponse {

    private boolean ok;
    private List<Update> result;

}
