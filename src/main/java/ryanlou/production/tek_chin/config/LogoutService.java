package ryanlou.production.tek_chin.config;

import jakarta.servlet.ServletException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.servlet.HandlerExceptionResolver;
import ryanlou.production.tek_chin.token.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;
import ryanlou.production.tek_chin.utils.UnauthorizedException;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

  private final TokenRepository tokenRepository;

  @Autowired
  @Qualifier("handlerExceptionResolver")
  private HandlerExceptionResolver resolver;

  @Override
  public void logout(
          HttpServletRequest request,
          HttpServletResponse response,
          Authentication authentication
  ) throws RuntimeException {

    try{
      log.info("logout: {}",request);
      final String authHeader = request.getHeader("Authorization");
      final String jwt;
      response.setContentType("application/json");
      response.setCharacterEncoding("UTF-8");

      if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        log.info("Invalid Authorization header");
        throw new RuntimeException("Token 驗證錯誤，請重新登入");
      }

      jwt = authHeader.substring(7);
      var storedToken = tokenRepository.findByToken(jwt)
              .orElse(null);

      if (storedToken != null) {
        storedToken.setExpired(true);
        storedToken.setRevoked(true);
        tokenRepository.save(storedToken);
        SecurityContextHolder.clearContext();
        response.setStatus(HttpServletResponse.SC_OK);
        writeJsonResponse(response, 200, "登出成功");
        log.info("登出成功");
      } else {
        log.info("Token not found");
//      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
//      writeJsonResponse(response, 403, "Token not found");
        throw new RuntimeException("Token 驗證錯誤，請重新登入");

      }
    } catch (RuntimeException e) {
      resolver.resolveException(request, response, null, new UnauthorizedException(e.getMessage()));
    }

  }

  private void writeJsonResponse(HttpServletResponse response, int status, String message) {
    try {
      String jsonResponse = String.format("{\"status\": %d, \"message\": \"%s\"}", status, message);
      response.getWriter().write(jsonResponse);
    } catch (IOException e) {
      e.printStackTrace();  // In a real application, consider proper logging.
    }
  }
}
