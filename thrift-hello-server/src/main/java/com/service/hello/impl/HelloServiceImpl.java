package com.service.hello.impl;

import org.apache.thrift.TException;

import com.service.hello.HelloService;

public class HelloServiceImpl implements HelloService.Iface {

	@Override
	public String sayHello(String username) throws TException {
		System.out.println("Client requested.");
		return "Hello," + username + "!";
	}

}
