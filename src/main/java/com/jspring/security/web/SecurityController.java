package com.jspring.security.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.jspring.web.WebConfig;

@Controller
public final class SecurityController {

	@Autowired
	SecurityResourceService securityResourceService;
	@Autowired
	SecurityUserDao<SecurityUser> securityUserRepository;

	@RequestMapping(path = "/login", method = RequestMethod.GET, produces = "text/html")
	public String loginHtml(HttpServletRequest request, HttpServletResponse response) {
		return WebConfig.redirect(() -> {
			return "login";
		}, request, response, RequestMethod.GET);
	}

	@RequestMapping(path = "/login", method = RequestMethod.GET)
	@ResponseBody
	public RestResult login(HttpServletRequest request, HttpServletResponse response) {
		return WebConfig.responseBody(() -> {
			RestResult r = new RestResult();
			r.status = 403;
			r.error = "Access denied";
			r.message = "You need login";
			return r;
		}, request, response, RequestMethod.GET);
	}

	@RequestMapping(path = "/user/reload", method = RequestMethod.PUT)
	@ResponseBody
	public RestResult sync(HttpServletRequest request, HttpServletResponse response) {
		return WebConfig.responseObject(() -> {
			securityResourceService.resetResources();
			return 0;
		}, request, response, RequestMethod.PUT);
	}

	@SuppressWarnings({ "unchecked" })
	@RequestMapping(path = "/user", method = RequestMethod.GET)
	@ResponseBody
	public RestResult user(HttpServletRequest request, HttpServletResponse response) {
		return WebConfig.responseObject(() -> {
			SecurityUserDetails<SecurityUser> details = (SecurityUserDetails<SecurityUser>) SecurityContextHolder
					.getContext().getAuthentication().getPrincipal();
			SecurityUser user = details.getUser();
			user.password = null;
			return user;
		}, request, response, RequestMethod.GET);
	}

	@SuppressWarnings({ "unchecked" })
	@RequestMapping(path = "/user", method = RequestMethod.POST)
	@ResponseBody
	public RestResult password(@RequestParam String oldPassword, @RequestParam String newPassword,
			HttpServletRequest request, HttpServletResponse response) {
		return WebConfig.responseBody(() -> {
			SecurityUserDetails<SecurityUser> details = (SecurityUserDetails<SecurityUser>) SecurityContextHolder
					.getContext().getAuthentication().getPrincipal();
			RestResult r = new RestResult();
			if (!SecurityConfig.PASSWORD_ENCODER.matches(oldPassword, details.getPassword())) {
				r.status = 403;
				r.error = "Access denied";
				r.message = "Illegal old password";
				return r;
			}
			if (Strings.isNullOrEmpty(newPassword)) {
				r.status = 403;
				r.error = "Access denied";
				r.message = "Illegal new password";
				return r;
			}
			SecurityUser user = securityUserRepository.findByUserName(details.getUsername());
			user.password = SecurityConfig.PASSWORD_ENCODER.encode(newPassword);
			securityUserRepository.update(user);
			r.status = 200;
			r.error = details.getUsername() + " password changed";
			r.message = "You have changed your password successfully!";
			return r;
		}, request, response, RequestMethod.POST);
	}

}
