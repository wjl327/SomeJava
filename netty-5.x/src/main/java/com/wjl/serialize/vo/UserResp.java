package com.wjl.serialize.vo;

import java.io.Serializable;

public class UserResp implements Serializable{
	
	private static final long serialVersionUID = -7126070800349799783L;
	private int id;
	private String name;
	private int sex;
	private String nikename;
	private String email;
	public UserResp() {
		super();
	}
	public UserResp(int id, String name, int sex, String nikename, String email) {
		super();
		this.id = id;
		this.name = name;
		this.sex = sex;
		this.nikename = nikename;
		this.email = email;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getSex() {
		return sex;
	}
	public void setSex(int sex) {
		this.sex = sex;
	}
	public String getNikename() {
		return nikename;
	}
	public void setNikename(String nikename) {
		this.nikename = nikename;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	@Override
	public String toString() {
		return "UserResp [id=" + id + ", name=" + name + ", sex=" + sex
				+ ", nikename=" + nikename + ", email=" + email + "]";
	}
}
