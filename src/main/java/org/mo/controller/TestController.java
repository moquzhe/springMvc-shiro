package org.mo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/test")
public class TestController {

	@RequestMapping("/test")
	public String test(){
		return "index";
	}
	
	@RequestMapping(value="/login",method=RequestMethod.GET)
	public String login(@RequestParam("username") String name,@RequestParam("password") String password){
		
		if("admin".equals(name) && "admin".equals(password)){
			return "index";
		}else{
			return "unauthorized";
		}
	}
}
