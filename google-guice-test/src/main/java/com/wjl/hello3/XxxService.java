package com.wjl.hello3;

import com.google.inject.ImplementedBy;

@ImplementedBy(XxxServiceImpl.class)
public interface XxxService {
	
	public void tst();

}
