package ajae.uhtm.controller;

import ajae.uhtm.auth.UserSecurityService;
import ajae.uhtm.config.SecurityConfig;
import ajae.uhtm.controller.bookmark.BookmarkController;
import ajae.uhtm.dto.joke.JokeDto;
import ajae.uhtm.entity.Joke;
import ajae.uhtm.service.BookmarkService;
import ajae.uhtm.auth.oauth2.OAuth2UserService;
import ajae.uhtm.service.UserService;
import ajae.uhtm.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    @MockBean
    private SecurityConfig securityConfig;

    @InjectMocks
    OAuth2UserService oAuth2UserService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserSecurityService userSecurityService;

    @Mock
    private DefaultOAuth2UserService delegate;

    OAuth2User oAuth2User;

    JokeDto jokeDto, jokeDto2;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .apply(springSecurity())
                .defaultRequest(post("/**").with(csrf()))
                .build();

        jokeDto = JokeDto.builder()
                .question("말과 소가 햄버거 가게를 차리면?")
                .answer("소말리아")
                .build();
    }

    @RegisterExtension
    final RestDocumentationExtension restDocumentation = new RestDocumentationExtension("build/generated-snippets/bookmark-controller-test");

    @Test
    @WithMockUser
    @DisplayName("특정 유저가 특정 개그를 북마크등록 했는지 체크한다.")
    void checkBookmark() throws Exception {
        when(bookmarkService.checkBookmark(any(String.class), any(String.class))).thenReturn(true);
        mockMvc.perform(post("/api/v1/check")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jokeDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("true"))
                .andDo(print());
    }

    @Test
    @WithMockUser
    @DisplayName("특정 유저가 특정 개그를 북마크등록 했는지 체크한다.")
    void checkBookmark_fail() throws Exception {
        when(bookmarkService.checkBookmark(any(String.class), any(String.class))).thenReturn(false);
        mockMvc.perform(post("/api/v1/check")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jokeDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("false"))
                .andDo(print())
                .andDo(document("bookmarks/post"));
    }

    @Test
    @WithMockUser
    @DisplayName("특정 유저가 북마크에 등록한 개그를 리스트로 조회한다.")
    void getAllJoke() throws Exception {
        jokeDto = JokeDto.builder()
                .question("말과 소가 햄버거 가게를 차리면?")
                .answer("소말리아")
                .build();

        jokeDto2 = JokeDto.builder()
                .question("아몬드가 죽으면?")
                .answer("다이아몬드")
                .build();

        List<JokeDto> result = List.of(jokeDto, jokeDto2);
        when(bookmarkService.getAllJoke(any(String.class))).thenReturn(result);
        mockMvc.perform(get("/api/v1/allJoke")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("bookmarks/get"));
    }

    @Test
    @WithMockUser
    @DisplayName("특정 유저가 북마크에 등록한 개그가 없다.")
    void getAllJokeIsEmpty() throws Exception {

        List<JokeDto> result = List.of();
        when(bookmarkService.getAllJoke(any(String.class))).thenReturn(result);
        mockMvc.perform(get("/api/v1/allJoke")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value( "등록된 북마크가 없습니다."))
                .andDo(print());
    }

    @Test
    @WithMockUser
    @DisplayName("특정 유저가 특정 개그를 북마크에 등록한다.")
    void addBookmark() throws Exception {
        jokeDto = JokeDto.builder()
                .question("말과 소가 햄버거 가게를 차리면?")
                .answer("소말리아")
                .build();

        when(bookmarkService.addBookmark(any(Joke.class), any(String.class))).thenReturn( 10L);

        mockMvc.perform(post("/api/v1/bookmark")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jokeDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("북마크 등록되었습니다."))
                .andDo(print());
    }

    @Test
    @WithMockUser
    @DisplayName("특정 유저가 특정 개그를 북마크에 등록 실패한다.")
    void addBookmark_fail() throws Exception {
        jokeDto = JokeDto.builder()
                .question("말과 소가 햄버거 가게를 차리면?")
                .answer("소말리아")
                .build();

        when(bookmarkService.addBookmark(any(Joke.class), any(String.class))).thenReturn( 0L);

        mockMvc.perform(post("/api/v1/bookmark")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jokeDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("북마크 등록에 실패했습니다."))
                .andDo(print());
    }
}