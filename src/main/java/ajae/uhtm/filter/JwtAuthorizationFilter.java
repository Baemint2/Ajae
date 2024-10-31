package ajae.uhtm.filter;


import ajae.uhtm.auth.CustomUserDetails;
import ajae.uhtm.auth.UserSecurityService;
import ajae.uhtm.utils.JwtUtil;
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

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserSecurityService userSecurityService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String token = resolveToken(request);

        if (isExcludedPath(request.getRequestURI())) {
            chain.doFilter(request, response);
            return;
        }

        if (token != null) {
            try {
                if (jwtUtil.validateToken(token)) {
                    Claims claims = jwtUtil.verify(token);
                    log.info("claims: {}", claims);
                    String username = claims.getSubject();
                    log.info("Username from token: {}", username);

                    UserDetails userDetails = userSecurityService.loadUserByUsername(username);
                    UsernamePasswordAuthenticationToken authentication = getAuthentication(userDetails, request);

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    log.info("Invalid JWT token");
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
                    return;
                }
            } catch (ExpiredJwtException ex) {
                log.info("Expired JWT token");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Expired JWT token");
                return;
            } catch (Exception e) {
                log.error("JWT Authentication failed", e);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT Authentication failed");
                return;
            }
        } else {
            log.info("Token is null");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token is null");
            return;
        }
        logRequestExecutionTime(request, chain, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String header = request.getHeader(JwtUtil.HEADER);
        log.info("header: {}", header);
        if (header == null || !header.startsWith(JwtUtil.TOKEN_PREFIX)) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("accessToken")) {
                        return cookie.getValue();
                    } else if (cookie.getName().equals("refreshToken")) {
                        return cookie.getValue();
                    }
                }
            }
        } else {
            return header.replace(JwtUtil.TOKEN_PREFIX, "");
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

    private boolean isExcludedPath(String requestURI) {
        return requestURI.equals("/login") ||
//                requestURI.equals("/api/auth/logout/v1") ||
//                requestURI.equals("/user/login") ||
                requestURI.equals("/logout") ||
                requestURI.contains("/css/style") ||
                requestURI.startsWith("/static/") ||
                requestURI.contains("/img/") ||
                requestURI.contains("manifest") ||
                requestURI.contains("/login.js") ||
                requestURI.startsWith("/oauth2/authorization") ||  // OAuth2 로그인 요청 경로
                requestURI.startsWith("/login/oauth2/code");
    }
}
