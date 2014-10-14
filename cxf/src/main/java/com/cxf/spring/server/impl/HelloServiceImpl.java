package com.cxf.spring.server.impl;  
  
import javax.jws.WebService;

import com.cxf.spring.server.IHelloService;
  
@WebService(serviceName="com.cxf.spring.server.IHelloService")
public class HelloServiceImpl implements IHelloService { 
	
    public String sayHello(String name) {  
    	System.out.println("client called");
        return "hello " + name + "! ";
    }  
    
} 