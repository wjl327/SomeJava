package com.gh.remote;

import org.springframework.remoting.caucho.HessianProxyFactoryBean;

import com.caucho.hessian.io.SerializerFactory;

public class SOAHessianProxyFactoryBean extends HessianProxyFactoryBean {

	private SOAHessianProxyFactory proxyFactory = new SOAHessianProxyFactory();
	
	private long connectTimeout = 1*1000;
	private boolean hasSetConnectTimeout = false;
	
	private long readTimeout = 20*1000;
	private boolean hasSetReadTimeout = false;
	
	private boolean useProxy = true;
	
	private boolean debug;
	
	public void setUseProxy(boolean useProxy) {
		this.useProxy = useProxy;
	}
	
	public void setConnectTimeout(long connectTimeout) {
		this.connectTimeout = connectTimeout;
		hasSetConnectTimeout = true;
	}
	
	/**
	 * Set the timeout to use when waiting for a reply from the Hessian service.
	 * @see com.caucho.hessian.client.HessianProxyFactory#setReadTimeout
	 */
	public void setReadTimeout(long readTimeout) {
		this.readTimeout = readTimeout;
		hasSetReadTimeout = true;
	}
	
	/**
	 * Set the HessianProxyFactory instance to use.
	 * If not specified, a default HessianProxyFactory will be created.
	 * <p>Allows to use an externally configured factory instance,
	 * in particular a custom HessianProxyFactory subclass.
	 */
	public void setProxyFactory(SOAHessianProxyFactory proxyFactory) {
		if(proxyFactory == null) {
			return;
		}
		if(!useProxy) {
			return;
		}
		this.proxyFactory = proxyFactory;
		commitProxyFactory();
	}
	
	/**
	 * Specify the Hessian SerializerFactory to use.
	 * <p>This will typically be passed in as an inner bean definition
	 * of type <code>com.caucho.hessian.io.SerializerFactory</code>,
	 * with custom bean property values applied.
	 */
	public void setSerializerFactory(SerializerFactory serializerFactory) {
		this.proxyFactory.setSerializerFactory(serializerFactory);
	}

	/**
	 * Set whether to send the Java collection type for each serialized
	 * collection. Default is "true".
	 */
	public void setSendCollectionType(boolean sendCollectionType) {
		this.proxyFactory.getSerializerFactory().setSendCollectionType(sendCollectionType);
	}

	/**
	 * Set whether overloaded methods should be enabled for remote invocations.
	 * Default is "false".
	 * @see com.caucho.hessian.client.HessianProxyFactory#setOverloadEnabled
	 */
	public void setOverloadEnabled(boolean overloadEnabled) {
		this.proxyFactory.setOverloadEnabled(overloadEnabled);
	}

	/**
	 * Set the username that this factory should use to access the remote service.
	 * Default is none.
	 * <p>The username will be sent by Hessian via HTTP Basic Authentication.
	 * @see com.caucho.hessian.client.HessianProxyFactory#setUser
	 */
	public void setUsername(String username) {
		this.proxyFactory.setUser(username);
	}

	/**
	 * Set the password that this factory should use to access the remote service.
	 * Default is none.
	 * <p>The password will be sent by Hessian via HTTP Basic Authentication.
	 * @see com.caucho.hessian.client.HessianProxyFactory#setPassword
	 */
	public void setPassword(String password) {
		this.proxyFactory.setPassword(password);
	}

	/**
	 * Set whether Hessian's debug mode should be enabled.
	 * Default is "false".
	 * @see com.caucho.hessian.client.HessianProxyFactory#setDebug
	 */
	public void setDebug(boolean debug) {
//		this.proxyFactory.setDebug(debug);
		this.debug = debug;
	}

	/**
	 * Set whether to use a chunked post for sending a Hessian request.
	 * @see com.caucho.hessian.client.HessianProxyFactory#setChunkedPost
	 */
	public void setChunkedPost(boolean chunkedPost) {
		this.proxyFactory.setChunkedPost(chunkedPost);
	}

	/**
	 * Set whether version 2 of the Hessian protocol should be used for
	 * parsing requests and replies. Default is "false".
	 * @see com.caucho.hessian.client.HessianProxyFactory#setHessian2Request
	 */
	public void setHessian2(boolean hessian2) {
		this.proxyFactory.setHessian2Request(hessian2);
		this.proxyFactory.setHessian2Reply(hessian2);
	}

	/**
	 * Set whether version 2 of the Hessian protocol should be used for
	 * parsing requests. Default is "false".
	 * @see com.caucho.hessian.client.HessianProxyFactory#setHessian2Request
	 */
	public void setHessian2Request(boolean hessian2) {
		this.proxyFactory.setHessian2Request(hessian2);
	}

	/**
	 * Set whether version 2 of the Hessian protocol should be used for
	 * parsing replies. Default is "false".
	 * @see com.caucho.hessian.client.HessianProxyFactory#setHessian2Reply
	 */
	public void setHessian2Reply(boolean hessian2) {
		this.proxyFactory.setHessian2Reply(hessian2);
	}


	@Override
	public void afterPropertiesSet() {
		commitProxyFactory();
		super.afterPropertiesSet();
	}
	
	private void commitProxyFactory() {
		if(this.debug) {
			proxyFactory.setReadTimeout(-1);
			proxyFactory.setConnectTimeout(-1);
		} else {
			if(hasSetConnectTimeout || proxyFactory.getConnectTimeout() == -1) {
				proxyFactory.setConnectTimeout(connectTimeout);
			}
			if(hasSetReadTimeout || proxyFactory.getReadTimeout() == -1) {
				proxyFactory.setReadTimeout(readTimeout);
			}
		}
		super.setProxyFactory(proxyFactory);
	}
}
