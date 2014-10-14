package com.cxf.spring.client;
  
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

import com.cxf.spring.server.IHelloService;
  
public class Client {
	
    public static void main(String[] args) {  
    	invoke();  
    	invoke2();
    }  
     
    public static void invoke2(){  
    	//创建WebService客户端代理工厂 
        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();     
        //注册WebService接口 
        factory.setServiceClass(IHelloService.class);     
        //设置WebService地址   
        factory.setAddress("http://localhost:8070/cxf/services/HelloService");          
        IHelloService iHelloWorld = (IHelloService)factory.create();     
        System.out.println(iHelloWorld.sayHello("xiaoLiang"));
        System.exit(0);     
    }  
     
    public static void invoke(){  
    	//创建WebService客户端代理工厂      
        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();     
        //注册WebService接口      
        factory.setServiceClass(IHelloService.class);     
        //设置WebService地址      
        factory.setAddress("http://localhost:8070/cxf/services/HelloService?wsdl");    
        IHelloService helloServices = (IHelloService)factory.create();     
        System.out.println("invoke helloServices webservice...");  
        String hello = helloServices.sayHello("xiaoMi");  
          
       System.out.println(hello);  
    }  
}  