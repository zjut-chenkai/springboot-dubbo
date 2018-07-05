package org.com.ck.util;

import static java.lang.Class.forName;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.util.CollectionUtils;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.rpc.service.GenericService;

/**
 * Created by on 2017/10/23.
 */

public class DubboCallbackUtil {

	// 当前应用的信息
	private static ApplicationConfig application = new ApplicationConfig();
	// 注册中心信息缓存
	private static Map<String, RegistryConfig> registryConfigCache = new ConcurrentHashMap<>();

	// 各个业务方的ReferenceConfig缓存
	@SuppressWarnings("rawtypes")
	private static Map<String, ReferenceConfig> referenceCache = new ConcurrentHashMap<>();

	static {
		application.setName("consumer-test");
	}

	/**
	 * 获取注册中心信息
	 *
	 * @param address
	 *            zk注册地址
	 * @param group
	 *            dubbo服务所在的组
	 * @return
	 */
	private static RegistryConfig getRegistryConfig(String address, String group, String version) {
		String key = address + "-" + group + "-" + version;
		RegistryConfig registryConfig = registryConfigCache.get(key);
		if (null == registryConfig) {
			registryConfig = new RegistryConfig();
			if (StringUtils.isNotEmpty(address)) {
				registryConfig.setAddress(address);
			}
			if (StringUtils.isNotEmpty(version)) {
				registryConfig.setVersion(version);
			}
			if (StringUtils.isNotEmpty(group)) {
				registryConfig.setGroup(group);
			}
			registryConfigCache.put(key, registryConfig);
		}
		return registryConfig;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static ReferenceConfig<Object> getReferenceConfig(String interfaceName, String address, String group,
			String version) {
		String referenceKey = interfaceName + "-" + version;

		ReferenceConfig<Object> referenceConfig = referenceCache.get(referenceKey);
		if (null == referenceConfig) {
			try {
				referenceConfig = new ReferenceConfig<>();
				referenceConfig.setApplication(application);
				referenceConfig.setRegistry(getRegistryConfig(address, group, version));
				Class interfaceClass = forName(interfaceName);
				referenceConfig.setInterface(interfaceClass);
				if (StringUtils.isNotEmpty(version)) {
					referenceConfig.setVersion(version);
				}
				referenceConfig.setGeneric(true);
				// referenceConfig.setUrl("dubbo://10.1.50.167:20880/com.test.service.HelloService");
				referenceCache.put(referenceKey, referenceConfig);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return referenceConfig;
	}

	public static Object invoke(String interfaceName, String methodName, List<Object> paramList, String address,
			String version) {
		ReferenceConfig<Object> reference = getReferenceConfig(interfaceName, address, null, version);
		if (null != reference) {
			GenericService genericService = (GenericService) reference.get();
			if (genericService == null) {
				System.out.println("GenericService 不存在:{}"+ interfaceName);
				return null;
			}

			Object[] paramObject = null;
			if (!CollectionUtils.isEmpty(paramList)) {
				paramObject = new Object[paramList.size()];
				for (int i = 0; i < paramList.size(); i++) {
					paramObject[i] = paramList.get(i);
				}
			}

			Object resultParam = genericService.$invoke(methodName, getMethodParamType(interfaceName, methodName),
					paramObject);
			return resultParam;
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	public static String[] getMethodParamType(String interfaceName, String methodName) {
		try {
			// 创建类
			Class<?> class1 = Class.forName(interfaceName);
			// 获取所有的公共的方法
			Method[] methods = class1.getMethods();
			for (Method method : methods) {
				if (method.getName().equals(methodName)) {
					Class[] paramClassList = method.getParameterTypes();
					String[] paramTypeList = new String[paramClassList.length];
					int i = 0;
					for (Class className : paramClassList) {
						paramTypeList[i] = className.getName();
						i++;
					}
					return paramTypeList;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}
}
