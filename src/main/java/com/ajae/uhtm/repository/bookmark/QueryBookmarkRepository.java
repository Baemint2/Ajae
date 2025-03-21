package com.ajae.uhtm.repository.bookmark;

import com.ajae.uhtm.domain.joke.Joke;

import java.util.List;

public interface QueryBookmarkRepository {

    List<Joke> getBookmarks(Long userId);

    // 해당 개그가 이미 북마크에 포함되어 있는지
    Boolean checkBookmark(Long userId, Long jokeId);

    Long getBookmark(Long userId, Long jokeId);

    Long countBookmark(long userId);

}
