package ajae.uhtm;

import ajae.uhtm.controller.JokeController;
import ajae.uhtm.dto.JokeDto;
import ajae.uhtm.entity.Joke;
import ajae.uhtm.service.JokeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@Slf4j
@WebMvcTest(JokeController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
class JokeControllerWebMvcTest {

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JokeService jokeService;

    @RegisterExtension
    final RestDocumentationExtension restDocumentation = new RestDocumentationExtension("build/generated-snippets/rest-docs-test-controller-test");

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

    @Test
    void RestDocsTest() throws Exception {
        // given: mock으로 고정된 값을 반환하게 설정
        JokeDto fixedJoke = new Joke("딸기가 직장을 잃으면?", "딸기 시럽").toDto();
        when(jokeService.getRandomJoke()).thenReturn(fixedJoke);  // getJoke() 호출 시 고정된 joke 객체를 반환

        // when & then
        mockMvc.perform(get("/api/v1/joke"))
                .andExpect(jsonPath("$.question").value(fixedJoke.getQuestion()))  // 고정된 값으로 검증
                .andExpect(jsonPath("$.answer").value(fixedJoke.getAnswer()))  // 고정된 값으로 검증
                .andDo(print())
                .andDo(document("jokes/get",
                        responseFields(
                                fieldWithPath("question").description("문제"),
                                fieldWithPath("answer").description("정답"))
                ));
    }

    @Test
    void 개그_등록() throws Exception {
        JokeDto request = new JokeDto("말과 소가 햄버거 가게를 차리면?", "소말리아");
        Long jokeId = 1L;
        when(jokeService.saveJoke(any(JokeDto.class))).thenReturn(jokeId);
        mockMvc.perform(post("/api/v1/joke")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(jsonPath("$.message").value("문제 등록이 완료되었습니다."))
                .andDo(print())
                .andDo(document("jokes/post"));
    }
}
