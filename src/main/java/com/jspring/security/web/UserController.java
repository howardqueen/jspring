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
import com.jspring.security.domain.ISecurityUserDetails;
import com.jspring.security.service.ISecurityUserService;
import com.jspring.web.RestResult;
import com.jspring.web.WebConfig;

@Controller
public final class UserController {

	@RequestMapping(path = "/login", method = RequestMethod.GET, produces = "text/html")
	public String loginHtml(HttpServletRequest request, HttpServletResponse response) {
		return WebConfig.responseTemplate("login", request, response);
	}

	@RequestMapping(path = "/login", method = RequestMethod.GET)
	@ResponseBody
	public RestResult login(HttpServletRequest request, HttpServletResponse response) {
		return WebConfig.responseRestResult(() -> {
			RestResult r = new RestResult();
			r.status = 403;
			r.error = "Access denied";
			r.message = "You need login";
			return r;
		}, request, response);
	}

	/**
	 * 获取用户信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(path = "security/user", method = RequestMethod.GET)
	@ResponseBody
	public RestResult user(HttpServletRequest request, HttpServletResponse response) {
		return WebConfig.responseRestObject(() -> {
			return securityUserService.getUserInfo();
		}, request, response);
	}

	@Autowired
	private ISecurityUserService securityUserService;

	/**
	 * 更改密码
	 * 
	 * @param oldPassword
	 * @param newPassword
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(path = "security/user", method = RequestMethod.PUT)
	@ResponseBody
	public RestResult changePassword(@RequestParam String oldPassword, @RequestParam String newPassword,
			@RequestParam String confirmPassword, HttpServletRequest request, HttpServletResponse response) {
		return WebConfig.responseRestObject(() -> {
			ISecurityUserDetails user = (ISecurityUserDetails) SecurityContextHolder.getContext().getAuthentication()
					.getPrincipal();
			RestResult r = new RestResult();
			if (securityUserService.getDefaultPassword().equals(user.getPassword())) {
				if (!securityUserService.getDefaultPassword().equals(oldPassword)) {
					r.status = 403;
					r.error = "Access denied";
					r.message = "Illegal old password";
					return r;
				}
			} else {
				if (!securityUserService.getPasswordEncoder().matches(oldPassword, user.getPassword())) {
					r.status = 403;
					r.error = "Access denied";
					r.message = "Illegal old password";
					return r;
				}
			}
			if (Strings.isNullOrEmpty(newPassword) || !newPassword.equals(confirmPassword)) {
				r.status = 403;
				r.error = "Access denied";
				r.message = "Illegal new password";
				return r;
			}
			securityUserService.changePassword(user.getUserId(),
					securityUserService.getPasswordEncoder().encode(newPassword));
			r.status = 200;
			r.message = "Password changed!";
			return r;
		}, request, response);
	}

}
