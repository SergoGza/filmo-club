package com.videoclub.filmoapp.film.mvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/videoclub")
public class IndexMvcController {

    @GetMapping
    public String getIndex() {
        return "videoclub/index";
    }


}
