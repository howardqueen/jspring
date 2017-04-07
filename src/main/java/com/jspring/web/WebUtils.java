package com.jspring.web;

import java.io.UnsupportedEncodingException;
//import java.lang.reflect.Field;
import java.net.URLEncoder;

//import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jspring.Encodings;

public class WebUtils {

	private WebUtils() {
	}

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

}
