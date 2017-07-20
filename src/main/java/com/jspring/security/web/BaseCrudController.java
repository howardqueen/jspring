package com.jspring.security.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jspring.Exceptions;
import com.jspring.collections.KeyValue;
import com.jspring.data.SqlExecutor;
import com.jspring.persistence.MapCrudService;
import com.jspring.text.ISerializer;
import com.jspring.text.JsonSerializer;
import com.jspring.web.RestResult;
import com.jspring.web.WebConfig;

public abstract class BaseCrudController extends BaseReadController {
	///////////////////
	/// ABSTRACT
	///////////////////
	protected abstract boolean validWriteDomain(Class<?> domain);

	protected abstract Object decorateWriteEntity(Object entity);

	private Object writeEntity(Object entity) {
		Object r = decorateWriteEntity(entity);
		if (null == r) {
			throw Exceptions.newInstance("Write denied for entity");
		}
		return r;
	}

	private Class<?> getWriteDomain(String domainSimpleClassName) {
		Class<?> r = getDomain(domainSimpleClassName);
		if (validWriteDomain(r)) {
			return r;
		}
		throw Exceptions.newInstance("Write denied for [Domain]" + r.getName());
	}

	protected abstract UnaryOperator<String> decorateWriteMap(Class<?> domain, UnaryOperator<String> map);

	private UnaryOperator<String> writeMap(Class<?> domain, UnaryOperator<String> map) {
		UnaryOperator<String> r = decorateWriteMap(domain, map);
		if (null == r) {
			throw Exceptions.newInstance("Write denied for entity-map");
		}
		return r;
	}

	///////////////////
	/// FIELDS
	///////////////////
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected MapCrudService<?, ?> createRepository(SqlExecutor sqlExecutor, Class<?> domain) {
		return new MapCrudService(sqlExecutor, domain);
	}

	private static List<KeyValue<Class<?>, ISerializer<?>>> serializers = new ArrayList<>();

	protected ISerializer<?> getSerializer(Class<?> entityClass) {
		Optional<?> r = serializers.stream()//
				.filter(a -> a.key.equals(entityClass))//
				.map(a -> a.value)//
				.findFirst();
		if (r.isPresent()) {
			return (ISerializer<?>) r.get();
		}
		synchronized (serializers) {
			r = serializers.stream()//
					.filter(a -> a.key.equals(entityClass))//
					.map(a -> a.value)//
					.findFirst();
			if (r.isPresent()) {
				return (ISerializer<?>) r.get();
			}
			ISerializer<?> t = JsonSerializer.newNoneGenericSerializer(entityClass);
			serializers.add(new KeyValue<>(entityClass, t));
			return t;
		}
	}

	///////////////////
	/// REST
	///////////////////
	@Override
	@ResponseBody
	@RequestMapping(path = "rest", method = RequestMethod.GET)
	public void getHelp(HttpServletRequest request, HttpServletResponse response) {
		// TODO
		super.getHelp(request, response);
	}

	///////////////////
	/// CRUD/REST/*
	///////////////////
	@ResponseBody
	@RequestMapping(path = "rest/crud/{domain}", method = RequestMethod.POST)
	public RestResult postAll(@PathVariable String domain, //
			HttpServletRequest request, HttpServletResponse response) {
		return WebConfig.responseRestObject(() -> {
			MapCrudService<?, ?> service = getRepository(domain);
			List<Object> objs = new ArrayList<>();
			try (BufferedReader br = request.getReader()) {
				String line;
				while (null != (line = br.readLine())) {
					objs.add(writeEntity(
							getSerializer(getWriteDomain((char) (domain.charAt(0) - 32) + domain.substring(1)))
									.deserialize(line)));
				}
			} catch (IOException e) {
				throw Exceptions.newInstance(e);
			}
			return service.insertAllNoneGeneric(objs.toArray(new Object[0]));
		}, request, response);
	}

