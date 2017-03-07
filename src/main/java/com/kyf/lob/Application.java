package com.kyf.lob;

/**
 * Created by kuangyifei on 17-1-18.
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/")
// This annotation tells Spring Boot to “guess” how you will want to configure Spring,
// based on the jar dependencies that you have added.
//@EnableAutoConfiguration
@SpringBootApplication // same as @Configuration @EnableAutoConfiguration @ComponentScan
public class Application {

    private static Logger logger = LoggerFactory.getLogger(Application.class);

    @RequestMapping("/home")
    @ResponseBody
    String hello() {
        logger.info("welcome!!");
        return "Hello World!";
    }

    @RequestMapping("/home2")
    public ModelAndView home() {
        try {
            ModelAndView mv = new ModelAndView("/index");
            return mv;
        } catch (Exception e) {
            logger.error("resolve /home failed! {}", e);
            return null;
        }
    }

    public static void main(String[] args) throws Exception {
        // completely disable restart support
//        System.setProperty("spring.devtools.restart.enabled", "false");
        // We need to pass Example.class as an argument to the run method to tell
        // SpringApplication which is the primary Spring component.
        SpringApplication.run(Application.class, args);
    }
}

