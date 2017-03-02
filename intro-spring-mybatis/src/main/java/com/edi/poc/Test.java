package com.edi.poc;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.edi.poc.domain.User;
import com.edi.poc.service.UserService;

public class Test {
	
	public static void main(String[] args) {
		ApplicationContext ctx = null;
		UserService userService = null;
		
		//ctx = new ClassPathXmlApplicationContext("applicationContext");
		ctx = new FileSystemXmlApplicationContext("src/main/resources/applicationContext.xml");
		userService = (UserService)ctx.getBean("userService");
		
		User u = new User();
		u.setName("test");
		u.setAge(10);
		u.setSex("M");
		u.setPassword("?");
		userService.add(u);
	}

}
