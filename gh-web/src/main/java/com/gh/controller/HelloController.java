package com.gh.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gh.remote.HelloServiceRemote;

@Controller  
@RequestMapping("/hello")
public class HelloController {
	
    @Autowired
    private HelloServiceRemote helloService;
    
    @ResponseBody
    @RequestMapping("/ok")
    public String ok(){  
        return "Web Test ok!!!!!!!!!!!!" + helloService.sayHello("xiaoMi");  
    }

}
