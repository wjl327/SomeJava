package com.wjl.web;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.wjl.vo.Person;

@Controller
@RequestMapping("/person")
public class PersonController {
	
	
	private final static Map<String, Person> persons = new HashMap<String, Person>();  
    
    public PersonController(){  
    	persons.put("xiaoMi", new Person("小米", "44010223300059", "xiaoMi@163.com", 20));  
    	persons.put("小天", new Person("小天", "44010883266659", "xiaoTian@yahoo.com", 18));  
    	persons.put("大东", new Person("大东", "44010552277759", "daDong@qq.com", 19));  
    	persons.put("daShan", new Person("大山", "44010332211159", "daShan@126.com", 22));
    }
    

	@ResponseBody
	@RequestMapping(method=RequestMethod.GET)
	public Map<String, Person> list() {
		return persons;
	}
	
	//支持URL中文
	@ResponseBody
	@RequestMapping(value="/{name}",method=RequestMethod.GET)
	public Person get(@PathVariable String name) {
		System.out.println(name);
		return persons.get(name);
	}
	
	@ResponseBody
    @RequestMapping(value="/add", method=RequestMethod.POST)  
    public String add(@RequestBody Person person){
		System.out.println(person);
		persons.put(person.getName(), person);
		System.out.println(persons);
        return "success";  
    }  
      
	@RequestMapping(value="/{name}", method=RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable String name) {
		persons.remove(name);
	}
	
	@ResponseBody
	@RequestMapping(value="/{name}",method=RequestMethod.PUT)
	public String update(@PathVariable String name,@RequestBody Person person) {
		persons.put(name, person);
		return "success";  
	}
	
}
