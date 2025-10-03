package com.ec.user.integration.redis;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class RedisServiceImpl implements RedisService {
	
	private final RedisTemplate<String, Object> redisTemplate;
	private  final HashOperations<String, String, Object> hashOperations;
	
	
	public RedisServiceImpl(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
		this.hashOperations = redisTemplate.opsForHash();
	}
	
	
	@Override
	public void set(String key, Object value) {
		redisTemplate.opsForValue().set(key, value);
	}
	
	@Override
	public void setObjectWithTTL(String key, Object value, long timeout, TimeUnit unit) {
		redisTemplate.opsForValue().set(key, value, timeout, unit);
	}
	
	
	@Override
	public void set(String key, String value) {
		redisTemplate.opsForValue().set(key, value);
	}
	
	@Override
	public void set(String key, String value, long timeout, TimeUnit unit) {
		redisTemplate.opsForValue().set(key, value, timeout, unit);
	}
	
	
	@Override
	public void setTimeToLive(String key, long timeoutInDays) {
		redisTemplate.expire(key, timeoutInDays, TimeUnit.DAYS);
	}
	
	@Override
	public void hashSet(String key, String field, Object value) {
		hashOperations.put(key, field, value);
	}
	
	@Override
	public boolean hashExists(String key, String field) {
		return hashOperations.hasKey(key, field);
		
	}
	
	@Override
	public boolean exists(String key) {
		return redisTemplate.hasKey(key);
		
	}
	
	
	@Override
	public Object get(String key) {
		return redisTemplate.opsForValue().get(key);
	}
	
	@Override
	public Map<String, Object> getField(String key) {
		return hashOperations.entries(key);
	}
	
	@Override
	public Object hashGet(String key, String field) {
		return hashOperations.get(key, field);
	}
	
	@Override
	public <T> List<T> hashGetAll(String key, Class<T> type) {
		
		Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
		return entries.values().stream().map(object -> type.cast(object)).collect(Collectors.toList());
		
		
	}
	
	public <T> int count(String key, Class<T> type) {
		
		Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
		return entries.values().stream().map(object -> type.cast(object)).collect(Collectors.toList()).size();
		
		
	}
	
	@Override
	public void delete(String key) {
		redisTemplate.delete(key);
	}
	
	@Override
	public void delete(String key, String field) {
		hashOperations.delete(key, field);
	}
	
	@Override
	public void delete(String key, List<String> fields) {
		for (String field : fields) {
			hashOperations.delete(key, field);
		}
	}
}