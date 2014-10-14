package com.wjl.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.wjl.vo.Category;

@Repository
public class CategoryDao {
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	private String collectionName = "category";
	
	public void createCategory(Category category){
		mongoTemplate.save(category, collectionName);
	}
	
	public void updateCategory(Category category){
		mongoTemplate.updateFirst(
				Query.query(Criteria.where("id").is(category.getId())), 
				Update.update("name", category.getName()), 
				collectionName);
	}
	
	public void deleteCategory(String id){
		mongoTemplate.remove(Query.query(Criteria.where("id").is(id)), collectionName);
	}
	
	public List<Category> findCategorys(){
		return mongoTemplate.findAll(Category.class);
	}
	
}
