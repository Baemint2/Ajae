package ajae.uhtm.repository.bookmark;

import ajae.uhtm.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long>, QueryBookmarkRepository {
}
