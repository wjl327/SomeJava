package com.gh.util;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component("commonConverter")
public class CommonConverter {
	
	public <T> T convert(Class<T> clazz , Object object){
		if(object!=null){
			T obj=null;
			try {
				obj = clazz.newInstance();
				BeanUtils.copyProperties(object, obj);
				return obj ;
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} 
		}
		return null;
	}
}