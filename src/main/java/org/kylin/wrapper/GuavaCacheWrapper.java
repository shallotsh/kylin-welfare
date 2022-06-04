package org.kylin.wrapper;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Slf4j
@Component
public class GuavaCacheWrapper<T> {

    private Cache<String, T> cache;

    @PostConstruct
    public void init(){
        cache = CacheBuilder.newBuilder()
                .maximumSize(100)
                .expireAfterWrite(120, TimeUnit.MINUTES)
                .concurrencyLevel(Runtime.getRuntime().availableProcessors())
                .recordStats()
                .build();
        log.info("cache initialized.");
    }


    public void put(String key, T obj){
        assertCache();
        cache.put(key, obj);
    }

    public T  getIfPresent(String key){
        assertCache();
        return cache.getIfPresent(key);
    }

    public T get(String key, Supplier<T> supplier){
        try {
            T val = cache.getIfPresent(key);
            if(val != null){
                return val;
            }
            val = supplier.get();
            if(val != null){
                cache.put(key, val);
            }
            return val;
        } catch (Exception e) {
            log.error("guava get error. key:{}", key, e);
            return supplier.get();
        }
    }


    public void invalidateAll(){
        assertCache();
        cache.invalidateAll();
    }

    public void invalidate(String key){
        assertCache();
        cache.invalidate(key);
    }


    public void assertCache(){
        if(Objects.isNull(cache)){
            throw new RuntimeException("Cache is not be initialized.");
        }
    }

}
