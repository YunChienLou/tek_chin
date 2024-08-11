package ryanlou.production.tek_chin.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ryanlou.production.tek_chin.post.Post;
import ryanlou.production.tek_chin.post.PostRepository;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
//@CrossOrigin(value = "http://localhost:3000/" , allowCredentials = "true")
@RequestMapping("/api/v1/post")
public class PostController {

    @Autowired
    private PostRepository postRepository;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createPost(
            @RequestParam("title") String title,
            @RequestParam("paragraph") String paragraph,
            @RequestParam("enable") Boolean enable,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) throws IOException {
        byte[] byteArr = new byte[0];
        if (file != null && !file.isEmpty()) {
            byteArr = file.getBytes();
        }

        var post = Post.builder()
                .title(title)
                .paragraph(paragraph)
                .enable(enable)
                .imageData(byteArr)
                .updateDate(new Date())
                .build();

        var result = postRepository.save(post);
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/uploadImage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImage(
            @RequestParam("postId") Integer postId,
            @RequestParam("file") MultipartFile file
    ) throws IOException {

        Optional<Post> oldPostOptional = postRepository.findById(postId);
        if (oldPostOptional.isPresent()) {

            byte[] byteArr = new byte[0];
            if (file != null && !file.isEmpty()) {
                byteArr = file.getBytes();
            }
            // Post exists, update its data
            Post newPost = oldPostOptional.get();
            newPost.setImageData(byteArr);
            newPost.setUpdateDate(new Date());
            // Update other fields as needed

            // Save the updated post back to the database
            postRepository.save(newPost);

            return ResponseEntity.ok().body(newPost);
        } else {
            // Post not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post not found");
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/delete/{postId}")
    public ResponseEntity<?> deletePost(
            @PathVariable Integer postId
    ) {
        // Check if the post with the given postId exists
        if (postRepository.existsById(postId)) {
            // Post exists, delete it
            postRepository.deleteById(postId);
            return ResponseEntity.ok().body("Post deleted successfully");
        } else {
            // Post not found
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value ="/updatePost", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updatePost(
            @RequestParam("title") String title,
            @RequestParam("paragraph") String paragraph,
            @RequestParam("enable") Boolean enable,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam("postId") Integer postId
    ) throws IOException {
        Optional<Post> oldPostOptional = postRepository.findById(postId);
        if (oldPostOptional.isPresent()) {

            byte[] byteArr = new byte[0];
            if (file != null && !file.isEmpty()) {
                byteArr = file.getBytes();
            }
            // Post exists, update its data
            Post newPost = oldPostOptional.get();
            newPost.setTitle(title);
            newPost.setParagraph(paragraph);
            newPost.setEnable(enable);
            newPost.setImageData(byteArr);
            newPost.setUpdateDate(new Date());
            // Update other fields as needed

            // Save the updated post back to the database
            postRepository.save(newPost);

            return ResponseEntity.ok().body(newPost);
        } else {
            // Post not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post not found");
        }
    }

    @GetMapping("/query")
    public ResponseEntity<?> queryPost(
            //查詢條件 Filtering
            @RequestParam(required = false) Boolean enable,
            @RequestParam(required = false) String search,
            //updateDate排序 Sorting
            @RequestParam(defaultValue = "desc") String sort
    ) {
        List<Post> posts = postRepository.customQuery(search , enable ,sort);
        return ResponseEntity.ok().body(posts);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getPost/{postId}")
    public ResponseEntity<?> getPost(@PathVariable Integer postId){
        Optional<Post> post = postRepository.findById(postId);
        if (post.isPresent()){
            return ResponseEntity.status(HttpStatus.OK).body(post);
        }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post not found");
        }
    }
}
