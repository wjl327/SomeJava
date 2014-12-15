package com.service.st.impl;

import org.apache.thrift.TException;

import com.service.st.*;

public class StudentServiceImpl implements StudentService.Iface{

	@Override
	public boolean addStudent(Student student) throws TException {
		System.out.println(student.getId() + " " + student.getName());
		return true;
	}

}
