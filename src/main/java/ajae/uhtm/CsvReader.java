package ajae.uhtm;

import ajae.uhtm.entity.Joke;
import ajae.uhtm.entity.JokeType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class CsvReader {
    public List<Joke> readCsv(String filePath) {
        List<Joke> dataList = new ArrayList<>();
        log.info("dataList: {}", dataList);

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length < 2) {
                    continue;
                }
                Joke entity = Joke.builder()
                        .question(values[0])
                        .answer(values[1])
                        .jokeType(JokeType.DEFAULT)
                        .build();
                dataList.add(entity);
            }
        } catch (IOException e) {
            log.error("에러 확인: ", e);
        }

        return dataList;
    }
}
