package com.wjl;

import javax.servlet.ServletContext;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;


public class Bootstrap {
	
	public static void main(String[] args) throws Exception {
		start();
	}
	
	private static void start() throws Exception{
		int port = 8080;
		String contextPath = "mvc";
		Server server = new Server(port);
        ServletContextHandler context = new ServletContextHandler(
                        ServletContextHandler.SESSIONS);
        context.setContextPath("/"+contextPath);
        server.setHandler(context);
        
        FilterHolder holder = new FilterHolder();
        holder.setInitParameter("encoding", "UTF-8");
        holder.setInitParameter("forceEncoding", "true");
        holder.setClassName("org.springframework.web.filter.CharacterEncodingFilter");
       	context.addFilter(holder, "/*", null);
        
        WebApplicationContext appContext = createXmlConfiguration(context.getServletContext());
        DispatcherServlet dispatcherServlet = new DispatcherServlet(appContext);
        context.addServlet(new ServletHolder(dispatcherServlet), "/*");
        
        System.out.println("Server started");
        server.start();
        server.join();
	}

	private static WebApplicationContext createXmlConfiguration(ServletContext context) {
		XmlWebApplicationContext appContext = new XmlWebApplicationContext();
		appContext.setServletContext(context);
		appContext.setConfigLocation("classpath:/applicationContext.xml");
		appContext.refresh();
		return appContext;
	}

}
