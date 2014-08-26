package com.gh.remote;

import com.caucho.hessian.client.HessianProxyFactory;

public class SOAHessianProxyFactory extends HessianProxyFactory {

	private long connectTimeout;

	public long getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(long connectTimeout) {
		this.connectTimeout = connectTimeout;
	}
}
