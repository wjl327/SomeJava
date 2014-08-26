package com.gh.dto;

import java.io.Serializable;

public class HelloDTO implements Serializable{
	
	private static final long serialVersionUID = -3741517641041137897L;
	
	private int id;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
}
