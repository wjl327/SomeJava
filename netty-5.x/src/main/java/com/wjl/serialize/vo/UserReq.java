package com.wjl.serialize.vo;

import java.io.Serializable;

public class UserReq implements Serializable{
	
	private static final long serialVersionUID = -6032282464065546696L;
	private int id;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
}
