package br.caffeine.demo.config;

import br.caffeine.demo.utils.CacheConstants;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.Getter;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
@Getter
public class JavaCacheConfig {

    private Cache<Object, Object> cache;

    @Primary
    @Bean
    public CacheManager databaseCacheManager() { // Gerencia o cache automaticamente com @Cacheable, @CachePut, etc...
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(CacheConstants.DATABASE_CACHE);
        cacheManager.setCaffeine(databaseCacheBuilder());

        return cacheManager;
    }

    Caffeine<Object, Object> databaseCacheBuilder() {
        return Caffeine.newBuilder()
                .expireAfterWrite(2, TimeUnit.MINUTES)
                .recordStats();
    }

    @Bean // Cache que pode ser modificado manualmente através do controller
    public Cache<Object, Object> secodaryCacheBuilder() {
        Cache<Object, Object> cache = Caffeine.newBuilder()
                .expireAfterWrite(20, TimeUnit.SECONDS)
                .recordStats()
                .build();

        this.cache = cache;

        return this.cache;
    }




}
