package com.kyf.lob;

/**
 * Created by kuangyifei on 17-1-18.
 */

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
// This annotation tells Spring Boot to “guess” how you will want to configure Spring,
// based on the jar dependencies that you have added.
//@EnableAutoConfiguration
@SpringBootApplication // same as @Configuration @EnableAutoConfiguration @ComponentScan
public class Application {

    @RequestMapping("/")
    @ResponseBody
    String hello() {
        return "Hello World!";
    }

    @RequestMapping("/home")
    public ModelAndView home() {
        ModelAndView mv = new ModelAndView("/index");
        return mv;
    }

    public static void main(String[] args) throws Exception {
        // completely disable restart support
//        System.setProperty("spring.devtools.restart.enabled", "false");
        // We need to pass Example.class as an argument to the run method to tell
        // SpringApplication which is the primary Spring component.
        SpringApplication.run(Application.class, args);
    }
}

