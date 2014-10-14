package com.wjl.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wjl.service.CategoryService;
import com.wjl.vo.Category;

@Controller  
@RequestMapping("/category")
public class CategoryController {
	
	@Autowired
	private CategoryService service;
	
    /** 
     * 往Mongo初始化分类
     */  
    @ResponseBody
    @RequestMapping(value="/init", method=RequestMethod.GET)
    public String init(){  
    	service.init();
        return "success";  
    }  
      
    /** 
     * 往Mongo初始化分类
     */  
    @ResponseBody
    @RequestMapping(method=RequestMethod.GET)
    public List<Category> list(){  
    	return service.findCategorys();
    }  
    
}
