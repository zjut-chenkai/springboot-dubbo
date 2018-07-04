package org.com.ck.service.impl;

import org.com.ck.service.ManService;
import org.com.ck.service.TestService;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;

@Service
public class TestServiceImpl implements TestService{

	@Reference(version = "1.0.0")
	ManService manService;
	
	@Override
	public String testSay() {
		// TODO Auto-generated method stub
		return manService.say("123456");
	}

}
