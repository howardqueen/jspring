package com.jspring.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jspring.Exceptions;
import com.jspring.collections.KeyValue;
import com.jspring.data.CrudRepository;
import com.jspring.data.JColumnValue;
import com.jspring.data.JTableValue;
import com.jspring.data.OrderBy;
import com.jspring.data.Where;
import com.jspring.data.SqlExecutor;
import com.jspring.text.ISerializer;
import com.jspring.text.JsonSerializer;

public abstract class CrudController implements ApplicationContextAware {

	protected static final Logger log = LoggerFactory.getLogger(CrudController.class.getSimpleName());

	///////////////////
	/// FIELDS
	///////////////////
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		context = applicationContext;
	}

	private ApplicationContext context;

	protected ApplicationContext getContext() {
		return context;
	}

	@Autowired
	private SqlExecutor sqlExecutor;

	private List<KeyValue<String, CrudRepository<?, ?>>> repositories = new ArrayList<>();

	protected CrudRepository<?, ?> getRepository(String domainName) {
		Optional<KeyValue<String, CrudRepository<?, ?>>> kv = repositories.stream()//
				.filter(a -> a.key.equals(domainName)).findFirst();
		if (kv.isPresent()) {
			return kv.get().value;
		}
		synchronized (this) {
			kv = repositories.stream()//
					.filter(a -> a.key.equals(domainName)).findFirst();
			if (kv.isPresent()) {
				return kv.get().value;
			}
			String simpleClassName = (char) (domainName.charAt(0) - 32) + domainName.substring(1);
			Class<?> domain = visit(simpleClassName);
			String bean = domainName + "Repository";
			if (getContext().containsBean(bean)) {
				log.info("LOAD REPOSITORY: " + bean);
				CrudRepository<?, ?> dao = (CrudRepository<?, ?>) getContext().getBean(bean);
				repositories.add(new KeyValue<>(domainName, dao));
				return dao;
			}
			// Class<?> t = Stream.of(getAuthedDomains())//
			// .filter(a -> a.getSimpleName().equals(simpleClassName))//
			// .findFirst()//
			// .orElseThrow(() -> Exceptions.newInstance("Forbidden domain: " +
			// domain));
			JTableValue table = JTableValue.of(domain);
			@SuppressWarnings({ "unchecked", "rawtypes" })
			CrudRepository<?, ?> dao = new CrudRepository(sqlExecutor, domain, table.primaryKey.field.getType());
			log.info("CREATE REPOSITORY: " + bean);
			repositories.add(new KeyValue<>(domainName, dao));
			return dao;
		}
	}

	private List<KeyValue<Class<?>, ISerializer<?>>> serializers = new ArrayList<>();

	protected ISerializer<?> getSerializer(Class<?> entityClass) {
		ISerializer<?> r = serializers.stream()//
				.filter(a -> a.key.equals(entityClass))//
				.map(a -> a.value)//
				.findFirst()//
				.get();
		if (null != r) {
			return r;
		}
		synchronized (serializers) {
			r = serializers.stream()//
					.filter(a -> a.key.equals(entityClass))//
					.map(a -> a.value)//
					.findFirst()//
					.get();
			if (null != r) {
				return r;
			}
			r = JsonSerializer.newNonGenericSerializer(entityClass);
			serializers.add(new KeyValue<>(entityClass, r));
			return r;
		}
	}

	///////////////////
	/// ABSTRACT
	///////////////////
	protected abstract Class<?> visit(String domainSimpleClassName);

	protected abstract Where[] readFilters(Class<?> domain, String filters);

	protected abstract Object readEntity(Object entity);

	protected abstract Object writeEntity(Object entity);

	protected abstract UnaryOperator<String> writeMap(Class<?> domain, UnaryOperator<String> map);

	///////////////////
	/// CRUD
	///////////////////
	@ResponseBody
	@RequestMapping(path = "crud", method = RequestMethod.GET)
	public void getHelp(HttpServletRequest request, HttpServletResponse response) {
		// TODO
	}

	///////////////////
	/// CRUD/REST/*
	///////////////////
	@ResponseBody
	@RequestMapping(path = "crud/rest/{domain}", method = RequestMethod.POST)
	public RestResult postAll(@PathVariable String domain, //
			HttpServletRequest request, HttpServletResponse response) {
		return WebConfig.responseObject(() -> {
			CrudRepository<?, ?> dao = getRepository(domain);
			List<Object> objs = new ArrayList<>();
			try (BufferedReader br = request.getReader()) {
				String line;
				while (null != (line = br.readLine())) {
					objs.add(writeEntity(getSerializer(dao.getTable().domain).deserialize(line)));
				}
			} catch (IOException e) {
				throw Exceptions.newInstance(e);
			}
			return dao.insertAllEntities(objs.toArray(new Object[0]));
		}, request, response);
	}

	@ResponseBody
	@RequestMapping(path = "crud/rest/{domain}", method = RequestMethod.GET)
	public RestResult getAll(@PathVariable String domain, //
			@RequestParam(defaultValue = "1") int page, //
			@RequestParam(defaultValue = "10") int size, //
			@RequestParam(defaultValue = "") String filters, //
			@RequestParam(defaultValue = "") String sorts, //
			HttpServletRequest request, HttpServletResponse response) {
		return WebConfig.responseObject(() -> {
			CrudRepository<?, ?> dao = getRepository(domain);
			Where[] ws = readFilters(dao.getDomain(), filters);
			RestPage p = new RestPage();
			p.total = dao.countAll(ws);
			p.rows = dao.findAll(page, size, OrderBy.deserialize(sorts), ws);
			//
			p.size = size;
			p.page = page;
			return p;
		}, request, response);
	}

	@ResponseBody
	@RequestMapping(path = "crud/rest/{domain}", method = RequestMethod.PUT)
	public RestResult replaceAll(@PathVariable String domain, //
			HttpServletRequest request, HttpServletResponse response) {
		return WebConfig.responseObject(() -> {
			CrudRepository<?, ?> dao = getRepository(domain);
			List<Object> objs = new ArrayList<>();
			try (BufferedReader br = request.getReader()) {
				String line;
				while (null != (line = br.readLine())) {
					objs.add(writeEntity(getSerializer(dao.getTable().domain).deserialize(line)));
				}
			} catch (IOException e) {
				throw Exceptions.newInstance(e);
			}
			return dao.replaceAllEntities(objs.toArray(new Object[0]));
		}, request, response);
	}

	@ResponseBody
	@RequestMapping(path = "crud/rest/{domain}", method = RequestMethod.DELETE)
	public RestResult deleteAll(@PathVariable String domain, //
			HttpServletRequest request, HttpServletResponse response) {
		return WebConfig.responseObject(() -> {
			CrudRepository<?, ?> dao = getRepository(domain);
			List<Object> ids = new ArrayList<>();
			try (BufferedReader br = request.getReader()) {
				String id;
				while (null != (id = br.readLine())) {
					writeEntity(dao.findOneByKey(id));
					ids.add(id);
				}
			} catch (IOException e) {
				throw Exceptions.newInstance(e);
			}
			return dao.deleteAllByKeys(ids.toArray(new Object[0]));
		}, request, response);
	}

	///////////////////
	/// CRUD/REST/*/*
	///////////////////
	@ResponseBody
	@RequestMapping(path = "crud/rest/{domain}/insert", method = RequestMethod.POST)
	public RestResult postOneInsert(@PathVariable String domain, HttpServletRequest request,
			HttpServletResponse response) {
		return WebConfig.responseObject(() -> {
			CrudRepository<?, ?> dao = getRepository(domain);
			return dao.insertOneSkipNull(writeMap(dao.getDomain(), (a) -> request.getParameter(a)));
		}, request, response);
	}

	@ResponseBody
	@RequestMapping(path = "crud/rest/{domain}/ignore", method = RequestMethod.POST)
	public RestResult postOneInsertIgnore(@PathVariable String domain, HttpServletRequest request,
			HttpServletResponse response) {
		return WebConfig.responseObject(() -> {
			CrudRepository<?, ?> dao = getRepository(domain);
			return dao.insertIgnoreOneSkipNull(writeMap(dao.getDomain(), (a) -> request.getParameter(a)));
		}, request, response);
	}

	@ResponseBody
	@RequestMapping(path = "crud/rest/{domain}/replace", method = RequestMethod.POST)
	public RestResult postOneReplace(@PathVariable String domain, HttpServletRequest request,
			HttpServletResponse response) {
		return WebConfig.responseObject(() -> {
			CrudRepository<?, ?> dao = getRepository(domain);
			return dao.replaceOneSkipNull(writeMap(dao.getDomain(), (a) -> request.getParameter(a)));
		}, request, response);
	}

	@ResponseBody
	@RequestMapping(path = "crud/rest/{domain}/{id}", method = RequestMethod.GET)
	public RestResult getOne(@PathVariable String domain, @PathVariable String id, HttpServletRequest request,
			HttpServletResponse response) {
		return WebConfig.responseObject(() -> readEntity(getRepository(domain).findOneByKey(id)), //
				request, response);
	}

	@ResponseBody
	@RequestMapping(path = "crud/rest/{domain}/{id}", method = RequestMethod.PUT)
	public RestResult updateOne(@PathVariable String domain, @PathVariable String id, HttpServletRequest request,
			HttpServletResponse response) {
		return WebConfig.responseObject(() -> {
			CrudRepository<?, ?> dao = getRepository(domain);
			writeEntity(dao.findOneByKey(id));
			return dao.updateOneSkipNullByKey(writeMap(dao.getDomain(), (a) -> request.getParameter(a)), id);
		}, request, response);
	}

	@ResponseBody
	@RequestMapping(path = "crud/rest/{domain}/{id}", method = RequestMethod.DELETE)
	public RestResult deleteOne(@PathVariable String domain, @PathVariable String id, HttpServletRequest request,
			HttpServletResponse response) {
		return WebConfig.responseObject(() -> {
			CrudRepository<?, ?> dao = getRepository(domain);
			writeEntity(dao.findOneByKey(id));
			return dao.deleteOneByKey(id);
		}, request, response);
	}

	///////////////////
	/// CRUD/CONF/*
	///////////////////
	@ResponseBody
	@RequestMapping(path = "crud/conf/{domain}", method = RequestMethod.GET)
	public RestResult getConf(@PathVariable String domain, HttpServletRequest request, HttpServletResponse response) {
		return WebConfig.responseObject(() -> getRepository(domain).getTable().getView(), request, response);
	}

	///////////////////
	/// CRUD/EXPORT/*
	///////////////////
	@RequestMapping(path = "crud/export/{domain}", method = RequestMethod.GET)
	public void getExport(@PathVariable String domain, //
			@RequestParam(defaultValue = "1") int page, //
			@RequestParam(defaultValue = "10") int size, //
			@RequestParam(defaultValue = "") String filters, //
			@RequestParam(defaultValue = "") String sorts, //
			HttpServletRequest request, HttpServletResponse response) {
		WebConfig.responseCsv((writer) -> {
			CrudRepository<?, ?> dao = getRepository(domain);
			boolean isAppend = false;
			for (JColumnValue c : dao.getTable().getFindAllableColumns()) {
				if (isAppend) {
					writer.write(',');
				} else {
					isAppend = true;
				}
				writer.write('"');
				writer.write(c.title);
				writer.write('"');
			}
			writer.write("\r\n");
			for (Object obj : dao.findAll(page, size, OrderBy.deserialize(sorts), Where.deserialize(filters))) {
				isAppend = false;
				for (JColumnValue c : dao.getTable().getFindAllableColumns()) {
					if (isAppend) {
						writer.write(',');
					} else {
						isAppend = true;
					}
					writer.write('"');
					writer.write(c.getValueString(obj));
					writer.write('"');
				}
				writer.write("\r\n");
			}
			return dao.getTable().title;
		}, request, response);
	}

}
