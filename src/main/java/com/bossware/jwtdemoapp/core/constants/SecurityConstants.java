package com.bossware.jwtdemoapp.core.constants;

public class SecurityConstants {
    public static final long EXPIRATION_TIME = 432_000_000;  //5 Days
    public static final String TOKEN_PREFIX = "Bearer ";  //Who ever has this token and if it starts with this string no need to verig
    public static final String JWT_TOKEN_HEADER = "Jwt-Token";
    public static final String TOKEN_CANNOT_BE_VERIFIED = "Token Can Not Be Verified";
    public static final String GET_BOSSWARE_ISSUER = "Bossware Framework;"; // Which issuer used this token
    public static final String GET_ARRAYS_AUDIENCE = "User Management Portal";
    public static final String AUTHORITIES = "authorities";
    public static final String FORBIDDEN_MESSAGE = "You need to login to access this page";
    public static final String ACCESS_DENIED_MESSAGE = "You do not have permission to access this page";
    public static final String OPTIONS_HTTP_METHOD = "OPTIONS";
    public static final String[] PUBLIC_URLS= {"/user/login","/user/register","/user/resetpassword/**","user/image/**"};
}
