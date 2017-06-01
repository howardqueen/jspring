package com.jspring.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jspring.Encodings;
import com.jspring.Exceptions;
import com.jspring.Strings;
import com.jspring.data.WebDao;
import com.jspring.data.CrudColumnInfo;
import com.jspring.data.DaoOrder;
import com.jspring.data.DaoWhere;
import com.jspring.data.DataManager;
import com.jspring.date.DateFormats;

@Controller
public final class RestCrudController implements ApplicationContextAware {

	private static final Logger log = LoggerFactory.getLogger(RestCrudController.class);

	private ApplicationContext context;

	protected ApplicationContext getContext() {
		return context;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		context = applicationContext;
	}

	@Autowired
	DataManager dataManager;

	protected String getUpperFirstLetter(String domain) {
		return (char) (domain.charAt(0) - 32) + domain.substring(1);
	}

	@Value("${jspring.web.crud-domain-packages}")
	String[] domains;
	private Map<String, WebDao<?>> repositories = new HashMap<>();

	private WebDao<?> getDao(String domain, HttpServletRequest request) throws Exception {
		if (repositories.containsKey(domain)) {
			return (WebDao<?>) repositories.get(domain);
		}
		synchronized (this) {
			if (repositories.containsKey(domain)) {
				return (WebDao<?>) repositories.get(domain);
			}
			String f = domain + "Repository";
			if (getContext().containsBean(f)) {
				log.info("LOAD REPOSITORY: " + f);
				WebDao<?> dao = (WebDao<?>) getContext().getBean(f);
				repositories.put(domain, dao);
				return (WebDao<?>) dao;
			}
			Class<?> t = null;
			for (String d : domains) {
				f = d + "." + getUpperFirstLetter(domain);
				try {
					t = Class.forName(f);
					log.info("LOAD DOMAIN: " + f);
					break;
				} catch (Exception e) {
				}
			}
			if (null == t) {
				log.debug("DOMAIN NOT EXISTS: " + domain);
				throw new Exception("DOMAIN NOT EXISTS: " + domain);
			}
			@SuppressWarnings({ "rawtypes", "unchecked" })
			WebDao<?> dao = new WebDao(dataManager, t);
			repositories.put(domain, dao);
			return (WebDao<?>) dao;
		}
	}