	@ResponseBody
	@RequestMapping(path = "rest/crud/{domain}", method = RequestMethod.PUT)
	public RestResult replaceAll(@PathVariable String domain, //
			HttpServletRequest request, HttpServletResponse response) {
		return WebConfig.responseRestObject(() -> {
			MapCrudService<?, ?> service = getRepository(domain);
			List<Object> objs = new ArrayList<>();
			try (BufferedReader br = request.getReader()) {
				String line;
				while (null != (line = br.readLine())) {
					objs.add(writeEntity(
							getSerializer(getWriteDomain((char) (domain.charAt(0) - 32) + domain.substring(1)))
									.deserialize(line)));
				}
			} catch (IOException e) {
				throw Exceptions.newInstance(e);
			}
			return service.replaceAllNoneGeneric(objs.toArray(new Object[0]));
		}, request, response);
	}

	@ResponseBody
	@RequestMapping(path = "rest/crud/{domain}", method = RequestMethod.DELETE)
	public RestResult deleteAll(@PathVariable String domain, //
			HttpServletRequest request, HttpServletResponse response) {
		return WebConfig.responseRestObject(() -> {
			MapCrudService<?, ?> service = getRepository(domain);
			List<String> ids = new ArrayList<>();
			try (BufferedReader br = request.getReader()) {
				String id;
				while (null != (id = br.readLine())) {
					writeEntity(service.findOneNoneGeneric(id));
					ids.add(id);
				}
			} catch (IOException e) {
				throw Exceptions.newInstance(e);
			}
			return service.deleteAllNoneGeneric(ids.toArray(new String[0]));
		}, request, response);
	}

	///////////////////
	/// CRUD/REST/*/*
	///////////////////
	@ResponseBody
	@RequestMapping(path = "rest/crud/{domain}/insert", method = RequestMethod.POST)
	public RestResult postOneInsert(@PathVariable String domain, HttpServletRequest request,
			HttpServletResponse response) {
		return WebConfig.responseRestObject(() -> {
			MapCrudService<?, ?> service = getRepository(domain);
			return service.insertOneSkipNull(//
					writeMap(getWriteDomain((char) (domain.charAt(0) - 32) + domain.substring(1)),
							a -> request.getParameter(a)));
		}, request, response);
	}

	@ResponseBody
	@RequestMapping(path = "rest/crud/{domain}/ignore", method = RequestMethod.POST)
	public RestResult postOneInsertIgnore(@PathVariable String domain, HttpServletRequest request,
			HttpServletResponse response) {
		return WebConfig.responseRestObject(() -> {
			return WebConfig.responseRestObject(() -> {
				MapCrudService<?, ?> service = getRepository(domain);
				return service.insertIgnoreOneSkipNull(//
						writeMap(getWriteDomain((char) (domain.charAt(0) - 32) + domain.substring(1)),
								a -> request.getParameter(a)));
			}, request, response);
		}, request, response);
	}

	@ResponseBody
	@RequestMapping(path = "rest/crud/{domain}/replace", method = RequestMethod.POST)
	public RestResult postOneReplace(@PathVariable String domain, HttpServletRequest request,
			HttpServletResponse response) {
		return WebConfig.responseRestObject(() -> {
			return WebConfig.responseRestObject(() -> {
				MapCrudService<?, ?> service = getRepository(domain);
				return service.replaceOneSkipNull(//
						writeMap(getWriteDomain((char) (domain.charAt(0) - 32) + domain.substring(1)),
								a -> request.getParameter(a)));
			}, request, response);
		}, request, response);
	}

	@ResponseBody
	@RequestMapping(path = "rest/crud/{domain}/{id}", method = RequestMethod.PUT)
	public RestResult updateOne(@PathVariable String domain, @PathVariable String id, HttpServletRequest request,
			HttpServletResponse response) {
		return WebConfig.responseRestObject(() -> {
			return WebConfig.responseRestObject(() -> {
				MapCrudService<?, ?> service = getRepository(domain);
				return service.updateOneSkipNullByMapId(//
						writeMap(getWriteDomain((char) (domain.charAt(0) - 32) + domain.substring(1)),
								a -> request.getParameter(a)),
						id);
			}, request, response);
		}, request, response);
	}

	@ResponseBody
	@RequestMapping(path = "rest/crud/{domain}/{id}", method = RequestMethod.DELETE)
	public RestResult deleteOne(@PathVariable String domain, @PathVariable String id, HttpServletRequest request,
			HttpServletResponse response) {
		return WebConfig.responseRestObject(() -> {
			MapCrudService<?, ?> service = getRepository(domain);
			writeEntity(service.findOneNoneGeneric(id));
			return service.deleteOneNoneGeneric(id);
		}, request, response);
	}

}
