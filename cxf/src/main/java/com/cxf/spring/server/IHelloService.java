package com.cxf.spring.server;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

@WebService  
public interface IHelloService { 
	
	@WebMethod(operationName = "sayHello")  
    @WebResult(name = "result")  
    public String sayHello(@WebParam(name = "name") String name);  
}