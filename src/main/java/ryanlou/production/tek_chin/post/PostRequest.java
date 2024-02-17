package ryanlou.production.tek_chin.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import ryanlou.production.tek_chin.user.Role;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostRequest {

    private String title;
    private String paragraph;
    private Boolean enable;
}