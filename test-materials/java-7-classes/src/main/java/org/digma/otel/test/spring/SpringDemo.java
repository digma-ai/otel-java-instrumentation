package org.digma.otel.test.spring;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpringDemo {

    @GetMapping("/hello")
    public String hello(){
        return "Hello";
    }
}
