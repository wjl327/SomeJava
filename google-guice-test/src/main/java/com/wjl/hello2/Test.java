package com.wjl.hello2;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class Test {
	
	public static void main(String[] args) {
		Injector inj=  Guice.createInjector();
		WorldService worldService1 = inj.getInstance(WorldService.class);
		WorldService worldService2 = inj.getInstance(WorldService.class);
		System.out.println(worldService1.hello());
		System.out.println(worldService2.hello());
		System.out.println("是否单例？ " + (worldService1 == worldService2));
	}

}
