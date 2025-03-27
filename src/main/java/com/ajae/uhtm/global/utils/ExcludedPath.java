package com.ajae.uhtm.global.utils;

public class ExcludedPath {

    private ExcludedPath() {
        throw new IllegalStateException("Utility class");
    }

    public static boolean isExcludedPath(String requestURI, String method) {
        return requestURI.equals("/login") ||
                requestURI.equals("/logout") ||
                requestURI.contains("/css/style") ||
                requestURI.startsWith("/static/") ||
                requestURI.contains("/img/") ||
                requestURI.contains("manifest") ||
                requestURI.contains("/login.js") ||
                requestURI.startsWith("/oauth2/authorization") ||  // OAuth2 로그인 요청 경로
                requestURI.startsWith("/login/oauth2/code") ||
                requestURI.equals("/api/v1/joke") && method.equalsIgnoreCase("GET") ||
                requestURI.startsWith("/api/v1/check") ;
    }

    public static boolean isTokenExcluded(String requestURI) {
        return requestURI.equals("/api/v1/loginCheck") ||
                requestURI.equals("/api/v1/userInfo") ||
                requestURI.equals("/api/v1/allUserJoke") ||
                requestURI.equals("/api/sheet/read") ||
                requestURI.equals("/api/v1/userJoke");
    }
}
