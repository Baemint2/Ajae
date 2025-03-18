package com.ajae.uhtm.repository.bookmark;

import com.ajae.uhtm.domain.bookmark.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long>, QueryBookmarkRepository {

    @Modifying
    @Query("update Bookmark b SET b.isDeleted = true where b.id = :id")
    int deleteBookmarkById(long id);

    @Modifying
    @Query("update Bookmark b SET b.isDeleted = false where b.id = :id")
    int updateBookmarkById(long id);
}
