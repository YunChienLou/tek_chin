package ryanlou.production.tek_chin.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ryanlou.production.tek_chin.carousel.Carousel;
import ryanlou.production.tek_chin.carousel.CarouselRepository;
import ryanlou.production.tek_chin.post.Post;
import ryanlou.production.tek_chin.post.PostRepository;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/content")
public class ContentController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CarouselRepository carouselRepository;

//    @GetMapping("set-cookie")
//    public ResponseEntity<String> setCookie(HttpServletResponse response) {
//
//        List<Post> posts = postRepository.findByEnable(true);
//
//        List<Carousel> carousel = carouselRepository.findByEnable(true);
//
//        // Serialize the list of posts to a JSON string
//        ObjectMapper objectMapper = new ObjectMapper();
//        String postsJson;
//        try {
//            postsJson = objectMapper.writeValueAsString(posts);
//        } catch (JsonProcessingException e) {
//            // Handle JSON serialization error
//            return ResponseEntity.badRequest().body("Error processing posts");
//        }
//
//        // Encode the JSON string using URLEncoder
//        String encodedValue;
//        try {
//            encodedValue = URLEncoder.encode(postsJson, "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            // Handle encoding error
//            return ResponseEntity.badRequest().body("Error encoding cookie value");
//        }
//
//        // 生成不可修改的 Cookie
//        Cookie cookie = new Cookie("tek_chin_content", encodedValue);
//        cookie.setHttpOnly(true); // 僅限 HTTP 訪問，JavaScript 無法修改
//        cookie.setSecure(true); // 只在 HTTPS 連線時發送 Cookie
//        cookie.setPath("/"); // 設置 Cookie 的路徑
//        cookie.setMaxAge(3600); // Cookie 有效期（秒）
//
//        // 將 Cookie 添加到響應中
//        response.addCookie(cookie);
//
//        // 回應 API 呼叫成功
//        return ResponseEntity.ok().body("Cookie 設置成功！");
//    }

    @GetMapping("set-cookie")
    public ResponseEntity<String> setCookie(HttpServletResponse response) {

        List<Post> posts = postRepository.findByEnable(true);
        List<Carousel> carousels = carouselRepository.findByEnable(true);

        // Create a JSON object to hold both posts and carousels
        Map<String, Object> cookieData = new HashMap<>();
        cookieData.put("posts", posts);
        cookieData.put("carousels", carousels);

        // Serialize the list of posts to a JSON string
        ObjectMapper objectMapper = new ObjectMapper();
        String postsJson;
        try {
            postsJson = objectMapper.writeValueAsString(cookieData);
        } catch (JsonProcessingException e) {
            // Handle JSON serialization error
            return ResponseEntity.badRequest().body("Error processing posts");
        }

        // Encode the JSON string using URLEncoder
        String encodedValue;
        try {
            encodedValue = URLEncoder.encode(postsJson, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // Handle encoding error
            return ResponseEntity.badRequest().body("Error encoding cookie value");
        }

        // 生成不可修改的 Cookie
        Cookie cookie = new Cookie("tek_chin_content", encodedValue);
        cookie.setHttpOnly(true); // 僅限 HTTP 訪問，JavaScript 無法修改
        cookie.setSecure(true); // 只在 HTTPS 連線時發送 Cookie
        cookie.setPath("/"); // 設置 Cookie 的路徑
        cookie.setMaxAge(3600); // Cookie 有效期（秒）

        // 將 Cookie 添加到響應中
        response.addCookie(cookie);

        // 回應 API 呼叫成功
        return ResponseEntity.ok().body("Cookie 設置成功！");
    }
}
