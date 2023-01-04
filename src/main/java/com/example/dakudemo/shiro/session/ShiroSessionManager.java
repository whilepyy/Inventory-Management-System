package com.example.dakudemo.shiro.session;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.session.mgt.WebSessionKey;
import org.springframework.http.HttpStatus;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.Serializable;

/**
 * @author chh
 * @date 2022/2/26 17:27
 */
@Slf4j
public class ShiroSessionManager extends DefaultWebSessionManager {
    /**
     * 获取session
     * 优化单次请求需要多次访问redis的问题
     * @param sessionKey
     * @return
     * @throws UnknownSessionException
     */
    @Override
    protected Session retrieveSession(SessionKey sessionKey) throws UnknownSessionException {
        Serializable sessionId = getSessionId(sessionKey);

        ServletRequest request = null;
        if (sessionKey instanceof WebSessionKey) {
            request = ((WebSessionKey) sessionKey).getServletRequest();
        }

        if (request != null && null != sessionId) {
            Object sessionObj = request.getAttribute(sessionId.toString());
            if (sessionObj != null) {
                return (Session) sessionObj;
            }
        }


        //下面这个父类的方法我们之间提出来单独来实现
        Session session = super.retrieveSession(sessionKey);

        if (sessionId == null) {
            log.debug("Unable to resolve session ID from SessionKey [{}].  Returning null to indicate a " +
                    "session could not be found.", sessionKey);
            return null;
        }
//        //去缓存里面搜索session
//        Session session = retrieveSessionFromDataSource(sessionId);
//        if (session == null) {
//            //session ID was provided, meaning one is expected to be found, but we couldn't find one:
//            //因为种种原因session找不到了：比如手动删除了或者过期了
//            String msg = "Could not find session with ID [" + sessionId + "]";
//            log.warn(msg);
//            Response302(msg, ((WebSessionKey) sessionKey).getServletResponse());
//            throw new UnknownSessionException(msg);
//        }

        //=================单独实现结束================
        if (request != null) {
            request.setAttribute(sessionId.toString(), session);
        }
        return session;
    }

    private void Response302(String msg, ServletResponse response){
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setStatus(302);
        httpServletResponse.setCharacterEncoding("utf-8");
        httpServletResponse.setContentType("text/html");
        try {
            PrintWriter out = httpServletResponse.getWriter();
            out.append(msg);
        }catch (Exception e){
            log.error("直接返回Response信息异常"+ e.getMessage());
            e.printStackTrace();
        }
    }
}
