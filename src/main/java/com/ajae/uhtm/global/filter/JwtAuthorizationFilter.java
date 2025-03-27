package com.ajae.uhtm.global.filter;


import com.ajae.uhtm.global.auth.CustomUserDetails;
import com.ajae.uhtm.global.auth.UserSecurityService;
import com.ajae.uhtm.global.utils.JwtVerifier;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import static com.ajae.uhtm.global.utils.ExcludedPath.isExcludedPath;
import static com.ajae.uhtm.global.utils.ExcludedPath.isTokenExcluded;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    public static final String ACCESS_TOKEN = "accessToken";
    public static final String REFRESH_TOKEN = "refreshToken";
    private final JwtVerifier jwtVerifier;
    private final UserSecurityService userSecurityService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String token = resolveToken(request);

        if (isExcludedPath(request.getRequestURI(), request.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        if (token == null) {
            log.info("Token is null");
            if (!isTokenExcluded(request.getRequestURI())) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token is null");
                return;
            }
            logRequestExecutionTime(request, chain, response);
            return;
        }

        try {
            if (!jwtVerifier.validateToken(token)) {
                log.info("Invalid JWT token");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
                return;
            }
            Claims claims = jwtVerifier.verify(token);
            log.info("claims: {}", claims);
            String username = claims.getSubject();
            log.info("Username from token: {}", username);

            UserDetails userDetails = userSecurityService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken authentication = getAuthentication(userDetails, request);
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (ExpiredJwtException ex) {
            log.info("Expired JWT token");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Expired JWT token");
            return;

        } catch (Exception e) {
            log.error("JWT Authentication failed", e);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT Authentication failed");
            return;
        }
        logRequestExecutionTime(request, chain, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String token = extractTokenFromHeader(request);
        return (token != null) ? token : extractTokenFromCookies(request);
    }

    private static String extractTokenFromHeader(HttpServletRequest request) {
        String header = request.getHeader(JwtVerifier.HEADER);
        if (header != null && header.startsWith(JwtVerifier.TOKEN_PREFIX)) {
            return header.replace(JwtVerifier.TOKEN_PREFIX, "");
        }
        return null;
    }

    private static String extractTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) return null;

        Map<String, String> cookieMap = Arrays.stream(cookies)
                .collect(Collectors.toMap(Cookie::getName, Cookie::getValue, (a, b) -> b));

        if (cookieMap.containsKey(ACCESS_TOKEN)) {
            return cookieMap.get(ACCESS_TOKEN);
        } else if (cookieMap.containsKey(REFRESH_TOKEN)) {
            return cookieMap.get(REFRESH_TOKEN);
        }

        return null;
    }

    private UsernamePasswordAuthenticationToken getAuthentication(UserDetails userDetails, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authentication = null;
        if (userDetails instanceof CustomUserDetails customUserDetails) {
            authentication = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        }
        if (authentication != null) {
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        }
        return authentication;
    }

    private void logRequestExecutionTime(HttpServletRequest request, FilterChain chain, HttpServletResponse response) throws IOException, ServletException {
        long start = System.currentTimeMillis();
        try {
            chain.doFilter(request, response);
        } finally {
            long executionTime = System.currentTimeMillis() - start;
            log.info("요청 [{}] 완료: 실행 시간 {} ms", request.getRequestURI(), executionTime);
        }
    }
}
