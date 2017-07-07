package com.jspring.web;

import java.util.function.UnaryOperator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.jspring.Exceptions;
import com.jspring.data.Where;

@RequestMapping(path = "admin")
@Controller
public final class AdminCrudController extends CrudController {

	@Value("${crud.domain.packages}")
	private String[] domainPackages;

	@Override
	protected Class<?> getVisitDomain(String domainSimpleClassName) {
		Class<?> t = null;
		String domain = null;
		for (String p : domainPackages) {
			domain = p + '.' + domainSimpleClassName;
			try {
				t = Class.forName(domain);
				log.info("LOAD DOMAIN: " + domain);
				break;
			} catch (Exception e) {
			}
		}
		if (null == t) {
			throw Exceptions.newInstance("DOMAIN NOT EXISTS: " + domain);
		}
		return t;
	}

	@Override
	protected Where[] getReadFilters(Class<?> domain, String filters) {
		return Where.deserialize(filters);
	}

	@Override
	protected Object getReadEntity(Object entity) {
		return entity;
	}

	@Override
	protected Object getWriteEntity(Object entity) {
		return entity;
	}

	@Override
	protected UnaryOperator<String> getWriteMap(Class<?> domain, UnaryOperator<String> map) {
		return map;
	}

}
