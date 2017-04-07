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

import com.jspring.date.DateFormats;

@Controller
public class SimpleErrorController extends AbstractErrorController {
	private static final Logger log = LoggerFactory.getLogger(SimpleErrorController.class);

	public SimpleErrorController(ErrorAttributes errorAttributes) {
		super(errorAttributes);
	}

	@Override
	public String getErrorPath() {
		return "/error";
	}

	@RequestMapping(value = "/error", produces = "text/html")
	public void errorHtml(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> body = (getErrorAttributes(request, getTraceParameter(request)));
		log.warn("[HTML:" + request.getRequestURI() + "][" + body.get("status") + "][" + body.get("error") + "]"
				+ body.get("message"));
		try {
			WebUtils.setResponse4IframeAndRest(response);
			PrintWriter writer = response.getWriter();
			writer.write("<html><head><meta charset=\"UTF-8\" />");
			writer.write("<title>ERROR</title>");
			writer.write("</head><body>");
			writer.write("<h2 style=\"color:red\">");
			writer.write(body.get("status").toString());
			writer.write(":");
			writer.write(body.get("error").toString());
			writer.write("</h2>");
			writer.write("<p>");
			writer.write(body.get("message").toString());
			writer.write("</p>");
			writer.write("<p>");
			writer.write("REQUEST_URI:");
			writer.write(request.getRequestURI());
			writer.write("</p>");
			writer.write("<p>");
			writer.write("TIMESTAMP:");
			writer.write(DateFormats.dateTime.format(body.get("timestamp")));
			writer.write("</p>");
			writer.write("</body><html>");
			writer.flush();
		} catch (Exception e) {
		}
	}

	@ResponseBody
	@RequestMapping(value = "/error")
	public RestResult error(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> body = getErrorAttributes(request, getTraceParameter(request));
		log.warn("[JSON:" + request.getRequestURI() + "][" + body.get("status") + "][" + body.get("error") + "]"
				+ body.get("message"));
		WebUtils.setResponse4IframeAndRest(response);
		RestResult r = new RestResult();
		r.path = request.getRequestURI();
		r.status = (Integer) body.get("status");
		r.error = body.get("error").toString();
		r.message = body.get("message").toString();
		r.content = DateFormats.dateTime.format(body.get("timestamp"));
		return r;
	}

}