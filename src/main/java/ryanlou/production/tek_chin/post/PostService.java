package ryanlou.production.tek_chin.post;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {

    @Autowired
    private  PostRepository postRepository;
}