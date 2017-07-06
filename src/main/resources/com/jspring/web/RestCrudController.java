//package com.jspring.web;
//
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.io.UnsupportedEncodingException;
//import java.lang.reflect.Field;
//import java.net.URLDecoder;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.xml.ws.Holder;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.BeansException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.ApplicationContextAware;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//import com.jspring.Encodings;
//import com.jspring.Environment;
//import com.jspring.Exceptions;
//import com.jspring.Strings;
//import com.jspring.data.JColumns;
//import com.jspring.data.CrudRepository;
//import com.jspring.data.JTables;
//import com.jspring.data.DaoOrder;
//import com.jspring.data.DaoWhere;
//import com.jspring.data.JEntity;
//import com.jspring.data.SqlExecutor;
//import com.jspring.date.DateFormats;
//
//@Controller
//public final class RestCrudController implements ApplicationContextAware {
//
//	private static final Logger log = LoggerFactory.getLogger(RestCrudController.class);
//
//	private ApplicationContext context;
//
//	protected ApplicationContext getContext() {
//		return context;
//	}
//
//	@Override
//	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
//		context = applicationContext;
//	}
//
//	protected String getUpperFirstLetter(String domain) {
//		return (char) (domain.charAt(0) - 32) + domain.substring(1);
//	}
//
//	@Autowired
//	private SqlExecutor sqlExecutor;
//
//	@Value("${jspring.web.crud-domain-packages}")
//	String[] domains;
//	private Map<String, CrudRepository<?>> repositories = new HashMap<>();
//
//	private CrudRepository<?> getRepository(String domain, HttpServletRequest request) {
//		if (repositories.containsKey(domain)) {
//			return (CrudRepository<?>) repositories.get(domain);
//		}
//		synchronized (this) {
//			if (repositories.containsKey(domain)) {
//				return repositories.get(domain);
//			}
//			String f = domain + "Repository";
//			if (getContext().containsBean(f)) {
//				log.info("LOAD REPOSITORY: " + f);
//				CrudRepository<?> dao = (CrudRepository<?>) getContext().getBean(f);
//				repositories.put(domain, dao);
//				return dao;
//			}
//			Class<?> t = null;
//			for (String d : domains) {
//				f = d + "." + getUpperFirstLetter(domain);
//				try {
//					t = Class.forName(f);
//					log.info("LOAD DOMAIN: " + f);
//					break;
//				} catch (Exception e) {
//				}
//			}
//			if (null == t) {
//				log.debug("DOMAIN NOT EXISTS: " + domain);
//				throw new RuntimeException("DOMAIN NOT EXISTS: " + domain);
//			}
//			@SuppressWarnings({ "rawtypes", "unchecked" })
//			CrudRepository<?> dao = new CrudRepository(sqlExecutor, t);
//			repositories.put(domain, dao);
//			return dao;
//		}
//	}
//
//	private Class<?> getEntityClass(String domain, HttpServletRequest request) {
//		try {
//			for (String d : domains) {
//				String f = d + "." + getUpperFirstLetter(domain);
//				Class<?> t = Class.forName(f);
//				return t;
//			}
//		} catch (Exception e) {
//			throw Exceptions.newInstance(e);
//		}
//		log.debug("DOMAIN NOT EXISTS: " + domain);
//		throw new RuntimeException("DOMAIN NOT EXISTS: " + domain);
//	}
//
//	///////////////////
//	/// CRUD
//	///////////////////
//	@ResponseBody
//	@RequestMapping(path = "crud/{domain}", method = RequestMethod.POST)
//	public RestResult create(@PathVariable String domain, HttpServletRequest request, HttpServletResponse response) {
//		return WebConfig.responseObject(() -> {
//			CrudRepository<?> dao = getRepository(domain, request);
//			return dao.insert(dao.parseEntity((s) -> request.getParameter(s)));
//		}, request, response);
//	}
//
//	@ResponseBody
//	@RequestMapping(path = "crud/{domain}/{id}", method = RequestMethod.PUT)
//	public RestResult update(@PathVariable String domain, @PathVariable String id, HttpServletRequest request,
//			HttpServletResponse response) {
//		Holder<String> hId = new Holder<>();
//		hId.value = id;
//		return WebConfig.responseObject(() -> {
//			if (hId.value.indexOf('%') >= 0) {
//				try {
//					hId.value = URLDecoder.decode(hId.value, Encodings.UTF8.value);
//				} catch (UnsupportedEncodingException e) {
//				}
//			}
//			return getRepository(domain, request).update((s) -> request.getParameter(s), id);
//		}, request, response);
//	}
//
//	@ResponseBody
//	@RequestMapping(path = "crud/{domain}/{id}", method = RequestMethod.DELETE)
//	public RestResult delete(@PathVariable String domain, @PathVariable String id, HttpServletRequest request,
//			HttpServletResponse response) {
//		Holder<String> hId = new Holder<>();
//		hId.value = id;
//		return WebConfig.responseObject(() -> {
//			if (hId.value.indexOf('%') >= 0) {
//				try {
//					hId.value = URLDecoder.decode(hId.value, Encodings.UTF8.value);
//				} catch (UnsupportedEncodingException e) {
//				}
//			}
//			CrudRepository<?> dao = getRepository(domain, request);
//			return dao.delete(hId.value);
//		}, request, response);
//	}
//
//	@ResponseBody
//	@RequestMapping(path = "crud/{domain}/{id}", method = RequestMethod.GET)
//	public RestResult findOne(@PathVariable String domain, @PathVariable String id, HttpServletRequest request,
//			HttpServletResponse response) {
//		Holder<String> hId = new Holder<>();
//		hId.value = id;
//		return WebConfig.responseObject(() -> {
//			if (hId.value.indexOf('%') >= 0) {
//				try {
//					hId.value = URLDecoder.decode(hId.value, Encodings.UTF8.value);
//				} catch (UnsupportedEncodingException e) {
//				}
//			}
//			CrudRepository<?> dao = getRepository(domain, request);
//			return dao.findOneById(hId.value);
//		}, request, response);
//	}
//
//	@ResponseBody
//	@RequestMapping(path = "crud/{domain}", method = RequestMethod.GET)
//	public RestResult findAll(@PathVariable String domain, @RequestParam(value = "page", defaultValue = "1") int page,
//			@RequestParam(value = "size", defaultValue = "10") int size,
//			@RequestParam(value = "filters", defaultValue = "") String filters,
//			@RequestParam(value = "order", defaultValue = "") String order, HttpServletRequest request,
//			HttpServletResponse response) {
//		return WebConfig.responseObject(() -> {
//			DaoWhere[] wheres = DaoWhere.deserialize(filters);
//			CrudRepository<?> dao = getRepository(domain, request);
//			RestPage p = new RestPage();
//			p.total = dao.countAll(wheres);
//			p.rows = dao.findAll(page, size, DaoOrder.fromJoinStrings(order), wheres);
//			//
//			p.size = size;
//			p.page = page;
//			return p;
//		}, request, response);
//	}
//
//	///////////////////
//	/// CRUDS
//	///////////////////
//	@ResponseBody
//	@RequestMapping(path = "cruds/schema/{domain}", method = RequestMethod.GET)
//	public RestResult schema(@PathVariable String domain, HttpServletRequest request, HttpServletResponse response) {
//		return WebConfig.responseObject(
//				() -> JEntity.of(getEntityClass(domain, request)).getJTablePojo(), //
//				request, response);
//	}
//
//	@RequestMapping(path = "cruds/export/{domain}", method = RequestMethod.GET)
//	public void export(@PathVariable String domain, @RequestParam(value = "page", defaultValue = "1") int page,
//			@RequestParam(value = "size", defaultValue = "10") int size,
//			@RequestParam(value = "filters", defaultValue = "") String filters,
//			@RequestParam(value = "order", defaultValue = "") String order, HttpServletRequest request,
//			HttpServletResponse response) throws IOException {
//		try {
//			DaoWhere[] wheres = DaoWhere.deserialize(filters);
//			JTables cti = JEntity.of(getEntityClass(domain, request)).getJTablePojo();
//			List<?> rows = getRepository(domain, request).findAll(page, size, DaoOrder.fromJoinStrings(order), wheres);
//			WebConfig.setResponse4Csv(response, cti.title + "_" + (Strings.isNullOrEmpty(filters) ? "全部" : filters));
//			PrintWriter writer = response.getWriter();
//			boolean isFirst = true;
//			for (JColumns c : cti.columns) {
//				if (isFirst) {
//					isFirst = false;
//				} else {
//					writer.write(',');
//				}
//				writer.write('"');
//				writer.write(c.title);
//				writer.write('"');
//			}
//			writer.write("\r\n");
//			for (Object obj : rows) {
//				isFirst = true;
//				for (Field f : obj.getClass().getFields()) {
//					if (isFirst) {
//						isFirst = false;
//					} else {
//						writer.write(',');
//					}
//					writer.write('"');
//					Object object = f.get(obj);
//					if (null != object) {
//						if (f.getType().getSimpleName().equals("Date")) {
//							writer.write(DateFormats.dateTime.format(object));
//						} else {
//							writer.write(object.toString());
//						}
//					}
//					writer.write('"');
//				}
//				writer.write("\r\n");
//			}
//			writer.flush();
//			log.info("[EXPORT:" + RequestMethod.GET + ":" + request.getRequestURI() + "][200][SUCC]");
//		} catch (RuntimeException e) {
//			response.getWriter().write(Exceptions.getStackTrace(e));
//			log.warn("[EXPORT:" + RequestMethod.GET + ":" + request.getRequestURI() + "][500]["
//					+ e.getClass().getSimpleName() + "]" + Environment.NewLine + Exceptions.getStackTrace(e));
//		} catch (Exception e) {
//			response.getWriter().write(e.getClass().getName() + "\r\n" + e.getMessage());
//			log.warn("[EXPORT:" + RequestMethod.GET + ":" + request.getRequestURI() + "][500][" + e.getClass().getName()
//					+ "]" + e.getMessage());
//		}
//	}
//
//	@ResponseBody
//	@RequestMapping(path = "cruds/one/{domain}", method = RequestMethod.GET)
//	public RestResult findOne(@PathVariable String domain,
//			@RequestParam(value = "filters", defaultValue = "") String filters,
//			@RequestParam(value = "order", defaultValue = "") String order, HttpServletRequest request,
//			HttpServletResponse response) {
//		return WebConfig.responseObject(
//				() -> getRepository(domain, request).findOne(DaoOrder.fromJoinStrings(order),
//						DaoWhere.deserialize(filters)), //
//				request, response);
//	}
//
//	@ResponseBody
//	@RequestMapping(path = "cruds/batch/{domain}", method = RequestMethod.DELETE)
//	public RestResult deleteAll(@PathVariable String domain, @RequestParam String filters, HttpServletRequest request,
//			HttpServletResponse response) {
//		return WebConfig.responseObject(
//				() -> getRepository(domain, request).deleteAll(DaoWhere.deserialize(filters)), //
//				request, response);
//	}
//
//}
