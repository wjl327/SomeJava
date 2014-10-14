package com.wjl.controller;  
  
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.wjl.vo.User;
  
@Controller  
@RequestMapping("/user")  
public class UserController { 
	
    private final static Map<String, User> users = new HashMap<String, User>();  
      
    //模拟数据源,构造初始数据  
    public UserController(){  
    	users.put("xiaoMi", new User("xiaoMi", "44010223300059", "xiaoMi@163.com"));  
    	users.put("xiaoTian", new User("xiaoTian", "44010883266659", "xiaoTian@yahoo.com"));  
    	users.put("daDong", new User("daDong", "44010552277759", "daDong@qq.com"));  
    	users.put("daShan", new User("daShan", "44010332211159", "daShan@126.com"));
    }  
      
    /** 
     * 添加新用户 
     * @see 访问/user/add时，GET请求就执行addUser(Model model)方法，POST请求就执行addUser(User user)方法 
     */  
    @RequestMapping(value="/add", method=RequestMethod.GET)  
    public String addUser(Model model){  
        //这里要传给前台一个空对象，否则会报告java.lang.IllegalStateException异常  
        //异常信息为Neither BindingResult nor plain target object for bean name 'user' available as request attribute  
        //并且传过去的key值要与前台modelAttribute属性值相同，即model.addAttribute("user", new User());  
        //我们也可以写成下面这种方式，此时SpringMVC会自动把对象名转换为小写值作为key，即User-->user  
        model.addAttribute(new User());  
        return "user/add";  
    }  
    
    @RequestMapping(value="/add", method=RequestMethod.POST)  
    public String addUser(User user){ //这里参数中的user就应该与add.jsp中的modelAttribute="user"一致了  
        users.put(user.getUsername(), user);  
        return "redirect:/user/list";  
    }  
      
    /** 
     * 列出所有用户信息 
     */  
    @RequestMapping("/list")  
    public String list(Model model){  
        model.addAttribute("users", users);  
        return "user/list";  
    }  
      
    /** 
     * 查询用户信息  访问该方法的路径就应该是"/user/具体的用户名" 
     */  
    @RequestMapping(value="/{name}", method=RequestMethod.GET)  
    public String show(@PathVariable String name, Model model){  
        model.addAttribute(users.get(name));  
        return "user/show";  
    }  
      
    /** 
     * 编辑用户信息 
     * @see 访问该方法的路径就应该是"/user/具体的用户名/update" 
     */  
    @RequestMapping(value="/{name}/update", method=RequestMethod.GET)  
    public String update(@PathVariable String name, Model model){  
        model.addAttribute(users.get(name));  
        return "user/update";  
    }  
    @RequestMapping(value="/{name}/update", method=RequestMethod.POST)  
    public String update(User user){  
        users.put(user.getUsername(), user);  
        return "redirect:/user/list"; //也可以retun "forward:/user/list",此时浏览器地址栏会有不同  
    }  
      
    /** 
     * 删除用户信息 
     */  
    @RequestMapping(value="/{name}/delete", method=RequestMethod.GET)  
    public String delete(@PathVariable String name){  
        users.remove(name);  
        return "redirect:/user/list"; //删除完成后显示当前存在的所有用户信息  
    }  
}  