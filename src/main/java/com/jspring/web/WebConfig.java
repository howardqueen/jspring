package com.jspring.web;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.AbstractJsonpResponseBodyAdvice;

import com.jspring.Encodings;
import com.jspring.Environment;
import com.jspring.Exceptions;
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

	private static final Logger log = LoggerFactory.getLogger(WebConfig.class);

	/**
	 * 允许IFRAME嵌套以及PUT/DELETE
	 * 
	 * @param response
	 */
	public static void setResponse4IframeAndRest(HttpServletResponse response) {
		setResponse4Iframe(response);
		setResponse4Rest(response);
	}

	/**
	 * 允许IFRAME嵌套
	 * 
	 * @param response
	 */
	public static void setResponse4Iframe(HttpServletResponse response) {
		response.setHeader("x-frame-options", "ALLOW-FROM");
	}

	/**
	 * 允许PUT/DELETE
	 * 
	 * @param response
	 */
	public static void setResponse4Rest(HttpServletResponse response) {
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "*");
		response.setHeader("Access-Control-Allow-Headers", "x-requested-with,content-type");
	}

	/**
	 * CSV头文件
	 * 
	 * @param response
	 * @param filename
	 * @throws UnsupportedEncodingException
	 */
	public static void setResponse4Csv(HttpServletResponse response, String filename)
			throws UnsupportedEncodingException {
		response.setContentType("APPLICATION/OCTET-STREAM");
		response.setCharacterEncoding(Encodings.GB2312.value);
		response.setHeader("Content-Disposition",
				"attachment; filename=" + URLEncoder.encode(filename + ".csv", Encodings.UTF8.value));
	}

	public static String redirect(Supplier<String> templatePathSupplier, HttpServletRequest request,
			HttpServletResponse response, RequestMethod method) {
		String templatePath = templatePathSupplier.get();
		setResponse4IframeAndRest(response);
		log.info("[HTML:" + method + ":" + request.getRequestURI() + "][200][SUCC]");
		return templatePath;
	}

	public static <R> R responseBody(Supplier<R> contentSupplier, BiFunction<String, String, R> errorFunction,
			HttpServletRequest request, HttpServletResponse response, RequestMethod method) {
		try {
			R r = contentSupplier.get();
			setResponse4IframeAndRest(response);
			log.info("[JSON:" + method + ":" + request.getRequestURI() + "][200][SUCC]");
			return r;
		} catch (RuntimeException e) {
			String error = Exceptions.getStackTrace(e);
			R r = errorFunction.apply(e.getClass().getSimpleName(), error);
			setResponse4IframeAndRest(response);
			log.warn("[JSON:" + method + ":" + request.getRequestURI() + "][500][" + e.getClass().getSimpleName() + "]"
					+ Environment.NewLine + error);
			return r;
		} catch (Exception e) {
			String error = e.getMessage();
			R r = errorFunction.apply(e.getClass().getName(), error);
			setResponse4IframeAndRest(response);
			log.warn("[JSON:" + method + ":" + request.getRequestURI() + "][500][" + e.getClass().getName() + "]"
					+ error);
			return r;
		}
	}

	public static RestResult responseBody(Supplier<RestResult> contentSupplier, HttpServletRequest request,
			HttpServletResponse response, RequestMethod method) {
		return responseBody(() -> {
			RestResult r = contentSupplier.get();
			r.path = request.getRequestURI();
			return r;
		}, (name, message) -> {
			RestResult r = new RestResult();
			r.status = 500;
			r.path = request.getRequestURI();
			r.error = name;
			r.message = message;
			return r;
		}, request, response, method);
	}

	public static RestResult responseObject(Supplier<Object> contentSupplier, HttpServletRequest request,
			HttpServletResponse response, RequestMethod method) {
		return responseBody(() -> {
			RestResult r = new RestResult();
			r.status = 200;
			r.message = method + " SUCCESS";
			r.content = contentSupplier.get();
			return r;
		}, (name, message) -> {
			RestResult r = new RestResult();
			r.status = 500;
			r.path = request.getRequestURI();
			r.error = name;
			r.message = message;
			return r;
		}, request, response, method);
	}

}
