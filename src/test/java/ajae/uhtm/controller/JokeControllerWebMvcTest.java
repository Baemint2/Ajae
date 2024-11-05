package ajae.uhtm.controller;

import ajae.uhtm.auth.CustomUserDetails;
import ajae.uhtm.auth.UserSecurityService;
import ajae.uhtm.config.SecurityConfig;
import ajae.uhtm.controller.joke.JokeController;
import ajae.uhtm.dto.joke.JokeDto;
import ajae.uhtm.entity.Joke;
import ajae.uhtm.entity.JokeType;
import ajae.uhtm.entity.User;
import ajae.uhtm.entity.UserJoke;
import ajae.uhtm.filter.JwtAuthorizationFilter;
import ajae.uhtm.repository.joke.JokeRepository;
import ajae.uhtm.repository.userJoke.UserJokeRepository;
import ajae.uhtm.service.JokeService;
import ajae.uhtm.service.UserJokeService;
import ajae.uhtm.service.UserService;
import ajae.uhtm.utils.JwtUtil;
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
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.crypto.SecretKey;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static ajae.uhtm.utils.JwtUtil.EXP_LONG;
import static ajae.uhtm.utils.JwtUtil.TOKEN_PREFIX;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WebMvcTest(JokeController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
class JokeControllerWebMvcTest {

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserSecurityService userSecurityService;

    @MockBean
    private JokeService jokeService;

    @MockBean
    private JokeRepository jokeRepository;

    @MockBean
    private UserJokeService userJokeService;

    @MockBean
    private UserService userService;

    @MockBean
    private UserJokeRepository userJokeRepository;

    @MockBean
    private SecurityConfig securityConfig;

    @RegisterExtension
    final RestDocumentationExtension restDocumentation = new RestDocumentationExtension("build/generated-snippets/joke-controller-test");

    User testUser;

    Joke testJoke, testJoke2;

    UserJoke testUserJoke, testUserJoke2;

    private SecretKey key;

    @Value("${jwt.secret}")
    private String secretKey;

    RequestPostProcessor jwtCookieProcessor;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .addFilters(new JwtAuthorizationFilter(jwtUtil, userSecurityService))
                .apply(documentationConfiguration(restDocumentation))
                .defaultRequest(post("/**").with(csrf()))
                .defaultRequest(delete("/**").with(csrf()))
                .build();

        testUser = User.builder()
                .providerKey("testProvider")
                .nickname("모지희")
                .build();

        testUser.testUserId(1L);

        testJoke = Joke.builder()
                .question("개가 한 마리만 사는 나라는?")
                .answer("독일")
                .jokeType(JokeType.USER_ADDED)
                .build();

        testJoke2 = Joke.builder()
                .question("아몬드가 죽으면?")
                .answer("다이아몬드")
                .jokeType(JokeType.USER_ADDED)
                .build();

        testJoke.testJokeId(1L);
        testJoke2.testJokeId(2L);

        testUserJoke = UserJoke.builder()
                .joke(testJoke)
                .user(testUser)
                .build();
        testUserJoke.testUserJokeId(1L);

        testUserJoke2 = UserJoke.builder()
                .joke(testJoke2)
                .user(testUser)
                .build();
        testUserJoke.testUserJokeId(2L);

        doNothing().when(jwtUtil).init();

        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        when(jwtUtil.getKey()).thenReturn(key);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");

        List<GrantedAuthority> authorities = Collections.singletonList(authority);

        when(userSecurityService.loadUserByUsername(auth.getName())).thenReturn(new CustomUserDetails(testUser.toDto(), authorities));

        when(jwtUtil.createAccessToken(auth)).thenReturn(Jwts.builder()
                .issuer("moz1mozi.com")
                .subject(auth.getName())
                .expiration(new Date(System.currentTimeMillis() + EXP_LONG))
                .claim("username", auth.getName())
                .claim("role", authority)
                .signWith(key)
                .compact());

        String jwtToken = jwtUtil.createAccessToken(auth);
        when(jwtUtil.validateToken(jwtToken)).thenReturn(true);
        when(jwtUtil.verify(jwtToken)).thenReturn(Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(jwtToken.replace(TOKEN_PREFIX, ""))
                .getPayload());
        jwtCookieProcessor = request -> {
            request.setCookies(new Cookie("accessToken", jwtToken));
            return request;
        };
    }

    @Test
    @WithMockUser
    @DisplayName("개그를 하나 조회한다.")
    void getJoke() throws Exception {
        // given: mock으로 고정된 값을 반환하게 설정
        JokeDto fixedJoke = Joke.builder()
                        .question("딸기가 직장을 잃으면?")
                        .answer("딸기 시럽")
                        .build().toDto();

        when(jokeService.getRandomJoke()).thenReturn(fixedJoke);  // getJoke() 호출 시 고정된 joke 객체를 반환

        // when & then
        mockMvc.perform(get("/api/v1/joke"))
                .andExpect(jsonPath("$.question").value(fixedJoke.getQuestion()))  // 고정된 값으로 검증
                .andExpect(jsonPath("$.answer").value(fixedJoke.getAnswer()))  // 고정된 값으로 검증
                .andDo(print())
                .andDo(document("jokes/get",
                        responseFields(
                                fieldWithPath("jokeId").description("개그 ID"),
                                fieldWithPath("question").description("문제"),
                                fieldWithPath("answer").description("정답"))
                ));
    }

    @Test
    @WithMockUser
    @DisplayName("유저가 개그를 등록한다.")
    void saveJoke() throws Exception {

        JokeDto request = JokeDto.builder()
                .id(1)
                .question("말과 소가 햄버거 가게를 차리면?")
                .answer("소말리아")
                .jokeType(JokeType.USER_ADDED)
                .build();

        when(jokeService.saveJoke(any(Joke.class), any(String.class))).thenReturn(request.toEntity());
        mockMvc.perform(post("/api/v1/joke")
                .contentType(APPLICATION_JSON)
                .with(jwtCookieProcessor)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(jsonPath("$.message").value("문제 등록이 완료되었습니다."))
                .andDo(print())
                .andDo(document("jokes/post",
                        requestFields(
                                fieldWithPath("jokeId").description("개그번호"),
                                fieldWithPath("question").description("문제"),
                                fieldWithPath("answer").description("정답")),
                        responseFields(
                                fieldWithPath("message").description( "문제 등록이 완료되었습니다.")
                        )));
    }

    @Test
    @WithMockUser
    @DisplayName("유저개그 리스트를 조회한다. (개그와 유저정보) ")
    void getAllUserJokes() throws Exception {
        when(userJokeRepository.selectAllUserJoke(JokeType.USER_ADDED)).thenReturn(List.of(testUserJoke, testUserJoke2));
        when(userJokeService.getAllUserJokes()).thenReturn(List.of(testUserJoke.toDto(), testUserJoke2.toDto()));
        mockMvc.perform(get("/api/v1/allUserJoke"))
                .andDo(print())
                .andDo(document("userJokes/get",
                        responseFields(
                                fieldWithPath("[].joke.jokeId").description("개그 ID"),
                                fieldWithPath("[].joke.question").description("개그 질문"),
                                fieldWithPath("[].joke.answer").description("개그 답변"),
                                fieldWithPath("[].user.id").description("유저 ID"),
                                fieldWithPath("[].user.profile").description("유저 프로필 (null 가능)"),
                                fieldWithPath("[].user.nickname").description("유저 닉네임")
                        )));
    }

    @Test
    @WithMockUser
    @DisplayName("유저개그 빈 리스트를 조회 한다. (개그와 유저정보) ")
    void getAllUserJokesIsEmpty() throws Exception {
        when(userJokeRepository.selectAllUserJoke(JokeType.USER_ADDED)).thenReturn(List.of());
        when(userJokeService.getAllUserJokes()).thenReturn(List.of());
        mockMvc.perform(get("/api/v1/allUserJoke"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("등록된 개그가 없습니다."))
                .andDo(print());
    }

    @Test
    @WithMockUser
    @DisplayName("특정 유저의 유저개그 리스트를 조회한다. ")
    void getUserJokes() throws Exception {
        when(userService.findByUsername(testUser.getProviderKey())).thenReturn(testUser);
        when(userJokeService.findAllJokesByUserId(testUser.getId())).thenReturn(List.of(testJoke.toDto(), testJoke2.toDto()));
        mockMvc.perform(get("/api/v1/userJoke")
                        .with(jwtCookieProcessor))
                .andExpect(status().isOk())
                .andDo(print());
    }
}