	///////////////////
	/// CRUD
	///////////////////
	@ResponseBody
	@RequestMapping(path = "crud/{domain}", method = RequestMethod.PUT)
	public RestResult create(@PathVariable String domain, HttpServletRequest request, HttpServletResponse response) {
		try {
			int c = getDao(domain, request).insert(request);
			if (c <= 0) {
				throw new RuntimeException("Rows affected 0");
			}
			//
			RestResult r = new RestResult();
			r.status = 200;
			r.path = request.getRequestURI();
			r.error = "SUCCESS";
			r.message = "CREATE DONE";
			//
			WebUtils.setResponse4IframeAndRest(response);
			// log.info("[JSON:" + request.getRequestURI() + "][200][SUCC]");
			return r;
		} catch (NullPointerException e) {
			RestResult r = new RestResult();
			r.status = 500;
			r.path = request.getRequestURI();
			r.error = e.getClass().getName();
			r.message = Exceptions.getStackTrace(e);
			WebUtils.setResponse4IframeAndRest(response);
			log.warn("[JSON:" + request.getRequestURI() + "][500][" + e.getClass().getName() + "]"
					+ Exceptions.getStackTrace(e));
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

	@ResponseBody
	@RequestMapping(path = "crud/{domain}/{id}", method = RequestMethod.POST)
	public RestResult update(@PathVariable String domain, @PathVariable String id, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			if (id.indexOf('%') >= 0) {
				id = URLDecoder.decode(id, Encodings.UTF8.value);
			}
			int c = getDao(domain, request).update(request, id);
			if (c <= 0) {
				throw new RuntimeException("Rows affected 0");
			}
			//
			RestResult r = new RestResult();
			r.status = 200;
			r.path = request.getRequestURI();
			r.error = "SUCCESS";
			r.message = "UPDATE DONE";
			//
			WebUtils.setResponse4IframeAndRest(response);
			log.info("[JSON:" + request.getRequestURI() + "][200][SUCC]");
			return r;
		} catch (NullPointerException e) {
			RestResult r = new RestResult();
			r.status = 500;
			r.path = request.getRequestURI();
			r.error = e.getClass().getName();
			r.message = Exceptions.getStackTrace(e);
			WebUtils.setResponse4IframeAndRest(response);
			log.warn("[JSON:" + request.getRequestURI() + "][500][" + e.getClass().getName() + "]"
					+ Exceptions.getStackTrace(e));
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

	@ResponseBody
	@RequestMapping(path = "crud/{domain}/{id}", method = RequestMethod.DELETE)
	public RestResult delete(@PathVariable String domain, @PathVariable String id, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			if (id.indexOf('%') >= 0) {
				id = URLDecoder.decode(id, Encodings.UTF8.value);
			}
			WebDao<?> dao = getDao(domain, request);
			dao.delete(id, dao.getPartitionDate(request));
			//
			RestResult r = new RestResult();
			r.status = 200;
			r.path = request.getRequestURI();
			r.error = "SUCCESS";
			r.message = "DELETE DONE";
			//
			WebUtils.setResponse4IframeAndRest(response);
			log.info("[JSON:" + request.getRequestURI() + "][200][SUCC]");
			return r;
		} catch (NullPointerException e) {
			RestResult r = new RestResult();
			r.status = 500;
			r.path = request.getRequestURI();
			r.error = e.getClass().getName();
			r.message = Exceptions.getStackTrace(e);
			WebUtils.setResponse4IframeAndRest(response);
			log.warn("[JSON:" + request.getRequestURI() + "][500][" + e.getClass().getName() + "]"
					+ Exceptions.getStackTrace(e));
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

	@ResponseBody
	@RequestMapping(path = "crud/{domain}/{id}", method = RequestMethod.GET)
	public RestResult findOne(@PathVariable String domain, @PathVariable String id, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			if (id.indexOf('%') >= 0) {
				id = URLDecoder.decode(id, Encodings.UTF8.value);
			}
			RestResult r = new RestResult();
			WebDao<?> dao = getDao(domain, request);
			r.content = dao.findOne(id, dao.getPartitionDate(request));
			//
			r.status = 200;
			r.path = request.getRequestURI();
			r.error = "SUCCESS";
			r.message = "RETRIEVE ONE DONE";
			//
			WebUtils.setResponse4IframeAndRest(response);
			log.info("[JSON:" + request.getRequestURI() + "][200][SUCC]");
			return r;
		} catch (NullPointerException e) {
			RestResult r = new RestResult();
			r.status = 500;
			r.path = request.getRequestURI();
			r.error = e.getClass().getName();
			r.message = Exceptions.getStackTrace(e);
			WebUtils.setResponse4IframeAndRest(response);
			log.warn("[JSON:" + request.getRequestURI() + "][500][" + e.getClass().getName() + "]"
					+ Exceptions.getStackTrace(e));
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

	@ResponseBody
	@RequestMapping(path = "crud/{domain}", method = RequestMethod.GET)
	public RestResult findAll(@PathVariable String domain, @RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "size", defaultValue = "10") int size,
			@RequestParam(value = "filters", defaultValue = "") String filters,
			@RequestParam(value = "order", defaultValue = "") String order, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			DaoWhere[] wheres = DaoWhere.fromJoinStrings(filters);
			WebDao<?> dao = getDao(domain, request);
			RestPage p = new RestPage();
			p.total = dao.countAll(wheres);
			p.rows = dao.findAll(page, size, DaoOrder.fromJoinStrings(order), wheres);
			//
			p.size = size;
			p.page = page;
			//
			RestResult r = new RestResult();
			r.content = p;
			//
			r.status = 200;
			r.path = request.getRequestURI();
			r.error = "SUCCESS";
			r.message = "RETRIEVE ALL DONE";
			//
			WebUtils.setResponse4IframeAndRest(response);
			log.info("[JSON:" + request.getRequestURI() + "][200][SUCC]");
			return r;
		} catch (NullPointerException e) {
			RestResult r = new RestResult();
			r.status = 500;
			r.path = request.getRequestURI();
			r.error = e.getClass().getName();
			r.message = Exceptions.getStackTrace(e);
			WebUtils.setResponse4IframeAndRest(response);
			log.warn("[JSON:" + request.getRequestURI() + "][500][" + e.getClass().getName() + "]"
					+ Exceptions.getStackTrace(e));
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

	///////////////////
	/// CRUDS
	///////////////////
	@ResponseBody
	@RequestMapping(path = "cruds/batch/{domain}", method = RequestMethod.DELETE)
	public RestResult deleteAll(@PathVariable String domain, @RequestParam String filters, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			getDao(domain, request).deleteAll(DaoWhere.fromJoinStrings(filters));
			//
			RestResult r = new RestResult();
			r.status = 200;
			r.path = request.getRequestURI();
			r.error = "SUCCESS";
			r.message = "BATCH DELETE DONE";
			//
			WebUtils.setResponse4IframeAndRest(response);
			log.info("[JSON:" + request.getRequestURI() + "][200][SUCC]");
			return r;
		} catch (NullPointerException e) {
			RestResult r = new RestResult();
			r.status = 500;
			r.path = request.getRequestURI();
			r.error = e.getClass().getName();
			r.message = Exceptions.getStackTrace(e);
			WebUtils.setResponse4IframeAndRest(response);
			log.warn("[JSON:" + request.getRequestURI() + "][500][" + e.getClass().getName() + "]"
					+ Exceptions.getStackTrace(e));
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

	@ResponseBody
	@RequestMapping(path = "cruds/single/{domain}", method = RequestMethod.GET)
	public RestResult findOne(@PathVariable String domain,
			@RequestParam(value = "filters", defaultValue = "") String filters,
			@RequestParam(value = "order", defaultValue = "") String order, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			RestResult r = new RestResult();
			r.content = getDao(domain, request).findOne(DaoOrder.fromJoinStrings(order),
					DaoWhere.fromJoinStrings(filters));
			//
			r.status = 200;
			r.path = request.getRequestURI();
			r.error = "SUCCESS";
			r.message = "RETRIEVE ONE DONE";
			//
			WebUtils.setResponse4IframeAndRest(response);
			log.info("[JSON:" + request.getRequestURI() + "][200][SUCC]");
			return r;
		} catch (NullPointerException e) {
			RestResult r = new RestResult();
			r.status = 500;
			r.path = request.getRequestURI();
			r.error = e.getClass().getName();
			r.message = Exceptions.getStackTrace(e);
			WebUtils.setResponse4IframeAndRest(response);
			log.warn("[JSON:" + request.getRequestURI() + "][500][" + e.getClass().getName() + "]"
					+ Exceptions.getStackTrace(e));
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

	@ResponseBody
	@RequestMapping(path = "cruds/check-null/{domain}", method = RequestMethod.PUT)
	public RestResult createCheckNull(@PathVariable String domain, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			int c = getDao(domain, request).insertCheckNull(request);
			if (c <= 0) {
				throw new RuntimeException("Rows affected 0");
			}
			//
			RestResult r = new RestResult();
			r.status = 200;
			r.path = request.getRequestURI();
			r.error = "SUCCESS";
			r.message = "CREATE DONE";
			//
			WebUtils.setResponse4IframeAndRest(response);
			log.info("[JSON:" + request.getRequestURI() + "][200][SUCC]");
			return r;
		} catch (NullPointerException e) {
			RestResult r = new RestResult();
			r.status = 500;
			r.path = request.getRequestURI();
			r.error = e.getClass().getName();
			r.message = Exceptions.getStackTrace(e);
			WebUtils.setResponse4IframeAndRest(response);
			log.warn("[JSON:" + request.getRequestURI() + "][500][" + e.getClass().getName() + "]"
					+ Exceptions.getStackTrace(e));
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

	@ResponseBody
	@RequestMapping(path = "cruds/check-null/{domain}/{id}", method = RequestMethod.POST)
	public RestResult updateCheckNull(@PathVariable String domain, @PathVariable String id, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			if (id.indexOf('%') >= 0) {
				id = URLDecoder.decode(id, Encodings.UTF8.value);
			}
			int c = getDao(domain, request).updateCheckNull(request, id);
			if (c <= 0) {
				throw new RuntimeException("Rows affected 0");
			}
			//
			RestResult r = new RestResult();
			r.status = 200;
			r.path = request.getRequestURI();
			r.error = "SUCCESS";
			r.message = "UPDATE DONE";
			//
			WebUtils.setResponse4IframeAndRest(response);
			log.info("[JSON:" + request.getRequestURI() + "][200][SUCC]");
			return r;
		} catch (NullPointerException e) {
			RestResult r = new RestResult();
			r.status = 500;
			r.path = request.getRequestURI();
			r.error = e.getClass().getName();
			r.message = Exceptions.getStackTrace(e);
			WebUtils.setResponse4IframeAndRest(response);
			log.warn("[JSON:" + request.getRequestURI() + "][500][" + e.getClass().getName() + "]"
					+ Exceptions.getStackTrace(e));
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

	@RequestMapping(path = "cruds/export/{domain}", method = RequestMethod.GET)
	public void export(@PathVariable String domain, @RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "size", defaultValue = "10") int size,
			@RequestParam(value = "filters", defaultValue = "") String filters,
			@RequestParam(value = "order", defaultValue = "") String order, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		try {
			DaoWhere[] wheres = DaoWhere.fromJoinStrings(filters);
			WebDao<?> dao = getDao(domain, request);
			List<?> rows = dao.findAll(page, size, DaoOrder.fromJoinStrings(order), wheres);
			WebUtils.setResponse4Csv(response,
					dao.getCrudView().title + "_" + (Strings.isNullOrEmpty(filters) ? "全部" : filters));
			PrintWriter writer = response.getWriter();
			boolean isFirst = true;
			for (CrudColumnInfo c : dao.getCrudView().columns) {
				if (isFirst) {
					isFirst = false;
				} else {
					writer.write(',');
				}
				writer.write('"');
				writer.write(c.title);
				writer.write('"');
			}
			writer.write("\r\n");
			for (Object obj : rows) {
				isFirst = true;
				for (Field f : obj.getClass().getFields()) {
					if (isFirst) {
						isFirst = false;
					} else {
						writer.write(',');
					}
					writer.write('"');
					Object object = f.get(obj);
					if (null != object) {
						if (f.getType().getSimpleName().equals("Date")) {
							writer.write(DateFormats.dateTime.format(object));
						} else {
							writer.write(object.toString());
						}
					}
					writer.write('"');
				}
				writer.write("\r\n");
			}
			writer.flush();
			log.info("[EXPORT:" + request.getRequestURI() + "][200][SUCC]");
		} catch (NullPointerException e) {
			response.getWriter().write(Exceptions.getStackTrace(e));
			log.warn("[EXPORT:" + request.getRequestURI() + "][500][" + e.getClass().getName() + "]"
					+ Exceptions.getStackTrace(e));
		} catch (Exception e) {
			response.getWriter().write(e.getClass().getName() + "\r\n" + e.getMessage());
			log.warn("[EXPORT:" + request.getRequestURI() + "][500][" + e.getClass().getName() + "]" + e.getMessage());
		}
	}

	@ResponseBody
	@RequestMapping(path = "cruds/schema/{domain}", method = RequestMethod.GET)
	public RestResult schema(@PathVariable String domain, HttpServletRequest request, HttpServletResponse response) {
		try {
			RestResult r = new RestResult();
			r.content = getDao(domain, request).getCrudView();
			//
			r.status = 200;
			r.path = request.getRequestURI();
			r.error = "SUCCESS";
			r.message = "RETRIEVE ONE DONE";
			//
			WebUtils.setResponse4IframeAndRest(response);
			log.info("[JSON:" + request.getRequestURI() + "][200][SUCC]");
			return r;
		} catch (NullPointerException e) {
			RestResult r = new RestResult();
			r.status = 500;
			r.path = request.getRequestURI();
			r.error = e.getClass().getName();
			r.message = Exceptions.getStackTrace(e);
			WebUtils.setResponse4IframeAndRest(response);
			log.warn("[JSON:" + request.getRequestURI() + "][500][" + e.getClass().getName() + "]"
					+ Exceptions.getStackTrace(e));
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
