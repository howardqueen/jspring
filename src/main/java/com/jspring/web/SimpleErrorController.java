package com.jspring.web;

import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.AbstractErrorController;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jspring.Exceptions;
import com.jspring.date.DateFormats;

@Controller
public class SimpleErrorController extends AbstractErrorController {

	private static final Logger log = LoggerFactory.getLogger(SimpleErrorController.class.getSimpleName());

	public SimpleErrorController(ErrorAttributes errorAttributes) {
		super(errorAttributes);
	}

	@Override
	public String getErrorPath() {
		return "/error";
	}

	@RequestMapping(value = "/error", produces = "text/html")
	public void errorHtml(HttpServletRequest request, HttpServletResponse response) {
		try {
			response.setContentType(ContentTypes.html.value);
			Map<String, Object> body = getErrorAttributes(request, getTraceParameter(request));
			log.error("[HTML:" + request.getMethod() + ":" + body.get("path") + "][" + body.get("status") + "]["
					+ body.get("error") + "]\r\n" + body.get("message"));
			WebConfig.setResponse4IframeAndRest(response);
			PrintWriter writer = response.getWriter();
			writer.write("<html>\r\n<head>\r\n<meta charset=\"UTF-8\" />\r\n");
			writer.write("<title>ERROR</title>\r\n");
			writer.write("</head>\r\n<body>\r\n");
			writer.write("<h2 style=\"color:red\">");
			writer.write(body.get("status").toString());
			writer.write(":");
			writer.write(body.get("error").toString());
			writer.write("</h2>\r\n");
			writer.write("<p>");
			String message = body.get("message").toString();
			writer.write(message.length() > 100 ? message.substring(0, 100) + "..." : message);
			writer.write("</p>\r\n");
			writer.write("<p>");
			writer.write("REQUEST_URI:");
			writer.write(String.valueOf(body.get("path")));
			writer.write("</p>\r\n");
			writer.write("<p>");
			writer.write("TIMESTAMP:");
			writer.write(DateFormats.dateTime.format(body.get("timestamp")));
			writer.write("</body>\r\n<html>");
			writer.flush();
		} catch (Exception e) {
			log.error(Exceptions.getStackTrace(e));
		}
	}

	@ResponseBody
	@RequestMapping(value = "/error")
	public Object error(HttpServletRequest request, HttpServletResponse response) {
		return WebConfig.responseObjectWithoutLog(() -> {
			response.setContentType(ContentTypes.js.value);
			Map<String, Object> body = getErrorAttributes(request, getTraceParameter(request));
			log.error("[JSON:" + request.getMethod() + ":" + body.get("path") + "][" + body.get("status") + "]["
					+ body.get("error") + "]\r\n" + body.get("message"));
			RestResult r = new RestResult();
			r.path = String.valueOf(body.get("path"));
			r.status = (Integer) body.get("status");
			r.error = body.get("error").toString();
			r.message = body.get("message").toString();
			if (r.message.length() > 100) {
				r.message = r.message.substring(0, 100) + "...";
			}
			return r;
		}, response);
	}

}