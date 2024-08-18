package ryanlou.production.tek_chin;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EntityScan(basePackages = "ryanlou.production.tek_chin")
@EnableJpaRepositories(basePackages = "ryanlou.production.tek_chin")
@SpringBootApplication(scanBasePackages = "ryanlou.production.tek_chin")
public class TekChinApplication {

    public static void main(String[] args) {
        SpringApplication.run(TekChinApplication.class, args);
    }

//    @Bean
//    public CommandLineRunner commandLineRunner(
//            AuthenticationService service
//    ) {
//        return args -> {
//            var admin = RegisterRequest.builder()
//                    .firstname("Admin")
//                    .lastname("Admin")
//                    .email("admin@mail.com")
//                    .password("password")
//                    .role(ADMIN)
//                    .build();
//            System.out.println("Admin token: " + service.register(admin).getAccessToken());
//
//            var manager = RegisterRequest.builder()
//                    .firstname("Manager")
//                    .lastname("Manager")
//                    .email("manager@mail.com")
//                    .password("password")
//                    .role(MANAGER)
//                    .build();
//            System.out.println("Manager token: " + service.register(manager).getAccessToken());
//
//
//            var post1 = Post.builder()
//                    .title("配送方式")
//                    .paragraph(
//                            "1.自取\n" +
//                            "2.本公司外務員配送\n" +
//                            "3.lalamove"
//                    )
//                    .enable(true)
//                    .updateDate(new Date())
//                    .build();
//
//            var post2 = Post.builder()
//                    .title("線上詢價")
//                    .paragraph(
//                            "1.電話: 02-2515-3105\n" + "2.Line : @TCT.COM"
//                    )
//                    .enable(true)
//                    .updateDate(new Date())
//                    .build();
//
//
//            var post3 = Post.builder()
//                    .title("聯絡我們")
//                    .paragraph(
//                            "地址:中山區建國北路三段113巷31弄15號1樓, Taipei, Taiwan\n" +
//                                    "電話: 02-2515-3105\n" +
//                                    "營業時間: 週一至週五 08:30-19:00"
//                    )
//                    .enable(true)
//                    .updateDate(new Date())
//                    .build();
//
//            postRepository.save(post1);
//            postRepository.save(post2);
//            postRepository.save(post3);
//
//            byte[] byteArray = Files.readAllBytes(Paths.get("src/main/resources/static/img.png"));
//
//            var carousel = Carousel.builder()
//                    .enable(true)
//                    .imageData(byteArray)
//                    .build();
//
//            carouselRepository.save(carousel);
//        };
//    }

}
