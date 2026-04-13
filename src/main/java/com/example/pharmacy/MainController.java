package com.example.pharmacy;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MainController {
    public MainController(){

    }

    @GetMapping("/hello")
    @ResponseBody
    public String view(){
        return "<h1>hello</h1>";
    }
}