package com.jspring.cache;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.reflect.Method;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

@Configuration
@EnableCaching
public class CacheConfig extends CachingConfigurerSupport {

	private void appendArray(StringBuilder sb, Object[] array) {
		if (array.length == 0) {
			return;
		}
		boolean isAppand = false;
		for (Object obj : array) {
			if (isAppand) {
				sb.append(',');
			} else {
				isAppand = true;
			}
			if (null == obj) {
				sb.append("null");
				continue;
			}
			if (obj.getClass().isArray()) {
				sb.append('[');
				appendArray(sb, (Object[]) obj);
				sb.append(']');
				continue;
			}
			sb.append(obj.toString());
		}
	}

	@Bean
	public KeyGenerator keyGenerator() {
		return new KeyGenerator() {
			@Override
			public Object generate(Object target, Method method, Object... params) {
				StringBuilder sb = new StringBuilder();
				sb.append(target.getClass().getName());
				sb.append(':');
				sb.append(method.getName());
				sb.append('(');
				appendArray(sb, params);
				sb.append(')');
				// log.debug("[CACHE]" + sb.toString());
				return sb.toString();
			}
		};
	}

	@Bean
	public CacheManager cacheManager(@SuppressWarnings("rawtypes") RedisTemplate redisTemplate) {
		RedisCacheManager rcm = new RedisCacheManager(redisTemplate);
		// 设置缓存过期时间
		rcm.setDefaultExpiration(60);// 8 * 60 * 60);// 1分钟
		// // 设置value的过期时间
		// Map<String, Long> map = new HashMap();
		// map.put("test", 60L);
		// rcm.setExpires(map);
		return rcm;
	}

	@Bean
	public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory) {
		StringRedisTemplate template = new StringRedisTemplate(factory);
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Jackson2JsonRedisSerializer<?> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
		ObjectMapper om = new ObjectMapper();
		// om.setVisibility(PropertyAccessor.ALL,
		// JsonAutoDetect.Visibility.ANY);
		om.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.PUBLIC_ONLY);
		om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
		jackson2JsonRedisSerializer.setObjectMapper(om);
		template.setValueSerializer(jackson2JsonRedisSerializer);
		template.afterPropertiesSet();
		return template;
	}

}