package ryanlou.production.tek_chin.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerExceptionResolver;
import ryanlou.production.tek_chin.token.Token;
import ryanlou.production.tek_chin.token.TokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import ryanlou.production.tek_chin.utils.UnauthorizedException;

import java.io.IOException;
import java.util.Arrays;


@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  @Value("${application.security.whiteListUrls}")
  private String[] whiteListUrls;

  private final JwtService jwtService;
  private final UserDetailsService userDetailsService;
  private final TokenRepository tokenRepository;

  @Autowired
  @Qualifier("handlerExceptionResolver")
  private HandlerExceptionResolver resolver;

  private final AntPathMatcher pathMatcher = new AntPathMatcher();

  protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
    String requestURI = request.getRequestURI();
    log.info("request log: {}", requestURI);

    if (Arrays.stream(whiteListUrls).anyMatch(url -> pathMatcher.match(url, requestURI))) {
      log.info("Whitelist bypass request: {}", requestURI);
      filterChain.doFilter(request, response);
      return;
    }

    final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    final String jwt;
    final String userid;

    try {
      if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        log.info("No header");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        throw new ServletException("請求未通過認證，請嘗試重新登入");
      }

      jwt = authHeader.substring(7);
      userid = jwtService.extractUsername(jwt);
      log.info("extractUsername: {}", userid);

      SecurityContext context = SecurityContextHolder.getContext();
      Authentication authentication = context.getAuthentication();
      if (userid != null && authentication == null) {
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(userid);
        var tokenOptional = tokenRepository.findByToken(jwt);

        if (tokenOptional.isPresent()) {
          Token tokenEntity = tokenOptional.get();
          boolean isTokenValid = !tokenEntity.isRevoked() && !tokenEntity.isExpired();
          log.info("isTokenValid: {}", isTokenValid);

          if (isTokenValid) {
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            context.setAuthentication(authToken);
            SecurityContextHolder.setContext(context);
          }
        }
      }

      filterChain.doFilter(request, response);
    } catch (Exception e) {
      log.error("An error occurred: ", e);
      resolver.resolveException(request, response, null, new UnauthorizedException(e.getMessage()));
    }
  }
}
