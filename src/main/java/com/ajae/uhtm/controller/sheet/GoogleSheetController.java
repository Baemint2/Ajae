package com.ajae.uhtm.controller.sheet;

import com.ajae.uhtm.domain.joke.Joke;
import com.ajae.uhtm.service.GoogleSheetService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/sheet")
public class GoogleSheetController {

    private final GoogleSheetService googleSheetService;

    @GetMapping("/read")
    public List<Joke> readSheet() throws IOException {
        return googleSheetService.readSheet();
    }
}
