package com.jspring.security.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jspring.security.service.SecurityResourceService;
import com.jspring.web.RestResult;
import com.jspring.web.WebUtils;

@Controller
public final class SecurityController {
	private static final Logger log = LoggerFactory.getLogger(SecurityController.class);
	@Autowired
	SecurityResourceService securityResourceService;

	@RequestMapping(path = "/login", method = RequestMethod.GET, produces = "text/html")
	public String loginHtml(HttpServletRequest request, HttpServletResponse response) {
		WebUtils.setResponse4IframeAndRest(response);
		log.info("[HTML:" + request.getRequestURI() + "][200][SUCC]");
		return "login";
	}

	@RequestMapping(path = "/login", method = RequestMethod.GET)
	@ResponseBody
	public RestResult login(HttpServletRequest request, HttpServletResponse response) {
		RestResult r = new RestResult();
		r.path = request.getRequestURI();
		r.status = 403;
		r.error = "Access denied";
		r.message = "You need login";
		r.content = null;
		WebUtils.setResponse4IframeAndRest(response);
		log.info("[JSON:" + request.getRequestURI() + "][403][You need login]");
		return r;
	}

	@RequestMapping(path = "/reset", method = RequestMethod.GET)
	@ResponseBody
	public RestResult reset(HttpServletRequest request, HttpServletResponse response) {
		securityResourceService.resetResources();
		RestResult r = new RestResult();
		r.path = request.getRequestURI();
		r.status = 200;
		r.error = "SUCC";
		r.message = "Securiy resources reloaded";
		r.content = null;
		WebUtils.setResponse4IframeAndRest(response);
		log.info("[JSON:" + request.getRequestURI() + "][200][Securiy resources reloaded]");
		return r;
	}
}
