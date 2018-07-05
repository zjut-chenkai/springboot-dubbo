package org.com.ck.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.com.ck.service.TestService;
import org.com.ck.util.DubboCallbackUtil;
import org.springframework.stereotype.Service;

@Service
public class TestServiceImpl implements TestService {

	/*
	 * @Reference(version = "1.0.0") ManService manService;
	 */

	@Override
	public String testSay() {
		System.out.println("service");
		List<Object> paramList = new ArrayList<>();
		paramList.add("123456");
		String redirecUrl = (String) DubboCallbackUtil.invoke("org.com.ck.service.ManService", "say", paramList,
				"zookeeper://127.0.0.1:2181?backup=127.0.0.1:2182,127.0.0.1:2183", "1.0.0");

		return redirecUrl;
	}

}
