package com.service.st.impl;

import org.apache.thrift.TException;

import com.service.st.*;

/**
 * TeacherService服务实现类
 * 
 * @author Jarvis.Wu
 *
 */
public class TeacherServiceImpl implements TeacherService.Iface{

	@Override
	public Teacher getTeacher(int id) throws TException {
		System.out.println("请求id为:"+ id);
		Teacher t = new Teacher();
		t.setId(id).setName("xx-"+id).setMaster(true);
		return t;
	}
	
}