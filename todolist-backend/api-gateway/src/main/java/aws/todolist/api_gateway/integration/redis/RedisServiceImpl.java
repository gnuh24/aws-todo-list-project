package aws.todolist.api_gateway.integration.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Optional;

@Service
public class RedisServiceImpl implements RedisService {
	
	@Autowired
	private ReactiveRedisTemplate<String, String> redisTemplate;
	
	@Override
	public Mono<Boolean> set(String key, String value, Duration ttl) {
		return redisTemplate.opsForValue()
			.set(key, value, ttl)
			.onErrorReturn(false);
	}
	
	@Override
	public Mono<Optional<String>> get(String key) {
		return redisTemplate.opsForValue()
			.get(key)
			.map(Optional::ofNullable)
			.defaultIfEmpty(Optional.empty())
			.onErrorReturn(Optional.empty());
	}

	

	@Override
	public Mono<Boolean> hasKey(String key) {
		return redisTemplate.hasKey(key)
			.onErrorReturn(false);
	}
}
