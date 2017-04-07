package com.jspring.patterns.structural.decorators;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jspring.patterns.structural.proxies.DemoLogMethodProxy;

public class DemoLogMethodProxySwitcher<T> extends DemoLogMethodProxy<T> {

	private static Logger log = LoggerFactory.getLogger(DemoLogMethodProxySwitcher.class);

	protected DemoLogMethodProxySwitcher(T source) {
		super(source);
	}

	private boolean enable;

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean value) {
		this.enable = value;
	}

	@Override
	protected void preInvode(T source, Method method, Object[] args) {
		if (isEnable()) {
			log.info(method.getName() + " start ...");
		}
	}

	@Override
	protected void afterInvode(T source, Method method, Object[] args, Object result) {
		if (isEnable()) {
			log.info(method.getName() + " end.");
		}
	}

}
