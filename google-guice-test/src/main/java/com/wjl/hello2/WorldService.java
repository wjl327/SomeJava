package com.wjl.hello2;

import com.google.inject.ImplementedBy;

@ImplementedBy(WorldServiceImpl.class)
public interface WorldService {
	
	public String hello();

}
