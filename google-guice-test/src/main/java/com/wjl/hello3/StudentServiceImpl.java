package com.wjl.hello3;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class StudentServiceImpl implements StudentService{

	private XxxService xxxService;
	
	//构造方法注入
	@Inject
	public StudentServiceImpl(XxxService xxxService){
		this.xxxService = xxxService;
		this.xxxService.tst();
	}
	
	//属性注入
	@Inject
	private GoService goService;
	
	public GoService getGoService() {
		return goService;
	}
	
	//另外也还支持setter注入，少用，也简单就不写例子了。

	@Override
	public void go(String name) {
		System.out.print("Hi, " + name + ". ");
		goService.go();
	}

}
