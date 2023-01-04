package com.example.dakudemo.shiro.cache;


import com.example.dakudemo.util.ApplicationContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;


import java.util.Collection;
import java.util.Set;

/**
 * @author chh
 * @date 2022/2/19 20:10
 * 自定义RedisCache实现Cache
 */
@Slf4j
public class RedisCache<k,v> implements Cache<k,v> {
    private String cacheName;

    public RedisCache() {
    }

    public RedisCache(String cacheName) {
        this.cacheName = cacheName;

    }

    @Override
    public v get(k k) throws CacheException {
        String key = null;
        try {
            key = getKey(k);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("get:" + key);
        return (v) getRedisTemplate().opsForHash().get(this.cacheName, key);
    }

    @Override
    public v put(k k, v v) throws CacheException {
        String key = null;
        try {
            key = getKey(k);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("put key:" + k);
        System.out.println("put value:" + v);
        getRedisTemplate().opsForHash().put(this.cacheName, key, v);
        return null;
    }

    @Override
    public v remove(k k) throws CacheException {
        String key = null;
        try {
            key = getKey(k);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return (v) getRedisTemplate().opsForHash().delete(this.cacheName,key);
    }

    @Override
    public void clear() throws CacheException {
        getRedisTemplate().delete(this.cacheName);
    }

    @Override
    public int size() {
        return getRedisTemplate().opsForHash().size(this.cacheName).intValue();
    }

    @Override
    public Set<k> keys() {
        return (Set<k>) getRedisTemplate().opsForHash().keys(this.cacheName);
    }

    @Override
    public Collection<v> values() {
        return (Collection<v>) getRedisTemplate().opsForHash().values(this.cacheName);
    }

    private RedisTemplate getRedisTemplate(){
        RedisTemplate redisTemplate = (RedisTemplate) ApplicationContextUtils.getBean("redisTemplate");
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        return redisTemplate;
    }

    private String getKey(k k) throws ClassNotFoundException {
        String key = null;
        if(k instanceof SimplePrincipalCollection){
            SimplePrincipalCollection collection = (SimplePrincipalCollection) k;
            key = (String) collection.getPrimaryPrincipal();
        }else if(k instanceof String){
            key = (String) k;
        }else {
            log.error(this.getClass().getName() + " does not support this class of key:"+k.getClass().toString());
            throw new ClassNotFoundException(this.getClass().getName() + " does not support this class of key:"+k.getClass().toString());
        }
        return key;
    }
}
