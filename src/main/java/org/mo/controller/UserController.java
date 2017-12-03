package org.mo.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/user")
@Controller
public class UserController {
	
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@RequestMapping(value="/login",method=RequestMethod.GET)
	public String login(@RequestParam("username") String name,@RequestParam("password") String password){
		/*
		if("admin".equals(name) && "admin".equals(password)){
			return "index";
		}else{
			return "unauthorized";
		}*/
		
		// 创建subject的实力
		Subject subject = SecurityUtils.getSubject();
		// 判断用户是否登录
		if(subject.isAuthenticated() == false){
			// 用户没有登录，则使用UsernamePasswordToken封装登录信息
			UsernamePasswordToken token = new UsernamePasswordToken(name, password);
			// 使用subject中的login方法执行登陆
			try {
				subject.login(token);
			} catch (AuthenticationException e) {
				// 跳转到失败页面
				logger.info("用户认证失败");
				e.printStackTrace();
				return "error";
			}
		}
		// 跳转到登录成功页面
		return "index";
	}
	
	@RequestMapping("/hello")
	public String hello(){
		System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxx");
		return "index";
	}
}


