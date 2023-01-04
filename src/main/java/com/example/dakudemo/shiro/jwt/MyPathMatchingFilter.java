package com.example.dakudemo.shiro.jwt;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

public abstract class MyPathMatchingFilter implements Filter {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private String suffix;

    protected boolean mustApply(final ServletRequest request) {
        final String path = getPath((HttpServletRequest)request);
        logger.debug("path: {} | suffix: {}", path, suffix);
        if (StringUtils.isEmpty(suffix)) {
            return true;
        } else {
            return path != null && path.endsWith(suffix);//判断后缀是否相等  相等返回true
        }
    }

    public String getPath(HttpServletRequest request) {
        final String fullPath = request.getRequestURI();
        // it shouldn't be null, but in case it is, it's better to return empty string
        if (fullPath == null) {
            return "";
        }
        final String context = request.getContextPath();
        // this one shouldn't be null either, but in case it is, then let's consider it is empty
        if (context != null) {
            return fullPath.substring(context.length());
        }
        return fullPath;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(final String suffix) {
        this.suffix = suffix;
    }

}
