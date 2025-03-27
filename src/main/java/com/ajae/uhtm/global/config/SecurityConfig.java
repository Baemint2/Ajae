package com.ajae.uhtm.global.config;

import com.ajae.uhtm.global.auth.CustomAuthenticationEntryPoint;
import com.ajae.uhtm.global.auth.LoginSuccessHandler;
import com.ajae.uhtm.global.auth.UserSecurityService;
import com.ajae.uhtm.global.auth.oauth2.CustomOAuth2AccessTokenResponseClient;
import com.ajae.uhtm.global.auth.oauth2.OAuth2UserService;
import com.ajae.uhtm.global.auth.oauth2.OauthProviderConfigService;
import com.ajae.uhtm.global.filter.JwtAuthorizationFilter;
import com.ajae.uhtm.global.utils.JwtTokenFactory;
import com.ajae.uhtm.global.utils.JwtVerifier;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@EnableMethodSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final OAuth2UserService oAuth2UserService;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final OauthProviderConfigService providerConfigService;

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http,
                                    JwtTokenFactory jwtTokenFactory,
                                    JwtVerifier jwtVerifier,
                                    UserSecurityService userSecurityService) throws Exception {
        http
            .cors(AbstractHttpConfigurer::disable)
            .csrf(AbstractHttpConfigurer::disable)
            .headers(headersConfigurer -> headersConfigurer.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
            .authorizeHttpRequests(request -> request
                    .requestMatchers("/api/v1/bookmark").authenticated()
                            .anyRequest().permitAll())
            .oauth2Login(oauth2 -> oauth2
                            .loginPage("/login")
                    .userInfoEndpoint(userInfo ->
                    userInfo.userService(oAuth2UserService))
                    .tokenEndpoint(token ->
                            token.accessTokenResponseClient(accessTokenResponseClient()))
                    .successHandler(new LoginSuccessHandler(jwtTokenFactory, jwtVerifier))
            )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(customAuthenticationEntryPoint))
                .addFilterBefore(new JwtAuthorizationFilter(jwtVerifier, userSecurityService), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient() {
        return new CustomOAuth2AccessTokenResponseClient(providerConfigService);
    }
}
