package com.jspring.security.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jspring.Strings;
import com.jspring.security.SecurityConfig;
import com.jspring.security.domain.SecurityUser;
import com.jspring.security.domain.SecurityUserDao;
import com.jspring.security.service.SecurityResourceService;
import com.jspring.security.service.SecurityUserService.SecurityUserDetails;
import com.jspring.web.RestResult;
import com.jspring.web.WebUtils;

@Controller
public final class SecurityController {

	private static final Logger log = LoggerFactory.getLogger(SecurityController.class);

	@Autowired
	SecurityResourceService securityResourceService;
	@Autowired
	SecurityUserDao<SecurityUser> securityUserRepository;

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

	@RequestMapping(path = "/sync", method = RequestMethod.POST)
	@ResponseBody
	public RestResult sync(HttpServletRequest request, HttpServletResponse response) {
		securityResourceService.resetResources();
		RestResult r = new RestResult();
		r.path = request.getRequestURI();
		r.status = 200;
		r.error = "SUCC";
		r.message = "Security resources synchronized";
		r.content = null;
		WebUtils.setResponse4IframeAndRest(response);
		log.info("[JSON:" + request.getRequestURI() + "][200][Security resources synchronized]");
		return r;
	}

	@SuppressWarnings({ "unchecked" })
	@RequestMapping(path = "/user", method = RequestMethod.GET)
	@ResponseBody
	public RestResult user(HttpServletRequest request, HttpServletResponse response) {
		try {
			SecurityUserDetails<SecurityUser> details = (SecurityUserDetails<SecurityUser>) SecurityContextHolder
					.getContext().getAuthentication().getPrincipal();
			SecurityUser user = details.getUser();
			user.password = null;
			//
			RestResult r = new RestResult();
			r.path = request.getRequestURI();
			r.status = 200;
			r.error = "SUCC";
			r.message = "User info query successfully!";
			r.content = user;
			WebUtils.setResponse4IframeAndRest(response);
			log.info("[JSON:" + request.getRequestURI() + "][200][" + details.getUsername() + "'s info queryed]");
			return r;
		} catch (Exception e) {
			RestResult r = new RestResult();
			r.status = 500;
			r.path = request.getRequestURI();
			r.error = e.getClass().getName();
			r.message = e.getMessage();
			WebUtils.setResponse4IframeAndRest(response);
			log.warn("[JSON:" + request.getRequestURI() + "][500][" + e.getClass().getName() + "]" + e.getMessage());
			return r;
		}
	}

	@SuppressWarnings({ "unchecked" })
	@RequestMapping(path = "/user", method = RequestMethod.POST)
	@ResponseBody
	public RestResult password(@RequestParam String oldPassword, @RequestParam String newPassword,
			HttpServletRequest request, HttpServletResponse response) {
		try {
			SecurityUserDetails<SecurityUser> details = (SecurityUserDetails<SecurityUser>) SecurityContextHolder
					.getContext().getAuthentication().getPrincipal();
			if (!SecurityConfig.PASSWORD_ENCODER.matches(oldPassword, details.getPassword())) {
				RestResult r = new RestResult();
				r.path = request.getRequestURI();
				r.status = 403;
				r.error = "Access denied";
				r.message = "Illegal old password";
				r.content = null;
				WebUtils.setResponse4IframeAndRest(response);
				log.info("[JSON:" + request.getRequestURI() + "][403][Illegal old password]");
				return r;
			}
			if (Strings.isNullOrEmpty(newPassword)) {
				RestResult r = new RestResult();
				r.path = request.getRequestURI();
				r.status = 403;
				r.error = "Access denied";
				r.message = "Illegal new password";
				r.content = null;
				WebUtils.setResponse4IframeAndRest(response);
				log.info("[JSON:" + request.getRequestURI() + "][403][Illegal new password]");
				return r;
			}
			SecurityUser user = securityUserRepository.findByUserName(details.getUsername());
			user.password = SecurityConfig.PASSWORD_ENCODER.encode(newPassword);
			securityUserRepository.update(user);
			RestResult r = new RestResult();
			r.path = request.getRequestURI();
			r.status = 200;
			r.error = "SUCC";
			r.message = "You have changed your password successfully!";
			r.content = null;
			WebUtils.setResponse4IframeAndRest(response);
			log.info("[JSON:" + request.getRequestURI() + "][200][" + details.getUsername() + "'s password changed]");
			return r;
		} catch (Exception e) {
			RestResult r = new RestResult();
			r.status = 500;
			r.path = request.getRequestURI();
			r.error = e.getClass().getName();
			r.message = e.getMessage();
			WebUtils.setResponse4IframeAndRest(response);
			log.warn("[JSON:" + request.getRequestURI() + "][500][" + e.getClass().getName() + "]" + e.getMessage());
			return r;
		}
	}

}
