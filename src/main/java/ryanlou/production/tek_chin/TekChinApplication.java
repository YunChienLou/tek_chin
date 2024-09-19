package ryanlou.production.tek_chin;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EntityScan(basePackages = "ryanlou.production.tek_chin")
@EnableJpaRepositories(basePackages = "ryanlou.production.tek_chin")
@SpringBootApplication(scanBasePackages = "ryanlou.production.tek_chin")
public class TekChinApplication {

    public static void main(String[] args) {
        SpringApplication.run(TekChinApplication.class, args);
    }

}
