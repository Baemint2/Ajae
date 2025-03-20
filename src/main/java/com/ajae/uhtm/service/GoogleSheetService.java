package com.ajae.uhtm.service;

import com.ajae.uhtm.domain.joke.Joke;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class GoogleSheetService {

    private static final String APPLICATION_NAME = "My Google Sheets App";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    @Value("${google.spread-sheet.id}")
    String spreadsheetId;

    @Value("${google.spread-sheet.key}")
    String spreadSheetKey;

    private Sheets sheetsService;

    private final JokeService jokeService;

    public GoogleSheetService(JokeService jokeService) {
        this.jokeService = jokeService;
    }

    @PostConstruct
    public void init() throws GeneralSecurityException, IOException {
        sheetsService = getSheetsService();
    }

    private Sheets getSheetsService() throws GeneralSecurityException, IOException {

        FileInputStream inputStream = new FileInputStream(spreadSheetKey);
        GoogleCredentials credentials = GoogleCredentials.fromStream(inputStream)
                .createScoped(List.of("https://www.googleapis.com/auth/spreadsheets.readonly"));


        return new Sheets.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                new HttpCredentialsAdapter(credentials))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public List<Joke> readSheet() throws IOException {
        List<Joke> jokeResponse = new ArrayList<>();
        String range = "시트1!A1:B";
        ValueRange response = sheetsService.spreadsheets().values()
                .get(spreadsheetId, range)
                .execute();
        List<List<Object>> values = response.getValues();

        for (List<Object> value : values) {
            log.info("{}, {} ", value.get(0).toString(), value.get(1).toString());
            jokeResponse.add(Joke.create(value.get(0).toString(), value.get(1).toString()));
        }

        return jokeService.importData(jokeResponse);
    }
}
