package com.wjl.hello;

import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.Scope;
import com.google.inject.Scopes;

public class Test {
	
	public static void main(String[] args) {
		Injector injector = Guice.createInjector(new Module() {
			public void configure(Binder binder) {
				binder.bind(HelloService.class).to(HelloServiceImpl.class);
			}
		});
		
		//简单使用
		HelloService helloService1 = injector.getInstance(HelloService.class);
		System.out.println(helloService1);
		System.out.println("test1: " + helloService1.sayHello("Jarvis Wu"));
		
		//测试是否单例
		HelloService tmp = injector.getInstance(HelloService.class);
		System.out.println("Injector默认是否单例：" + (helloService1 == tmp));
		
		//允许自己创建对象
		Injector injector2 = Guice.createInjector(new Module() {
			public void configure(Binder binder) {
				binder.bind(HelloService.class).toInstance(new HelloServiceImpl());
			}
		});
		HelloService helloService2 = injector2.getInstance(HelloService.class);
		System.out.println("test2: " + helloService2.sayHello("Jarvis Wu"));
		
		//提供构造对象的方式
		Injector injector3 = Guice.createInjector(new Module() {
			public void configure(Binder binder) {
				binder.bind(HelloService.class).toProvider(new Provider<HelloService>() {
					public HelloService get() {
						return new HelloServiceImpl();
					}
				});
			}
		});
		HelloService helloService3 = injector3.getInstance(HelloService.class);
		System.out.println("test3: " + helloService3.sayHello("Jarvis Wu"));
		
		//单例的使用
		Injector injector4 = Guice.createInjector(new Module() {
			public void configure(Binder binder) {
				binder.bind(HelloService.class).to(HelloServiceImpl.class).in(Scopes.SINGLETON);
			}
		});
		HelloService helloService4 = injector4.getInstance(HelloService.class);
		HelloService helloService5 = injector4.getInstance(HelloService.class);
		System.out.println("test4: 单例？ " + (helloService4 == helloService5));
	}

}
