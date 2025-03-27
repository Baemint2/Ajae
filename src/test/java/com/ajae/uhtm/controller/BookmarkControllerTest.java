package com.ajae.uhtm.controller;

import com.ajae.uhtm.controller.bookmark.BookmarkController;
import com.ajae.uhtm.domain.bookmark.Bookmark;
import com.ajae.uhtm.domain.joke.Joke;
import com.ajae.uhtm.domain.joke.JokeType;
import com.ajae.uhtm.domain.user.User;
import com.ajae.uhtm.dto.joke.JokeDto;
import com.ajae.uhtm.global.auth.CustomUserDetails;
import com.ajae.uhtm.global.auth.UserSecurityService;
import com.ajae.uhtm.global.config.SecurityConfig;
import com.ajae.uhtm.global.filter.JwtAuthorizationFilter;
import com.ajae.uhtm.global.utils.JwtTokenFactory;
import com.ajae.uhtm.global.utils.JwtVerifier;
import com.ajae.uhtm.repository.bookmark.BookmarkRepository;
import com.ajae.uhtm.service.BookmarkService;
import com.ajae.uhtm.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.crypto.SecretKey;
import java.util.*;

import static com.ajae.uhtm.global.utils.JwtTokenFactory.EXP_LONG;
import static com.ajae.uhtm.global.utils.JwtVerifier.TOKEN_PREFIX;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@WebMvcTest(BookmarkController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
class BookmarkControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private BookmarkService bookmarkService;

    @Mock
    private BookmarkRepository bookmarkRepository;

    @MockBean
    private JwtAuthorizationFilter jwtFilter;

    @MockBean
    private SecurityConfig securityConfig;

    @InjectMocks
    OAuth2UserService oAuth2UserService;

    @MockBean
    private JwtTokenFactory jwtTokenFactory;
    @MockBean
    private JwtVerifier jwtVerifier;

    @MockBean
    private UserSecurityService userSecurityService;

    @Mock
    private DefaultOAuth2UserService delegate;


    JokeDto jokeDto, jokeDto2;

    User testUser;

    Joke testJoke, testJoke2;

    Bookmark testBookmark, testBookmark2;

    SecretKey key;

    @Value("${jwt.secret}")
    private String secretKey;

    RequestPostProcessor jwtCookieProcessor;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .apply(springSecurity())
                .addFilters(new JwtAuthorizationFilter(jwtVerifier, userSecurityService))
                .defaultRequest(post("/**").with(csrf()))
                .defaultRequest(delete("/**").with(csrf()))
                .build();
        testUser = User.builder()
                .providerKey("testProvider")
                .nickname("모지희")
                .build();

        testUser.testUserId(99999L);

        testJoke = Joke.builder()
                .question("개가 한 마리만 사는 나라는?")
                .answer("독일")
                .jokeType(JokeType.USER_ADDED)
                .build();

        testJoke2 = Joke.builder()
                .question("테스트 문제")
                .answer("테스트 정답")
                .jokeType(JokeType.USER_ADDED)
                .build();

        testJoke.testJokeId(1L);
        testJoke2.testJokeId(2L);

        testBookmark = Bookmark.builder()
                .user(testUser)
                .joke(testJoke)
                .build();

        testBookmark2 = Bookmark.builder()
                .user(testUser)
                .joke(testJoke2)
                .build();

        testBookmark.testBookmarkId(99998L);
        testBookmark2.testBookmarkId(99999L);

        key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        when(jwtTokenFactory.getKey()).thenReturn(key);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");

        List<GrantedAuthority> authorities = Collections.singletonList(authority);

        when(userSecurityService.loadUserByUsername(auth.getName())).thenReturn(new CustomUserDetails(testUser.toDto(), authorities));

        when(jwtTokenFactory.createAccessToken(auth)).thenReturn(Jwts.builder()
                .issuer("moz1mozi.com")
                .subject(auth.getName())
                .expiration(new Date(System.currentTimeMillis() + EXP_LONG))
                .claim("username", auth.getName())
                .claim("role", authority)
                .signWith(key)
                .compact());

        String jwtToken = jwtTokenFactory.createAccessToken(auth);
        when(jwtVerifier.validateToken(jwtToken)).thenReturn(true);
        when(jwtVerifier.verify(jwtToken)).thenReturn(Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(jwtToken.replace(TOKEN_PREFIX, ""))
                .getPayload());
        jwtCookieProcessor = request -> {
            request.setCookies(new Cookie("accessToken", jwtToken));
            return request;
        };

    }

    @RegisterExtension
    final RestDocumentationExtension restDocumentation = new RestDocumentationExtension("build/generated-snippets/bookmark-controller-test");

    @Test
    @WithMockUser
    @DisplayName("특정 유저가 특정 개그를 북마크등록 했는지 체크한다.")
    void checkBookmark() throws Exception {

        when(bookmarkService.checkBookmark(any(String.class), any(Long.class))).thenReturn(true);
        mockMvc.perform(post("/api/v1/check")
                        .contentType(APPLICATION_JSON)
                        .with(jwtCookieProcessor)
                        .content(objectMapper.writeValueAsString(testJoke.toDto())))
                .andExpect(status().isOk())
                .andExpect(content().string("true"))
                .andDo(print());
    }

    @Test
    @WithMockUser
    @DisplayName("특정 유저가 특정 개그를 북마크등록 했는지 체크한다.")
    void checkBookmark_fail() throws Exception {
        when(bookmarkService.checkBookmark(any(String.class), any(Long.class))).thenReturn(false);
        mockMvc.perform(post("/api/v1/check")
                        .contentType(APPLICATION_JSON)
                        .with(jwtCookieProcessor)
                        .content(objectMapper.writeValueAsString(testJoke.toDto())))
                .andExpect(status().isOk())
                .andExpect(content().string("false"))
                .andDo(print())
                .andDo(document("bookmarks/post"));
    }

    @Test
    @WithMockUser
    @DisplayName("특정 유저가 북마크에 등록한 개그를 리스트로 조회한다.")
    void getAllJoke() throws Exception {

        List<JokeDto> result = List.of(testJoke.toDto(), testJoke2.toDto());
        when(bookmarkService.getAllJoke(any(String.class))).thenReturn(result);
        mockMvc.perform(get("/api/v1/allJoke")
                        .with(jwtCookieProcessor)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("bookmarks/get"));
    }

    @Test
    @WithMockUser
    @DisplayName("특정 유저가 북마크에 등록한 개그가 없다.")
    void getAllJokeIsEmpty() throws Exception {

        when(bookmarkService.getAllJoke(any(String.class))).thenReturn(List.of());
        mockMvc.perform(get("/api/v1/allJoke")
                        .with(jwtCookieProcessor)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value( "등록된 북마크가 없습니다."))
                .andDo(print());
    }

    @Test
    @WithMockUser
    @DisplayName("특정 유저가 특정 개그를 북마크에 등록한다.")
    void addBookmark() throws Exception {
        when(bookmarkService.addBookmark(any(JokeDto.class), any(String.class))).thenReturn( 10L);

        mockMvc.perform(post("/api/v1/bookmark")
                        .contentType(APPLICATION_JSON)
                        .with(jwtCookieProcessor)
                        .with(user("testUser").roles("USER"))
                        .content(objectMapper.writeValueAsString(testJoke.toDto())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("북마크 등록되었습니다."))
                .andDo(print());
    }

    @Test
    @WithMockUser()
    @DisplayName("특정 유저가 특정 개그를 북마크에 등록 실패한다.")
    void addBookmark_fail() throws Exception {
        when(bookmarkService.addBookmark(any(JokeDto.class), any(String.class))).thenReturn( 0L);
        mockMvc.perform(post("/api/v1/bookmark")
                        .contentType(APPLICATION_JSON)
                        .with(jwtCookieProcessor)
                        .with(user("testUser").roles("USER"))
                        .content(objectMapper.writeValueAsString(testJoke.toDto())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("북마크 등록에 실패했습니다."))
                .andDo(print());
    }

    @Test
    @WithMockUser
    @DisplayName("특정 유저의 특정 북마크를 제거한다.")
    void deleteBookmark() throws Exception {

        when(bookmarkService.getAllJoke(testUser.getProviderKey())).thenReturn(List.of(testJoke.toDto(), testJoke2.toDto()));
        when(bookmarkService.deleteBookmark(any(String.class), any(Long.class))).thenReturn(1);

        String requestBody = objectMapper.writeValueAsString(testJoke.toDto());

        mockMvc.perform(delete("/api/v1/bookmark")
                    .contentType(APPLICATION_JSON)
                    .with(jwtCookieProcessor)
                    .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("북마크가 제거되었습니다."))
                .andDo(print());
    }

    @Test
    @WithMockUser
    @DisplayName("특정 유저가 등록한 북마크의 개수를 조회한다")
    void countBookmarkByUser() throws Exception {
        when(userService.findByUsername(testUser.getProviderKey())).thenReturn(testUser);
        when(bookmarkService.countBookmark(testUser.getId())).thenReturn(2L);
        mockMvc.perform(get("/api/v1/bookmark/count")
                .with(jwtCookieProcessor))
                .andExpect(status().isOk())
                .andDo(print());
    }
}