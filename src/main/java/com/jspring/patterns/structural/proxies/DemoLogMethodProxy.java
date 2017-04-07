package com.jspring.patterns.structural.proxies;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DemoLogMethodProxy<T> extends MethodProxy<T> {

	private static Logger log = LoggerFactory.getLogger(DemoLogMethodProxy.class);

	protected DemoLogMethodProxy(T source) {
		super(source);
	}

	@Override
	protected void preInvode(T source, Method method, Object[] args) {
		log.info(method.getName() + " start ...");
	}

	@Override
	protected void afterInvode(T source, Method method, Object[] args, Object result) {
		log.info(method.getName() + " end.");
	}

	public static <T> T newProxy(T source) {
		return new DemoLogMethodProxy<T>(source).getProxy();
	}

}
