package com.example.dakudemo.shiro.session;

import com.example.dakudemo.util.JsonCovertUtils;
import com.example.dakudemo.util.SerializeUtils;
import com.example.dakudemo.util.StringUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.ValidatingSession;
import org.apache.shiro.session.mgt.eis.CachingSessionDAO;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.apache.shiro.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author chh
 * @date 2022/2/26 17:31
 */
public class RedisSessionDao extends CachingSessionDAO {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisSessionDao.class);
    private String prefix = "sessionId:";
    // 设置会话的过期时间
    private int seconds = 100;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    protected Serializable doCreate(Session session) {
        // 创建一个Id并设置给Session
        Serializable sessionId = this.generateSessionId(session);
        assignSessionId(session, sessionId);
        try {
            session.setTimeout(seconds);
            redisTemplate.opsForValue().set(prefix + sessionId, SerializeUtils.serializeToString((ShiroSession)session),seconds, TimeUnit.MINUTES);
            LOGGER.info("sessionId {} name {} 被创建", sessionId, session.getClass().getName());
        } catch (Exception e) {
            LOGGER.warn("创建Session失败", e);
        }
        return sessionId;
    }

    @Override
    public Session readSession(Serializable sessionId) throws UnknownSessionException {
        Session session = getCachedSession(sessionId);
        if (session == null
                || session.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY) == null) {
            session = this.doReadSession(sessionId);
            if (session == null) {
                return null;
                //throw new UnknownSessionException("There is no session with id [" + sessionId + "]");
            } else {
                // 缓存
                cache(session, session.getId());
            }
        }
        return session;
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {
        Session session = null;
        try {
            String key = prefix + sessionId;
            String value = redisTemplate.opsForValue().get(key);
            if (value != null) {
                redisTemplate.opsForValue().set(key,value,seconds, TimeUnit.MINUTES);
                session = SerializeUtils.deserializeFromString(value);
                LOGGER.info("sessionId {} name {} 被读取", sessionId, session.getClass().getName());

            }
        } catch (Exception e) {
            LOGGER.warn("读取Session失败", e);
        }
        return session;
    }
    public Session doReadSessionWithoutExpire(Serializable sessionId) {
        Session session = null;
        try {
            String key = prefix + sessionId;
            String value = redisTemplate.opsForValue().get(key);
            if (StringUtils.isNotBlank(value)) {
                session = SerializeUtils.deserializeFromString(value);
            }
        } catch (Exception e) {
            LOGGER.warn("读取Session失败", e);
        }
        return session;
    }


    @Override
    protected void doUpdate(Session session) {
        //如果会话过期/停止 没必要再更新了
        try {
            if (session instanceof ValidatingSession && !((ValidatingSession) session).isValid()) {
                return;
            }
        } catch (Exception e) {
            LOGGER.error("ValidatingSession error");
        }

        try {
            if (session instanceof ShiroSession) {
                // 如果没有主要字段(除lastAccessTime以外其他字段)发生改变
                ShiroSession ss = (ShiroSession) session;
                if (!ss.isChanged()) {
                    return;
                }
                try {
                    ss.setChanged(false);
                    redisTemplate.opsForValue().set(prefix + session.getId(), SerializeUtils.serializeToString(ss),seconds,TimeUnit.MINUTES);
//                    redisTemplate.opsForValue().set(prefix + session.getId(), JsonCovertUtils.objToJson(ss),seconds,TimeUnit.MINUTES);
                    LOGGER.info("sessionId {} name {} 被更新", session.getId(), session.getClass().getName());
                    // 执行事务
                } catch (Exception e) {
                    throw e;
                }

            } else if (session instanceof Serializable) {
                redisTemplate.opsForValue().set(prefix + session.getId(), JsonCovertUtils.objToJson((Serializable) session),seconds,TimeUnit.MINUTES);
                LOGGER.info("sessionId {} name {} 作为非ShiroSession对象被更新, ", session.getId(), session.getClass().getName());
            } else {
                LOGGER.warn("sessionId {} name {} 不能被序列化 更新失败", session.getId(), session.getClass().getName());
            }
        } catch (Exception e) {
            LOGGER.warn("更新Session失败", e);
        }
    }

    @Override
    protected void doDelete(Session session) {
        try {
            redisTemplate.delete(prefix + session.getId());
            LOGGER.debug("Session {} 被删除", session.getId());
        } catch (Exception e) {
            LOGGER.warn("修改Session失败", e);
        }
    }

    /**
     * 删除cache中缓存的Session
     */
    public void uncache(Serializable sessionId) {
        Session session = this.readSession(sessionId);
        super.uncache(session);
        LOGGER.info("取消session {} 的缓存", sessionId);
    }

    /**
     * 获取当前所有活跃用户，如果用户量多此方法影响性能
     */
    @Override
    public Collection<Session> getActiveSessions() {
        try {
            Set<String> keys = redisTemplate.keys(prefix + "*");
            if (CollectionUtils.isEmpty(keys)) {
                return null;
            }
            List<String> valueList = redisTemplate.opsForValue().multiGet(keys);
//            return SerializeUtils.deserializeFromStringController(valueList);
//            return JsonCovertUtils.jsonToObj(valueList,ArrayList.class);
        } catch (Exception e) {
            LOGGER.warn("统计Session信息失败", e);
        }
        return null;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }
}
