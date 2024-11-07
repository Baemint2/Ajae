package ajae.uhtm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;


@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CheckDiscordCommand {


    public static List<String> checkCommand(MessageReceivedEvent event, String[] messageArray) {
        List<String> result = new ArrayList<>();

        if (messageArray[0].contains("아재") || messageArray[0].contains("ㅇㅈ")) {
            result.add(event.getAuthor().getName() + "님 주문하신 아재개그입니다!");
            result.add(checkBodyCommandForAPICall());
            return result;
        } else {
            result.add(event.getAuthor().getName() + "님 해당 명령어는 존재하지 않습니다 ㅠㅠ");
            result.add("아재개그를 원하시면 아재 또는 ㅇㅈ를 입력해주세요");
            return result;
        }
    }

    private static String checkBodyCommandForAPICall() {
        Map<String, String> commandOption = new HashMap<>();

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange("http://localhost:8080/api/v1/joke", HttpMethod.GET, HttpEntity.EMPTY, String.class);
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            String question = jsonNode.get("question").asText();
            String answer = jsonNode.get("answer").asText();

            log.info("응답: {}", String.format("%s? %s", question, answer));
            return question + "? " + answer;
        } catch (IOException e) {
            log.error("JSON 파싱 오류", e);
            return "문제가 발생했습니다. 나중에 다시 시도해 주세요.";
        }
    }


    private static String checkBodyCommand(MessageReceivedEvent event, String[] messageArray) {
        User user = event.getAuthor();
        String result;
        result = "아재개그테스트";
        switch (messageArray[1]) {
            case "아재개그테스트":
                result = checkBodyCommandForAPICall();
                break;
        }
        return result;
    }

    private static void checkCommandOption(Map<String, String> commandOptionMap, String commandOption) {

        String[] commandOptionArray = commandOption.split("=");

        if (commandOptionArray.length >= 2) {
            for (int index = 0; index < (commandOptionArray.length - 1); index++) {
                String[] searchTypeArray = commandOptionArray[index].split("-");
                String searchType = null;

                if (searchTypeArray.length > 0) {
                    searchType = searchTypeArray[1];
                }

                if (searchType != null) {
                    commandOptionMap.put(searchType, commandOptionArray[index + 1]);
                } else {
                    commandOptionMap.put(commandOptionArray[index], commandOptionArray[index + 1]);
                }
            }
        }
    }
}