package ryanlou.production.tek_chin.demo;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ryanlou.production.tek_chin.carousel.Carousel;
import ryanlou.production.tek_chin.carousel.CarouselRepository;
import ryanlou.production.tek_chin.post.ImageUtil;


import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
//@CrossOrigin(value = "http://localhost:3000/" , allowCredentials = "true")
//@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/v1/carousel")
public class CarouselController {

    @Autowired
    private CarouselRepository carouselRepository;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createCarousel(
            @RequestParam("enable") Boolean enable,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        byte[] byteArr = new byte[0];
        if (file != null && !file.isEmpty()) {
            byteArr = file.getBytes();
        }

        var carousel = Carousel.builder()
                .enable(enable)
                .imageData(byteArr)
                .build();

        var result = carouselRepository.save(carousel);
        return ResponseEntity.ok(result);
    }

    @PostMapping(value = "/uploadImage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImage(
            @RequestParam("carouselId") Integer carouselId,
            @RequestParam("file") MultipartFile file
    ) throws IOException {

        Optional<Carousel> oldCarouselOptional = carouselRepository.findById(carouselId);
        if (oldCarouselOptional.isPresent()) {

            byte[] byteArr = new byte[0];
            if (file != null && !file.isEmpty()) {
                byteArr = file.getBytes();
            }
            // Post exists, update its data
            Carousel newCarousel = oldCarouselOptional.get();
            newCarousel.setImageData(ImageUtil.compressImage(byteArr));
            // Update other fields as needed

            // Save the updated post back to the database
            carouselRepository.save(newCarousel);

            return ResponseEntity.ok().body(newCarousel);
        } else {
            // Post not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Carousel not found");
        }
    }

    @PostMapping("/delete/{carouselId}")
    public ResponseEntity<?> deleteCarousel(
            @PathVariable Integer carouselId
    ) {
        // Check if the post with the given postId exists
        if (carouselRepository.existsById(carouselId)) {
            // Post exists, delete it
            carouselRepository.deleteById(carouselId);
            return ResponseEntity.ok().body("carousel deleted successfully");
        } else {
            // Post not found
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(value = "/updateCarousel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateCarousel(
            @RequestParam("enable") Boolean enable,
            @RequestParam("carouselId") Integer carouselId
    ) {
        try {
            Optional<Carousel> oldCarouselOptional = carouselRepository.findById(carouselId);
            if (oldCarouselOptional.isPresent()) {

                // Post exists, update its data
                Carousel newCarousel = oldCarouselOptional.get();
                newCarousel.setEnable(enable);
                // Update other fields as needed

                // Save the updated post back to the database
                carouselRepository.save(newCarousel);

                return ResponseEntity.ok().body(newCarousel);
            } else {
                // Post not found
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Carousel not found");
            }
        } catch (Exception e) {
            // Handle any unexpected exceptions
            log.info("An error occurred while updating the carousel: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while updating the carousel: " + e.getMessage());
        }
    }

    @GetMapping("/query")
    public ResponseEntity<?> queryCarousel(
            //查詢條件 Filtering
            @RequestParam(required = false) Boolean enable,
            //updateDate排序 Sorting
            @RequestParam(defaultValue = "desc") String sort
    ) {
        List<Carousel> carousels = carouselRepository.customQuery(enable);
        return ResponseEntity.ok().body(carousels);
    }

    @GetMapping("/getPost/{carouselId}")
    public ResponseEntity<?> getCarousel(@PathVariable Integer carouselId){
        Optional<Carousel> carousel = carouselRepository.findById(carouselId);
        if (carousel.isPresent()){
            return ResponseEntity.status(HttpStatus.OK).body(carousel);
        }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("carousel not found");
        }
    }
}
