package com.wjl.vo;

import java.io.Serializable;
import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

public class Category implements Serializable{
	
	private static final long serialVersionUID = 1L;
	@Id
	private ObjectId id;
	private String name;
	private Date createTime;
	
	public Category() {
		super();
	}
	public Category(String name) {
		super();
		this.name = name;
	}
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
}
