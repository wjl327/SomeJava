package com.gh.client;

import com.caucho.hessian.client.HessianProxyFactory;
import com.gh.remote.HelloServiceRemote;

public class Client {
	
	public static void main(String[] args) throws Exception {
		HessianProxyFactory factory = new HessianProxyFactory();
        String url = "http://localhost:8080/gh-soa/remoting/hello";
        HelloServiceRemote helloService=(HelloServiceRemote)factory.create(HelloServiceRemote.class, url);
        System.out.println(helloService.sayHello("xiaoMimi"));
	}

}
