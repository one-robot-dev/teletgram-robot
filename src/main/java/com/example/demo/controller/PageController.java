package com.example.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author yxs
 * @date 2023/04/23 21:46
 */
@RequestMapping("/page")
public class PageController {

    @RequestMapping("/index")
    public String index() {
        return "index";
    }

}
