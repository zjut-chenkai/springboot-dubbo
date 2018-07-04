package org.com.ck.service.impl;

import org.com.ck.service.ManService;

import com.alibaba.dubbo.config.annotation.Service;

@Service(version = "1.0.0")
public class ManServiceImpl implements ManService{

	@Override
	public String say(String word) {
		System.err.println("我说：" + word);
		return "我说：" + word;
	}

}
