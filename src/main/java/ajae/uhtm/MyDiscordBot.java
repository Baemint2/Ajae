package ajae.uhtm;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;


import java.awt.*;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class MyDiscordBot extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;  // 봇의 메시지는 무시
        }

        Message message = event.getMessage();

        List<String> resultList = CheckDiscordCommand.checkCommand(event, message.getContentDisplay().split(" "));
        log.info("resultList: {} ", resultList.toString());
        if (resultList.isEmpty()) {
            log.info("처리 결과 값 공백");
        }

        createSendMessage(event, resultList.get(0), Objects.requireNonNull(resultList.get(1)));
    }

    private void createSendMessage (MessageReceivedEvent event, String returnMessage, String returnEmbedMessage) {
        int discordAllowMessageSize = 1950;
        int resultMessageSize = returnEmbedMessage.length();

        EmbedBuilder embed = new EmbedBuilder();

        if (returnEmbedMessage.equals("명령어를 확인해 주세요.")) {
            embed.setColor(Color.RED);
        }

        embed.setDescription(returnEmbedMessage);
        sendMessage(event, returnMessage, embed);

        embed.clear();
    }

    private void sendMessage(MessageReceivedEvent event, String returnMessage, EmbedBuilder embed) {
        TextChannel textChannel = event.getChannel().asTextChannel();
        textChannel.sendMessage(returnMessage).setEmbeds(embed.build()).queue();
    }
}
