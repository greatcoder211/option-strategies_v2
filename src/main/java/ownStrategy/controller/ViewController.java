package ownStrategy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
//controller widoków statycznych
@Controller
public class ViewController {
    @GetMapping("/strategy")
    public String strategy() {
        return "index";
    }
    @GetMapping("/")
    public String home() {
        return "index";
    }
}
