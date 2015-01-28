package com.wjl.web;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Singleton;

@Singleton
public class HelloServlet extends HttpServlet{
	
	private static final long serialVersionUID = 5625929887079771586L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		System.out.println(req.getMethod());
		System.out.println(req.getProtocol());
		System.out.println(req.getPathInfo());
		System.out.println("---------------------");
		System.out.println();
		resp.getWriter().append("Hello, Guice!" + new Date());
	}

}
