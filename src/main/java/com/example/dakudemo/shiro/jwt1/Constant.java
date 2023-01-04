package com.example.dakudemo.shiro.jwt1;

public class Constant {
    public static final String SUCCESS_CODE = "100";
    public static final String SUCCESS_MSG = "请求成功";

    /**
     * session中存放用户信息的key值
     */
    public static final String SESSION_USER_INFO = "userInfo";
    public static final String SESSION_USER_PERMISSION = "userPermission";


    /*------------------------------------------------cas--------------------------------------*/

    public static final String CLAIM_KEY_USERNAME = "sub";

    public static final String CLAIM_KEY_AUDIENCE = "audience";

    public static final String CLAIM_KEY_CREATED = "created";

    public static final String AUDIENCE_UNKNOWN = "unknown";

    public static final String AUDIENCE_WEB = "web";

    public static final String AUDIENCE_MOBILE = "mobile";

    public static final String AUDIENCE_TABLET = "tablet";

    public static final String TOKEN = "token";

    public static final String AUTHORIZATION_HEADER = "Authorization";

    public final static String DEFAULT_CALLBACK_SUFFIX = "/callback";

    public static final String TICKET_NAME = "ticket";

    public static final String TICKET_MULTIPART = "multipart";

    public static final String CONTENT_TYPE_NAME = "Content-Type";

    public static final String LOGOUT_NAME = "logoutRequest";

    public static final int INDEX_NOT_FOUND = -1;

    public static final String USER_IP = "clientIpAddress";

    public static final String DEPARTMENT = "department";

    public static final String REDIRECT_URL = "redirectUrl";

    public static final String CLIENT_IP_ADDRESS = "clientIpAddress";

    public static final String DISPLAY_NAME = "displayName";
}
