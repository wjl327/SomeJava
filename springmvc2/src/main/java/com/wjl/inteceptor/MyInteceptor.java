package com.wjl.inteceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class MyInteceptor implements HandlerInterceptor {
	
//	分别实现预处理、后处理（调用了Service并返回ModelAndView，但未进行页面渲染）、返回处理（已经渲染了页面） 
//	在preHandle中，可以进行编码、安全控制等处理； 
//	在postHandle中，有机会修改ModelAndView； 
//	在afterCompletion中，可以根据ex是否为null判断是否发生了异常，进行日志记录。 
//	参数中的Object handler是下一个拦截器。 

	//Action之前执行
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		return false;
	}

	//生成视图之前执行
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		
	}

	//最后执行，可用于释放资源
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {

	}

}
