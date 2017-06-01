package com.jspring.web;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.boot.autoconfigure.web.AbstractErrorController;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.AbstractJsonpResponseBodyAdvice;

import com.jspring.web.SimpleErrorController;

@Configuration
@EnableAutoConfiguration
@ComponentScan(value = { "com.jspring.web" })
public class WebConfig extends WebMvcConfigurerAdapter {

	// 支持JSONP
	@Order(2)
	@ControllerAdvice
	public static class JsonpAdvice extends AbstractJsonpResponseBodyAdvice {
		public JsonpAdvice() {
			super("callback");
		}
	}

	// 支持自定义错误
	@Bean
	@ConditionalOnMissingBean(value = ErrorController.class, search = SearchStrategy.CURRENT)
	public AbstractErrorController basicErrorController(ErrorAttributes errorAttributes) {
		return new SimpleErrorController(errorAttributes);
	}

	// @Override
	// protected void configure(HttpSecurity http) throws Exception {
	// http
	// //暂停防CSRF攻击
	// .csrf().disable()
	// //允许在IFRAME中嵌入展示
	// .headers().frameOptions().disable();
	// }

}
