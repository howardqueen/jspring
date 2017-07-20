package com.jspring.security.web;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.jspring.data.Where;

@RequestMapping(path = "security/visitor")
@Controller
public final class VisitorController extends BaseReadController {

	@Override
	protected boolean validReadDomain(Class<?> domain) {
		return true;
	}

	@Override
	protected Object decorateReadEntity(Object entity) {
		return entity;
	}

	@Override
	protected boolean validReadFilters(Class<?> domain, List<Where> wheres) {
		return true;
	}

}
