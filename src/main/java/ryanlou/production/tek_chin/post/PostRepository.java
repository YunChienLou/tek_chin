package ryanlou.production.tek_chin.post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Integer> {
    List<Post> findByEnable(boolean enable);

    @Query("SELECT e FROM Post e WHERE " +
            "(:search IS NULL OR e.title LIKE %:search% OR e.paragraph LIKE %:search%) " +
            "AND (:enable IS NULL OR e.enable = :enable) " +
            "ORDER BY " +
            "CASE WHEN :sort = 'ASC' THEN e.updateDate END ASC, " +
            "CASE WHEN :sort = 'DESC' THEN e.updateDate END DESC")
    List<Post> customQuery(
            @Param("search") String search,
            @Param("enable") Boolean enable,
            @Param("sort") String sort
    );
}
