package nus.iss.travlr.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(path = "/")
public class LandingController {

    @GetMapping
    public String getLanding() {
        return "index";
    }

    @GetMapping(path = "/map")
    public String getMap() {
        return "map";
    }

    @GetMapping(path = "/testmap")
    public String getTestMap() {
        return "testmap";
    }
}
