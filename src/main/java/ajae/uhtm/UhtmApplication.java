package ajae.uhtm;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class UhtmApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(UhtmApplication.class, args);
        DiscordBotToken discordBotToken = context.getBean(DiscordBotToken.class);


        JDABuilder builder = JDABuilder.createDefault(discordBotToken.getDiscordBotToken());
        builder.enableIntents(GatewayIntent.MESSAGE_CONTENT);  // 메시지 콘텐츠를 활성화
        builder.addEventListeners(new MyDiscordBot());  // 이벤트 리스너 등록
        builder.build();  // 봇 시작
    }

}
