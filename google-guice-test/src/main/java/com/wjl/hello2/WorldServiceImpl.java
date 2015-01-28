package com.wjl.hello2;

import com.google.inject.Singleton;

@Singleton
public class WorldServiceImpl implements WorldService{

	public String hello() {
		return "hello world!!!!";
	}
	
}
