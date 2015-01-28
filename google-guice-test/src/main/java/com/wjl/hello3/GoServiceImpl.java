package com.wjl.hello3;

import com.google.inject.Singleton;

@Singleton
public class GoServiceImpl implements GoService{

	@Override
	public void go() {
		System.out.println("Let's go.....");
	}

}
