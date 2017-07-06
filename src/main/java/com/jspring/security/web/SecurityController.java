package com.jspring.security.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jspring.security.domain.SecurityUser;
import com.jspring.security.service.SecurityResourceService;
import com.jspring.security.service.SecurityUserService.SecurityUserDetails;
import com.jspring.web.RestResult;
import com.jspring.web.WebConfig;

@Controller
public final class SecurityController {

	@Autowired
	private SecurityResourceService securityResourceService;
	// @Autowired
	// private SecurityUserRepository<? extends SecurityUser>
	// securityUserRepository;

	@RequestMapping(path = "/login", method = RequestMethod.GET, produces = "text/html")
	public String loginHtml(HttpServletRequest request, HttpServletResponse response) {
		return WebConfig.redirect(() -> {
			return "login";
		}, request, response);
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
		}, request, response);
	}

	@RequestMapping(path = "/security/reload", method = RequestMethod.PUT)
	@ResponseBody
	public RestResult sync(HttpServletRequest request, HttpServletResponse response) {
		return WebConfig.responseObject(() -> {
			securityResourceService.resetResources();
			return 0;
		}, request, response);
	}

	/**
	 * 获取用户信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(path = "/security/user", method = RequestMethod.GET)
	@ResponseBody
	public RestResult user(HttpServletRequest request, HttpServletResponse response) {
		return WebConfig.responseObject(() -> {
			SecurityUserDetails details = (SecurityUserDetails) SecurityContextHolder.getContext().getAuthentication()
					.getPrincipal();
			SecurityUser user = details.getUser();
			user.password = null;
			return user;
		}, request, response);
	}

	// /**
	// * 更改密码
	// *
	// * @param oldPassword
	// * @param newPassword
	// * @param request
	// * @param response
	// * @return
	// */
	// @RequestMapping(path = "/security/user", method = RequestMethod.PUT)
	// @ResponseBody
	// public RestResult changePassword(@RequestParam String oldPassword,
	// @RequestParam String newPassword,
	// HttpServletRequest request, HttpServletResponse response) {
	// return WebConfig.responseBody(() -> {
	// SecurityUserDetails details = (SecurityUserDetails)
	// SecurityContextHolder.getContext().getAuthentication()
	// .getPrincipal();
	// RestResult r = new RestResult();
	// if (!SecurityConfig.PASSWORD_ENCODER.matches(oldPassword,
	// details.getPassword())) {
	// r.status = 403;
	// r.error = "Access denied";
	// r.message = "Illegal old password";
	// return r;
	// }
	// if (Strings.isNullOrEmpty(newPassword)) {
	// r.status = 403;
	// r.error = "Access denied";
	// r.message = "Illegal new password";
	// return r;
	// }
	// SecurityUser user =
	// securityUserRepository.findByUserName(details.getUsername());
	// user.password = SecurityConfig.PASSWORD_ENCODER.encode(newPassword);
	// securityUserRepository.updateOne(user);
	// r.status = 200;
	// r.message = "Password changed!";
	// return r;
	// }, request, response);
	// }
	//
	// /**
	// * 重置密码
	// */
	// @RequestMapping(path = "/security/user/{userId}", method =
	// RequestMethod.PUT)
	// @ResponseBody
	// public RestResult resetPassword(@PathVariable Integer userId,
	// HttpServletRequest request,
	// HttpServletResponse response) {
	// return WebConfig.responseBody(() -> {
	// RestResult r = new RestResult();
	// SecurityUser user = securityUserRepository.findOne(userId);
	// user.password = SecurityConfig.PASSWORD_123456;
	// securityUserRepository.updateOne(user);
	// r.status = 200;
	// r.message = "Password reset!";
	// return r;
	// }, request, response);
	// }

}
