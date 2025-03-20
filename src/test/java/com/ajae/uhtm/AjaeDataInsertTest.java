package com.ajae.uhtm;

import com.ajae.uhtm.service.JokeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class AjaeDataInsertTest {

    @Autowired
    private JokeService jokeService;
}
