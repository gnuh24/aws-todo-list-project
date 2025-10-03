package com.ec.user.integration.redis;


import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public interface RedisService {
	void set(String key, Object value);
	
	void setObjectWithTTL(String key, Object value, long timeout, TimeUnit unit);
	
	void set(String key, String value);
	
	void set(String key, String value, long timeout, TimeUnit unit);
	
	void setTimeToLive(String key, long timeoutInDays);
	
	void hashSet(String key, String field, Object value);
	
	boolean exists(String key);
	
	<T> List<T> hashGetAll(String key, Class<T> type);
	
	boolean hashExists(String key, String field);
	
	Object get(String key);
	
	public Map<String, Object> getField(String key);
	
	Object hashGet(String key, String field);
	
	void delete(String key);
	
	void delete(String key, String field);
	
	void delete(String key, List<String> fields);
	
}

