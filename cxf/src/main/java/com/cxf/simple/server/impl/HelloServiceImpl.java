package com.cxf.simple.server.impl;  
  
import com.cxf.simple.server.IHelloService;
  
public class HelloServiceImpl implements IHelloService {  

	public String sayHello(String name) {  
		System.out.println("client called");
        return "hello " + name + "! ";  
    }  
  
}