package com.wjl.timer.hello;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("helloTimer")
public class HelloTimer {
	
	Logger logger = LoggerFactory.getLogger(HelloTimer.class);
	
	public void sayHello(String userName){
		logger.info("hello,"+userName);
	}
	
	public void run() {
		sayHello("hello world");
	}
}
