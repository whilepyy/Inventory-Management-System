package com.example.dakudemo.shiro.cache;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;

/**
 * @author chh
 * @date 2022/2/19 20:06
 * 自定义Redis缓存管理器实现CacheManager
 */
public class RedisCacheManager implements CacheManager {
    /*此处参数为CacheName*/
    @Override
    public <K, V> Cache<K, V> getCache(String cacheName) throws CacheException {
        return new RedisCache<K, V>(cacheName);
    }
}
