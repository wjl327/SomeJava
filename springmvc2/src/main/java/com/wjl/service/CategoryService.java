package com.wjl.service;

import java.util.List;

import com.wjl.vo.Category;

public interface CategoryService {
	
	public void createCategory(Category category);
	
	public void updateCategory(Category category);
	
	public void deleteCategory(String id);
	
	public List<Category> findCategorys();

	public void init();
	
}