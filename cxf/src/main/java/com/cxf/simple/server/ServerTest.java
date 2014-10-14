package com.cxf.simple.server;  

import org.apache.cxf.frontend.ServerFactoryBean;

import com.cxf.simple.server.impl.HelloServiceImpl;
  
public class ServerTest {  
	
	/**
	 * 启动后，可以查看wsdl文件
	 * http://localhost:8888/hello?wsdl
	 * 
	 * @param args
	 * @throws InterruptedException
	 */
    public static void main(String[] args) throws InterruptedException {  
    	IHelloService helloServices = new HelloServiceImpl();
    	ServerFactoryBean factory = new ServerFactoryBean();
    	factory.setAddress("http://localhost:8888/hello"); 
    	factory.setServiceClass(IHelloService.class);  
    	factory.setServiceBean(helloServices);  
    	factory.create();  
    	System.out.println("Server ready...");  
    	Thread.sleep(5 * 60 * 1000);  
    	System.out.println("Server exiting");  
    	System.exit(0);  
    }  
}  
