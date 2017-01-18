package hello;

/**
 * Created by kuangyifei on 17-1-18.
 */

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;

@Controller
// This annotation tells Spring Boot to “guess” how you will want to configure Spring,
// based on the jar dependencies that you have added.
@EnableAutoConfiguration
public class SampleController {

    @RequestMapping("/")
    @ResponseBody
    String home() {
        return "Hello World!";
    }

    public static void main(String[] args) throws Exception {
        // We need to pass Example.class as an argument to the run method to tell
        // SpringApplication which is the primary Spring component.
        SpringApplication.run(SampleController.class, args);
    }
}

