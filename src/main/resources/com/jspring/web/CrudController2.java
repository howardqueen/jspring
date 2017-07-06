//package com.jspring.web;
//
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.io.UnsupportedEncodingException;
//import java.lang.reflect.Field;
//import java.net.URLDecoder;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Stream;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.xml.ws.Holder;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.BeansException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.ApplicationContextAware;
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
//import com.jspring.collections.KeyValue;
//import com.jspring.data.JColumnValue;
//import com.jspring.data.CrudIntegerRepository;
//import com.jspring.data.JTableValue;
//import com.jspring.data.DaoOrder;
//import com.jspring.data.DaoWhere;
//import com.jspring.data.SqlExecutor;
//import com.jspring.date.DateFormats;
//
//public abstract class CrudController2 implements ApplicationContextAware {
//
//	protected static final Logger log = LoggerFactory.getLogger(CrudController2.class.getSimpleName());
//
//	@Override
//	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
//		context = applicationContext;
//	}
//
//	private ApplicationContext context;
//
//	protected ApplicationContext getContext() {
//		return context;
//	}
//
//	@Autowired
//	private SqlExecutor sqlExecutor;
//
//	protected SqlExecutor getSqlExecutor() {
//		return sqlExecutor;
//	}
//
//	public static abstract class AuthValidator {
//
//		private static final Logger log = LoggerFactory.getLogger(AuthValidator.class.getSimpleName());
//
//		private final CrudController2 controller;
//		private final Class<?>[] authedDomains;
//
//		public AuthValidator(CrudController2 controller) {
//			this.controller = controller;
//			this.authedDomains = getAuthedDomains();
//		}
//
//		private List<KeyValue<String, CrudIntegerRepository<?>>> repositories = new ArrayList<>();
//
//		public CrudIntegerRepository<?> getAuthedRepository(String domain) {
//			Optional<KeyValue<String, CrudIntegerRepository<?>>> kv = repositories.stream()//
//					.filter(a -> a.key.equals(domain)).findFirst();
//			if (kv.isPresent()) {
//				return kv.get().value;
//			}
//			synchronized (this) {
//				kv = repositories.stream()//
//						.filter(a -> a.key.equals(domain)).findFirst();
//				if (kv.isPresent()) {
//					return kv.get().value;
//				}
//				String bean = domain + "Repository";
//				if (controller.getContext().containsBean(bean)) {
//					log.info("LOAD REPOSITORY: " + bean);
//					CrudIntegerRepository<?> dao = (CrudIntegerRepository<?>) controller.getContext().getBean(bean);
//					repositories.add(new KeyValue<>(domain, dao));
//					return dao;
//				}
//				String simpleClassName = (char) (domain.charAt(0) - 32) + domain.substring(1);
//				Class<?> t = Stream.of(authedDomains)//
//						.filter(a -> a.getSimpleName().equals(simpleClassName))//
//						.findFirst()//
//						.orElseThrow(() -> Exceptions.newInstance("Forbidden domain: " + domain));
//				@SuppressWarnings({ "unchecked", "rawtypes" })
//				CrudIntegerRepository<?> dao = new CrudIntegerRepository(controller.getSqlExecutor(), t);
//				log.info("CREATE REPOSITORY: " + bean);
//				repositories.add(new KeyValue<>(domain, dao));
//				return dao;
//			}
//		}
//
//		public abstract DaoWhere[] getAuthedFilters(String domain, String joinStrings);
//
//		protected abstract Class<?>[] getAuthedDomains();
//
//		public abstract void checkAuth(Object entity);
//
//		public abstract void checkAuth(String domain, String id);
//
//	}
//
//	protected AuthValidator BLOCK_ALL = new AuthValidator(this) {
//		@Override
//		public void checkAuth(String domain, String id) {
//			throw Exceptions.newInstance("Access denied for domain: " + domain);
//		}
//
//		@Override
//		public void checkAuth(Object entity) {
//			throw Exceptions.newInstance("Access denied for entity: " + entity.getClass().getSimpleName());
//		}
//
//		@Override
//		public DaoWhere[] getAuthedFilters(String domain, String joinStrings) {
//			throw Exceptions.newInstance("Access denied for domain: " + domain);
//		}
//
//		@Override
//		protected Class<?>[] getAuthedDomains() {
//			// TODO Auto-generated method stub
//			return null;
//		}
//	};
//	protected AuthValidator ALLOW_ALL = new AuthValidator(this) {
//		@Override
//		public void checkAuth(String domain, String id) {
//			throw Exceptions.newInstance("Access denied for domain: " + domain);
//		}
//
//		@Override
//		public void checkAuth(Object entity) {
//			throw Exceptions.newInstance("Access denied for entity: " + entity.getClass().getSimpleName());
//		}
//
//		@Override
//		public DaoWhere[] getAuthedFilters(String domain, String joinStrings) {
//			throw Exceptions.newInstance("Access denied for domain: " + domain);
//		}
//
//		@Override
//		protected Class<?>[] getAuthedDomains() {
//			// TODO Auto-generated method stub
//			return null;
//		}
//	};
//
//	///////////////////
//	/// VISITOR
//	///////////////////
//	protected abstract AuthValidator visitor();
//
//	private AuthValidator _visitor;
//
//	protected AuthValidator getVisitor() {
//		if (null == _visitor) {
//			_visitor = visitor();
//		}
//		return _visitor;
//	}
//
//	@ResponseBody
//	@RequestMapping(path = "crud/{domain}/definition", method = RequestMethod.POST)
//	public RestResult visitorPostDefinition(@PathVariable String domain, HttpServletRequest request,
//			HttpServletResponse response) {
//		return WebConfig.responseObject(//
//				() -> getVisitor().getAuthedRepository(domain).getTable().getView(), //
//				request, response);
//	}
//
//	@ResponseBody
//	@RequestMapping(path = "crud/{domain}", method = RequestMethod.GET)
//	public RestResult visitorGetFindAll(@PathVariable String domain,
//			@RequestParam(value = "page", defaultValue = "1") int page,
//			@RequestParam(value = "size", defaultValue = "10") int size,
//			@RequestParam(value = "filters", defaultValue = "") String filters,
//			@RequestParam(value = "order", defaultValue = "") String order, HttpServletRequest request,
//			HttpServletResponse response) {
//		return WebConfig.responseObject(() -> {
//			DaoWhere[] ws = getVisitor().getAuthedFilters(domain, filters);
//			CrudIntegerRepository<?> dao = getVisitor().getAuthedRepository(domain);
//			RestPage p = new RestPage();
//			p.total = dao.countAll(ws);
//			p.rows = dao.findAll(page, size, DaoOrder.fromJoinStrings(order), ws);
//			//
//			p.size = size;
//			p.page = page;
//			return p;
//		}, request, response);
//	}
//
//	@ResponseBody
//	@RequestMapping(path = "crud/{domain}/first", method = RequestMethod.POST)
//	public RestResult visitorPostFirst(@PathVariable String domain,
//			@RequestParam(value = "filters", defaultValue = "") String filters,
//			@RequestParam(value = "order", defaultValue = "") String order, HttpServletRequest request,
//			HttpServletResponse response) {
//		return WebConfig.responseObject(
//				() -> getVisitor().getAuthedRepository(domain).findOne(DaoOrder.fromJoinStrings(order),
//						getVisitor().getAuthedFilters(domain, filters)), //
//				request, response);
//	}
//
//	@ResponseBody
//	@RequestMapping(path = "crud/{domain}/{id}", method = RequestMethod.GET)
//	public RestResult visitorGetFindOne(@PathVariable String domain, @PathVariable String id,
//			HttpServletRequest request, HttpServletResponse response) {
//		Holder<String> hId = new Holder<>();
//		hId.value = id;
//		return WebConfig.responseObject(() -> {
//			if (hId.value.indexOf('%') >= 0) {
//				try {
//					hId.value = URLDecoder.decode(hId.value, Encodings.UTF8.value);
//				} catch (UnsupportedEncodingException e) {
//				}
//			}
//			CrudIntegerRepository<?> dao = getVisitor().getAuthedRepository(domain);
//			return dao.findOne(hId.value);
//		}, request, response);
//	}
//
//	///////////////////
//	/// REPORTOR
//	///////////////////
//	protected abstract AuthValidator reportor();
//
//	@RequestMapping(path = "crud/{domain}/export", method = RequestMethod.POST)
//	public void ReportorPostExport(@PathVariable String domain,
//			@RequestParam(value = "page", defaultValue = "1") int page,
//			@RequestParam(value = "size", defaultValue = "10") int size,
//			@RequestParam(value = "filters", defaultValue = "") String filters,
//			@RequestParam(value = "order", defaultValue = "") String order, HttpServletRequest request,
//			HttpServletResponse response) throws IOException {
//		try {
//			DaoWhere[] wheres = reportor().getAuthedFilters(domain, filters);
//			JTableValue cti = reportor().getAuthedRepository(domain).getTable();
//			List<?> rows = reportor().getAuthedRepository(domain).findAll(page, size, DaoOrder.fromJoinStrings(order),
//					wheres);
//			WebConfig.setResponse4Csv(response, cti.title + "_" + (Strings.isNullOrEmpty(filters) ? "全部" : filters));
//			PrintWriter writer = response.getWriter();
//			boolean isFirst = true;
//			for (JColumnValue c : cti.columns) {
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
//	///////////////////
//	/// EDITOR
//	///////////////////
//	protected abstract AuthValidator editor();
//
//	@ResponseBody
//	@RequestMapping(path = "crud/{domain}", method = RequestMethod.POST)
//	public RestResult editorPostInserOne(@PathVariable String domain, HttpServletRequest request,
//			HttpServletResponse response) {
//		return WebConfig.responseObject(() -> {
//			CrudIntegerRepository<?> dao = editor().getAuthedRepository(domain);
//			Object obj = getSqlExecutor().getMapper().parseEntity(dao.getTable().getInsertableColumns(),
//					(s) -> request.getParameter(s));
//			editor().checkAuth(obj);
//			return dao.insertAll(obj);
//		}, request, response);
//	}
//
//	@ResponseBody
//	@RequestMapping(path = "crud/{domain}/{id}", method = RequestMethod.PUT)
//	public RestResult editorPutUpdateOne(@PathVariable String domain, @PathVariable String id,
//			HttpServletRequest request, HttpServletResponse response) {
//		return WebConfig.responseObject(() -> {
//			editor().checkAuth(domain, id);
//			return editor().getAuthedRepository(domain).updateOne((s) -> request.getParameter(s), id);
//		}, request, response);
//	}
//
//	///////////////////
//	/// MANAGER
//	///////////////////
//	protected abstract AuthValidator manager();
//
//	@ResponseBody
//	@RequestMapping(path = "crud/{domain}/{id}", method = RequestMethod.DELETE)
//	public RestResult managerDeleteOne(@PathVariable String domain, @PathVariable String id, HttpServletRequest request,
//			HttpServletResponse response) {
//		return WebConfig.responseObject(() -> {
//			manager().checkAuth(domain, id);
//			return manager().getAuthedRepository(domain).deleteOne(id);
//		}, request, response);
//	}
//
//	@ResponseBody
//	@RequestMapping(path = "crud/{domain}", method = RequestMethod.PUT)
//	public RestResult managerPutUpdateAll(@PathVariable String domain, @RequestParam String wheres,
//			HttpServletRequest request, HttpServletResponse response) {
//		return WebConfig.responseObject(() -> {
//			return editor().getAuthedRepository(domain).updateAll((s) -> request.getParameter(s),
//					manager().getAuthedFilters(domain, wheres));
//		}, request, response);
//	}
//
//	// @ResponseBody
//	// @RequestMapping(path = "crud/{domain}/insert", method =
//	// RequestMethod.POST)
//	// public RestResult postInsertAll(@PathVariable String domain,
//	// HttpServletRequest request,
//	// HttpServletResponse response) {
//	// return WebConfig.responseObject(() -> {
//	// // CrudRepository<?> dao = getEditor().getCrudRepository(domain);
//	// // Object obj = dao.parseEntity((s) -> request.getParameter(s));
//	// // getEditor().valid(obj);
//	// // return dao.insert(obj);
//	// }, request, response);
//	// }
//	//
//	// @ResponseBody
//	// @RequestMapping(path = "crud/{domain}/replace", method =
//	// RequestMethod.POST)
//	// public RestResult postReplaceAll(@PathVariable String domain,
//	// HttpServletRequest request,
//	// HttpServletResponse response) {
//	// return WebConfig.responseObject(() -> {
//	// // CrudRepository<?> dao = getEditor().getCrudRepository(domain);
//	// // Object obj = dao.parseEntity((s) -> request.getParameter(s));
//	// // getEditor().valid(obj);
//	// // return dao.insert(obj);
//	// }, request, response);
//	// }
//	//
//	// @ResponseBody
//	// @RequestMapping(path = "crud/{domain}/update", method =
//	// RequestMethod.POST)
//	// public RestResult postUpdateAll(@PathVariable String domain,
//	// HttpServletRequest request,
//	// HttpServletResponse response) {
//	// return WebConfig.responseObject(() -> {
//	// // CrudRepository<?> dao = getEditor().getCrudRepository(domain);
//	// // Object obj = dao.parseEntity((s) -> request.getParameter(s));
//	// // getEditor().valid(obj);
//	// // return dao.insert(obj);
//	// }, request, response);
//	// }
//
//	///////////////////
//	/// ADMINISTRATOR
//	///////////////////
//	protected abstract AuthValidator admin();
//
//	@ResponseBody
//	@RequestMapping(path = "crud/{domain}", method = RequestMethod.DELETE)
//	public RestResult adminDeleteAll(@PathVariable String domain, @RequestParam String filters,
//			HttpServletRequest request, HttpServletResponse response) {
//		return WebConfig.responseObject(() -> {
//			return manager().getAuthedRepository(domain).deleteAll(admin().getAuthedFilters(domain, filters));
//		}, request, response);
//	}
//
//}
