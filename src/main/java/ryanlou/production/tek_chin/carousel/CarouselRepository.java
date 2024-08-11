package ryanlou.production.tek_chin.carousel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarouselRepository extends JpaRepository<Carousel, Integer> {
    List<Carousel> findByEnable(boolean enable);

    @Query("SELECT e FROM Carousel e WHERE " +
            " (:enable IS NULL OR e.enable = :enable)")
    List<Carousel> customQuery(
            @Param("enable") Boolean enable
    );
}
