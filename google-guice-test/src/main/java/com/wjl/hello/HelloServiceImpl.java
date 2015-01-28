package com.wjl.hello;

public class HelloServiceImpl implements HelloService{

	@Override
	public String sayHello(String name) {
		return "hello, " + name;
	}

}
