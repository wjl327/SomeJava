package com.wjl.hello3;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class Test {
	
	public static void main(String[] args) {
		Injector injector = Guice.createInjector();
		StudentService studentService = injector.getInstance(StudentService.class);
		GoService goService = injector.getInstance(GoService.class);
		studentService.go("Jarvis");
		System.out.printf("检测单例：%b\n\n" ,studentService.getGoService() == goService);
		
		//如果不通过Guice容器使用依赖注入，直接就用了，肯定会错误。下面为错误示例
		//StudentService studentService2 = new StudentServiceImpl();
		//studentService2.go("Jarvis");
		//但是。。。。。。。。
		//实际上自己new的，也可以让容器来帮忙注入
		StudentService studentService2 = new StudentServiceImpl(new XxxServiceImpl());
		Injector injector2 = Guice.createInjector();
		injector2.injectMembers(studentService2); //容器帮忙注入属性。
		studentService2.go("Jarvis");
		
	}

}
