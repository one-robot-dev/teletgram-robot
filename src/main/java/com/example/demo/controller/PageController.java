package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author yxs
 * @date 2023/04/23 21:46
 */
@Controller
@RequestMapping("/")
public class PageController {

    @RequestMapping("")
    public String index() {
        return "page/index";
    }

    @RequestMapping("page/groupFunction")
    public String groupFunction() {
        return "page/group_function";
    }

    @RequestMapping("page/setting")
    public String setting() {
        return "page/setting";
    }

}
