package ryanlou.production.tek_chin.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        log.info("Configuring CORS settings");

        registry.addMapping("/**")
                .allowedOrigins("https://18.181.211.6", "https://teck-chin.com.tw", "http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);

        log.info("CORS settings applied for /api/**, allowed origins: https://18.181.211.6, https://teck-chin.com.tw, http://localhost:3000");
    }
}
