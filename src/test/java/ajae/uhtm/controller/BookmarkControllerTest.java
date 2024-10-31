package ajae.uhtm.controller;

import ajae.uhtm.config.SecurityConfig;
import ajae.uhtm.controller.bookmark.BookmarkController;
import ajae.uhtm.dto.joke.JokeDto;
import ajae.uhtm.service.BookmarkService;
import ajae.uhtm.auth.oauth2.OAuth2UserService;
import ajae.uhtm.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @Mock
    private DefaultOAuth2UserService delegate;

    OAuth2User oAuth2User;

    JokeDto jokeDto;

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
    void checkBookmark() throws Exception {
        when(bookmarkService.checkBookmark(any(String.class), any(String.class))).thenReturn(false);
        mockMvc.perform(post("/api/v1/check")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jokeDto)))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("bookmarks/post"));
    }

    @Test
    @WithMockUser
    void getAllJoke() throws Exception {
        jokeDto = JokeDto.builder()
                .question("말과 소가 햄버거 가게를 차리면?")
                .answer("소말리아")
                .build();
        List<JokeDto> result = List.of(jokeDto);
        when(bookmarkService.getAllJoke(any(String.class))).thenReturn(result);
        mockMvc.perform(get("/api/v1/allJoke")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("bookmarks/get"));
    }
}