package com.wjl.hello3;

import com.google.inject.ImplementedBy;

@ImplementedBy(StudentServiceImpl.class)
public interface StudentService {
	
	public void go(String name);
	
	public GoService getGoService();

}
