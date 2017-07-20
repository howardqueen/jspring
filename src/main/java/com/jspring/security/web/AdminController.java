package com.jspring.security.web;

import java.util.List;
import java.util.function.UnaryOperator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jspring.data.Where;
import com.jspring.security.service.ISecurityResourceService;
import com.jspring.web.RestResult;
import com.jspring.web.WebConfig;

@RequestMapping(path = "security/admin")
@Controller
public final class AdminController extends BaseCrudController {
	///////////////////
	/// READ
	///////////////////
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

	///////////////////
	/// WRITE
	///////////////////
	@Override
	protected boolean validWriteDomain(Class<?> domain) {
		return true;
	}

	@Override
	protected Object decorateWriteEntity(Object entity) {
		return entity;
	}

	@Override
	protected UnaryOperator<String> decorateWriteMap(Class<?> domain, UnaryOperator<String> map) {
		return map;
	}

	///////////////////
	/// MORE
	///////////////////
	@Autowired
	private ISecurityResourceService securityResourceService;

	@RequestMapping(path = "reload", method = RequestMethod.PUT)
	@ResponseBody
	public RestResult reload(HttpServletRequest request, HttpServletResponse response) {
		return WebConfig.responseRestObject(() -> {
			securityResourceService.resetResources();
			return 0;
		}, request, response);
	}

}
