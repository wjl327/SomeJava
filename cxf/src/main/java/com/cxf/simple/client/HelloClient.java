package com.cxf.simple.client;  
  
import org.apache.cxf.frontend.ClientProxyFactoryBean;

import com.cxf.simple.server.IHelloService;
  
public class HelloClient {  
	
    public static void main(String[] args) {  
    	ClientProxyFactoryBean client = new ClientProxyFactoryBean();
    	client.setAddress("http://localhost:8888/hello");
    	IHelloService helloService  = client.create(IHelloService.class);  
    	System.out.println(helloService);
		System.out.println(helloService.sayHello("xiaoMi"));
    }
    
}  
