package com.wjl.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wjl.dao.CategoryDao;
import com.wjl.service.CategoryService;
import com.wjl.vo.Category;

@Service
public class CategoryServiceImpl implements CategoryService {
	
	@Autowired
	private CategoryDao categoryDao;

	public void createCategory(Category category) {
		category.setCreateTime(new Date());
		categoryDao.createCategory(category);
	}

	public void updateCategory(Category category) {
		categoryDao.updateCategory(category);
	}

	@Override
	public void deleteCategory(String id) {
		categoryDao.deleteCategory(id);
	}

	@Override
	public List<Category> findCategorys() {
		List<Category> rst = categoryDao.findCategorys();
		System.out.println(rst);
		return rst;
	}

	@Override
	public void init() {
		createCategory(new Category("Java"));
		createCategory(new Category("PHP"));
		createCategory(new Category("Node.js"));
	}

}
