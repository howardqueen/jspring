package com.jspring.web;

import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.function.BiFunction;
import java.util.function.Function;
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
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.AbstractJsonpResponseBodyAdvice;

import com.jspring.Encodings;
import com.jspring.Exceptions;
import com.jspring.Strings;
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
			HttpServletResponse response) {
		String templatePath = templatePathSupplier.get();
		setResponse4IframeAndRest(response);
		log.info("[HTML:" + request.getMethod() + ":" + request.getRequestURI() + "][200][SUCC]");
		return templatePath;
	}

	public static void responseCsv(Function<PrintWriter, String> contentWriter, HttpServletRequest request,
			HttpServletResponse response) {
		try (PrintWriter writer = response.getWriter()) {
			String title = contentWriter.apply(writer);
			setResponse4Csv(response, title);
			log.info("[CSV:" + request.getMethod() + ":" + request.getRequestURI() + "][200][SUCC]");
		} catch (Exception er) {
			Exceptions e = Exceptions.newInstance(er);
			response.reset();
			setResponse4Rest(response);
			try (PrintWriter writer = response.getWriter()) {
				writer.write(er.getClass().getName());
				writer.write("\r\n");
				writer.write(e.getMessage());
			} catch (Exception e1) {
			}
			log.warn("[CSV:" + request.getMethod() + ":" + request.getRequestURI() + "][500]" + e.getMessage());
		}
	}

	public static <R> R responseBodyWithoutLog(Supplier<R> contentSupplier, BiFunction<String, String, R> errorFunction,
			HttpServletResponse response) {
		try {
			R r = contentSupplier.get();
			setResponse4IframeAndRest(response);
			return r;
		} catch (Exception e) {
			R r = errorFunction.apply(e.getClass().getName(), Exceptions.newInstance(e).getMessage());
			setResponse4IframeAndRest(response);
			return r;
		}
	}

	public static <R> R responseBody(Supplier<R> contentSupplier, BiFunction<String, String, R> errorFunction,
			HttpServletRequest request, HttpServletResponse response) {
		try {
			R r = contentSupplier.get();
			setResponse4IframeAndRest(response);
			log.info("[JSON:" + request.getMethod() + ":" + request.getRequestURI() + "][200][SUCC]");
			return r;
		} catch (Exception er) {
			Exceptions e = Exceptions.newInstance(er);
			R r = errorFunction.apply(er.getClass().getName(), e.getMessage());
			setResponse4IframeAndRest(response);
			log.warn("[JSON:" + request.getMethod() + ":" + request.getRequestURI() + "][500]" + e.getMessage());
			return r;
		}
	}

	public static RestResult responseBody(Supplier<RestResult> contentSupplier, HttpServletRequest request,
			HttpServletResponse response) {
		return responseBody(() -> {
			RestResult r = contentSupplier.get();
			r.path = request.getRequestURI();
			return r;
		}, (name, message) -> {
			RestResult r = new RestResult();
			r.status = 500;
			r.path = request.getRequestURI();
			r.error = name;
			r.message = Strings.isNullOrEmpty(message) ? null
					: (message.length() > 100 ? message.substring(0, 100) + "..." : message);
			return r;
		}, request, response);
	}

	public static RestResult responseObject(Supplier<Object> contentSupplier, HttpServletRequest request,
			HttpServletResponse response) {
		return responseBody(() -> {
			RestResult r = new RestResult();
			r.status = 200;
			r.message = request.getMethod() + " SUCCESS";
			r.content = contentSupplier.get();
			return r;
		}, (name, message) -> {
			RestResult r = new RestResult();
			r.status = 500;
			r.path = request.getRequestURI();
			r.error = name;
			r.message = Strings.isNullOrEmpty(message) ? null
					: (message.length() > 100 ? message.substring(0, 100) + "..." : message);
			return r;
		}, request, response);
	}

	public static Object responseObjectWithoutLog(Supplier<Object> contentSupplier, HttpServletResponse response) {
		return responseBodyWithoutLog(() -> {
			return contentSupplier.get();
		}, (name, message) -> {
			return name + "," + (Strings.isNullOrEmpty(message) ? null
					: (message.length() > 100 ? message.substring(0, 100) + "..." : message));
		}, response);
	}

}
