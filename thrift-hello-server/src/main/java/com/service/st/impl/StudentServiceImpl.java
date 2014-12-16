package com.service.st.impl;

import org.apache.thrift.TException;

import com.service.st.*;

/**
 * StudentService服务实现类
 * 
 * @author Jarvis.Wu
 *
 */
public class StudentServiceImpl implements StudentService.Iface{

	@Override
	public boolean addStudent(Student student) throws TException {
		System.out.println(student.getId() + " " + student.getName());
		return true;
	}

}
