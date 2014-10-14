package com.wjl.vo;

import java.io.Serializable;

public class Person implements Serializable{
	private static final long serialVersionUID = 4542832134148763263L;
	
	private String name;
	private String gmid;
	private String email;
	private int age;
	
	public Person() {
		super();
	}
	public Person(String name, String gmid, String email, int age) {
		super();
		this.name = name;
		this.gmid = gmid;
		this.email = email;
		this.age = age;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getGmid() {
		return gmid;
	}
	public void setGmid(String gmid) {
		this.gmid = gmid;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	@Override
	public String toString() {
		return "Person [name=" + name + ", gmid=" + gmid + ", email=" + email
				+ ", age=" + age + "]";
	}

}
