package com.allhour.allhourstudy.modules.main;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Profile("dev")
@Controller
public class HelloController {

    @GetMapping("/helloWorld")
    public String helloWorld() {
        return "helloWorld";
    }
}
