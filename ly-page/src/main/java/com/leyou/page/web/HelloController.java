package com.leyou.page.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloController {
    @GetMapping("demo")
    public String demo(Model model){
        model.addAttribute("msg","hello");
        return "hello";
    }
}
