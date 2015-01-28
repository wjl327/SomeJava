package com.wjl.hello3;

import com.google.inject.ImplementedBy;

@ImplementedBy(GoServiceImpl.class)
public interface GoService {
	
	public void go();

}
