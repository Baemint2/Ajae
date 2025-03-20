package com.ajae.uhtm.global.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

public class CookieUtil {

    private CookieUtil() {
    }

    public static void createCookie(HttpServletResponse response, String name, int maxAge) {
        Cookie cookie = new Cookie(name, null);
        cookie.setPath("/");
        cookie.setHttpOnly(false);
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }


    public static void deleteCookie(HttpServletResponse response, String name) {
        Cookie cookie = new Cookie(name, null);
        cookie.setPath("/");
        cookie.setHttpOnly(false);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
