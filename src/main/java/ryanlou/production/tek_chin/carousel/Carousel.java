package ryanlou.production.tek_chin.carousel;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "carousel")
public class Carousel {

    @Id
    @GeneratedValue
    private Integer id;
    private Boolean enable;

    @Lob
    @Column(name = "imagedata", length = 1000000)
    private byte[] imageData;
}
