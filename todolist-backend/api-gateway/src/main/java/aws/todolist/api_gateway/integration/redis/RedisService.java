package aws.todolist.api_gateway.integration.redis;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public interface RedisService {
	
	Mono<Boolean> set(String key, String value, Duration ttl);
	
	Mono<Optional<String>> get(String key);
	
	Mono<Boolean> hasKey(String key);
}

