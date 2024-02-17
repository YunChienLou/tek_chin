package ryanlou.production.tek_chin.post;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "post")
public class Post {

    @Id
    @GeneratedValue
    private Integer id;
    private String title;
    private String paragraph;
    private Boolean enable;
    private Date updateDate;

    @Lob
    @Column(name = "imagedata", length = 1000)
    private byte[] imageData;
}
