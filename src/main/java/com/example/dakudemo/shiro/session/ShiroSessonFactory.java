package com.example.dakudemo.shiro.session;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SessionContext;
import org.apache.shiro.session.mgt.SessionFactory;

/**
 * @author chh
 * @date 2022/2/26 17:25
 */
public class ShiroSessonFactory implements SessionFactory {

    @Override
    public Session createSession(SessionContext initData) {
        ShiroSession session = new ShiroSession();
        return session;
    }

}
