package com.wjl.web;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;

/**
 * Guice 通过监听器的方式。将容器放在web应用的servletContext(上下文)里面。
 * 这样任何servlet都可以在任何时候获取到容器Injector。
 * 
 * @author jarviswu
 *
 */
public class MyGuiceServletContextListener extends GuiceServletContextListener {
	@Override
    protected Injector getInjector() {
		return Guice.createInjector(new ServletModule(){
			protected void configureServlets() {
				serve("/hello").with(HelloServlet.class);
			}
			
		});
	} 
}
