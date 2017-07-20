package com.jspring.security.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jspring.Exceptions;
import com.jspring.collections.KeyValue;
import com.jspring.data.OrderBy;
import com.jspring.data.Where;
import com.jspring.persistence.JColumnValue;
import com.jspring.persistence.MapService;
import com.jspring.persistence.RestPage;
import com.jspring.web.RestResult;
import com.jspring.web.WebConfig;
import com.jspring.data.SqlExecutor;

public abstract class BaseReadController implements ApplicationContextAware {

	///////////////////
	/// ABSTRACT
	///////////////////
	protected abstract boolean validReadDomain(Class<?> domain);

	private Class<?> getReadDomain(String domainSimpleClassName) {
		Class<?> r = getDomain(domainSimpleClassName);
		if (validReadDomain(r)) {
			return r;
		}
		throw Exceptions.newInstance("Read denied for [Domain]" + r.getName());
	}

	protected abstract Object decorateReadEntity(Object entity);

	private Object readEntity(Object entity) {
		Object r = decorateReadEntity(entity);
		if (null == r) {
			throw Exceptions.newInstance("Read denied for entity");
		}
		return r;
	}

	protected abstract boolean validReadFilters(Class<?> domain, List<Where> wheres);

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

	@Value("${security.domain.packages}")
	private String[] domainPackages;

	private final List<Class<?>> domains = new ArrayList<>();

	protected Class<?> getDomain(String domainSimpleClassName) {
		return domains.stream()//
				.filter(a -> a.getSimpleName().equals(domainSimpleClassName))//
				.findFirst()//
				.orElseGet(() -> {
					Class<?> t = null;
					for (String p : domainPackages) {
						try {
							t = Class.forName(p + '.' + domainSimpleClassName);
							break;
						} catch (Exception e) {
						}
					}
					if (null == t) {
						throw Exceptions.newInstance("Domain not found for [SimpleClassName]" + domainSimpleClassName);
					}
					domains.add(t);
					return t;
				});
	}

	@Autowired
	private SqlExecutor sqlExecutor;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected MapService<?, ?, ?> createRepository(SqlExecutor sqlExecutor, Class<?> domain) {
		return new MapService(sqlExecutor, domain);
	}

	private static List<KeyValue<String, MapService<?, ?, ?>>> repositories = new ArrayList<>();

	@SuppressWarnings("unchecked")
	protected <S extends MapService<?, ?, ?>> S getRepository(String domainName) {
		String simpleClassName = (char) (domainName.charAt(0) - 32) + domainName.substring(1);
		Class<?> domain = getReadDomain(simpleClassName);
		Optional<KeyValue<String, MapService<?, ?, ?>>> kv = repositories.stream()//
				.filter(a -> a.key.equals(domainName))//
				.findFirst();
		if (kv.isPresent()) {
			return (S) kv.get().value;
		}
		synchronized (repositories) {
			kv = repositories.stream()//
					.filter(a -> a.key.equals(domainName))//
					.findFirst();
			if (kv.isPresent()) {
				return (S) kv.get().value;
			}
			MapService<?, ?, ?> repository = createRepository(sqlExecutor, domain);
			repositories.add(new KeyValue<>(domainName, repository));
			return (S) repository;
		}
	}

	///////////////////
	/// CRUD
	///////////////////
	@ResponseBody
	@RequestMapping(path = "rest", method = RequestMethod.GET)
	public void getHelp(HttpServletRequest request, HttpServletResponse response) {
		// TODO
	}

	///////////////////
	/// CRUD/CONF/*
	///////////////////
	@ResponseBody
	@RequestMapping(path = "rest/conf/{domain}", method = RequestMethod.GET)
	public RestResult getConf(@PathVariable String domain, HttpServletRequest request, HttpServletResponse response) {
		return WebConfig.responseRestObject(() -> getRepository(domain).repository.getTable().getView(), request,
				response);
	}

	///////////////////
	/// CRUD/CONF/*/*
	///////////////////
	@ResponseBody
	@RequestMapping(path = "rest/conf/{domain}/{option}", method = RequestMethod.GET)
	public RestResult getConfOptions(@PathVariable String domain, //
			@PathVariable String option, //
			@RequestParam(defaultValue = "100") int size, //
			HttpServletRequest request, HttpServletResponse response) {
		return WebConfig.responseRestObject(() -> getRepository(domain).findOptions(option, size), request, response);
	}

	///////////////////
	/// CRUD/REST/*
	///////////////////
	@ResponseBody
	@RequestMapping(path = "rest/crud/{domain}", method = RequestMethod.GET)
	public RestResult getAll(@PathVariable String domain, //
			@RequestParam(defaultValue = "1") int page, //
			@RequestParam(defaultValue = "10") int size, //
			@RequestParam(defaultValue = "") String filters, //
			@RequestParam(defaultValue = "") String sorts, //
			HttpServletRequest request, HttpServletResponse response) {
		return WebConfig.responseRestObject(() -> {
			MapService<?, ?, ?> service = getRepository(domain);
			List<Where> wheres = Where.deserializeList(filters);
			if (validReadFilters(getReadDomain((char) (domain.charAt(0) - 32) + domain.substring(1)), wheres)) {
				RestPage<?> p = service.repository.findAll(page, size, OrderBy.deserialize(sorts),
						wheres.toArray(new Where[0]));
				return p;
			}
			throw Exceptions.newInstance("Read denied for filters");
		}, request, response);
	}

	///////////////////
	/// CRUD/REST/*/*
	///////////////////
	@ResponseBody
	@RequestMapping(path = "rest/crud/{domain}/{id}", method = RequestMethod.GET)
	public RestResult getOne(@PathVariable String domain, @PathVariable String id, HttpServletRequest request,
			HttpServletResponse response) {
		return WebConfig.responseRestObject(() -> readEntity(getRepository(domain).findOneNoneGeneric(id)), //
				request, response);
	}

	///////////////////
	/// CRUD/EXPORT/*
	///////////////////
	@RequestMapping(path = "rest/export/{domain}", method = RequestMethod.GET)
	public void getExport(@PathVariable String domain, //
			@RequestParam(defaultValue = "1") int page, //
			@RequestParam(defaultValue = "10") int size, //
			@RequestParam(defaultValue = "") String filters, //
			@RequestParam(defaultValue = "") String sorts, //
			HttpServletRequest request, HttpServletResponse response) {
		WebConfig.responseCsv((writer) -> {
			MapService<?, ?, ?> service = getRepository(domain);
			boolean isAppend = false;
			for (JColumnValue c : service.repository.getTable().getFindAllableColumns()) {
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
			for (Object obj : service.repository.findAll(page, size, OrderBy.deserialize(sorts),
					Where.deserialize(filters)).rows) {
				isAppend = false;
				for (JColumnValue c : service.repository.getTable().getFindAllableColumns()) {
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
			return service.repository.getTable().title;
		}, request, response);
	}

}
