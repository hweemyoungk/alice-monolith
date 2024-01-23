package cards.alice.monolith.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.task.ThreadPoolTaskExecutorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import redis.clients.jedis.JedisPooled;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

@Configuration
public class RedisConfig {
    @Value("${cards.alice.common.jedis.server.host}")
    private String jedisHost;
    @Value("${cards.alice.common.jedis.server.port}")
    private int jedisPort;

    @Bean
    public JedisPooled jedis() {
        return new JedisPooled(jedisHost, jedisPort);
    }

    @Bean
    public Map<String, Future<?>> redeemRequestTtlTaskPool() {
        return new HashMap<>();
    }

    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setThreadNamePrefix("RedisManualTtlExecutor");
        return threadPoolTaskScheduler;
    }

    @Bean
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        return new ThreadPoolTaskExecutorBuilder()
                .threadNamePrefix("RedisManualTtlExecutor")
                .build();
    }
}
