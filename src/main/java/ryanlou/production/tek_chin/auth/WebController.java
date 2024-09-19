package ryanlou.production.tek_chin.auth;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WebController {

//    @RequestMapping("/{path:^(?!api|static).*$}/**")
//    public String forward() {
//        // Forward to index.html for any route except those starting with "api" or "static"
//        return "index.html";
//    }

    @RequestMapping(value = "/{path:[^\\.]*}")
    public String forward() {
        // Forward all routes to index.html
        return "forward:/index.html";
    }
}
