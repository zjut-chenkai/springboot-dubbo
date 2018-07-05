package org.com.ck.controller;

import org.com.ck.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/test")
public class IndexController {
	
	@Autowired
	private TestService testService;
	
	@RequestMapping("/index")
	@ResponseBody
	public String index() {
		System.out.println("controller");
		return testService.testSay();
	}
}
