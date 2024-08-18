package ryanlou.production.tek_chin.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.server.ResponseStatusException;
import ryanlou.production.tek_chin.config.JwtService;
import ryanlou.production.tek_chin.token.Token;
import ryanlou.production.tek_chin.token.TokenRepository;
import ryanlou.production.tek_chin.token.TokenType;
import ryanlou.production.tek_chin.user.User;
import ryanlou.production.tek_chin.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

  @Autowired
  private UserRepository repository;

  @Autowired
  private TokenRepository tokenRepository;

  private final PasswordEncoder passwordEncoder;

  @Autowired
  private  JwtService jwtService;

  @Autowired
  private AuthenticationManager authenticationManager;

  public AuthenticationResponse register(RegisterRequest request) {
    var user = User.builder()
        .firstname(request.getFirstname())
        .lastname(request.getLastname())
        .email(request.getEmail())
        .password(passwordEncoder.encode(request.getPassword()))
        .role(request.getRole())
        .build();

    var savedUser = repository.save(user);
    var jwtToken = jwtService.generateToken(user);
    var refreshToken = jwtService.generateRefreshToken(user);
    saveUserToken(savedUser, jwtToken);
    return AuthenticationResponse.builder()
        .accessToken(jwtToken)
            .refreshToken(refreshToken)
        .build();
  }

  public AuthenticationResponse authenticate(AuthenticationRequest request) {
    try {
      authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(
                      request.getEmail(),
                      request.getPassword()
              )
      );
    } catch (BadCredentialsException e) {
      // Update user's retry count
      User user = repository.findByEmail(request.getEmail())
              .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "找不到此帳號", e));
      user.setRetry(user.getRetry() + 1);
      repository.save(user);
      log.info("帳號或是密碼錯誤");
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "密碼錯誤", e);
    } catch (AuthenticationException e) {
      log.info("帳務遭禁用，請通知管理員");
      // Handle other authentication exceptions
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "帳務遭禁用，請通知管理員", e);
    }
    User user = repository.findByEmail(request.getEmail()).orElseThrow();
    user.setRetry(0);
    var jwtToken = jwtService.generateToken(user);
    var refreshToken = jwtService.generateRefreshToken(user);
    revokeAllUserTokens(user);
    saveUserToken(user, jwtToken);
    return AuthenticationResponse.builder()
        .accessToken(jwtToken).refreshToken(refreshToken)
        .build();
  }



  private void saveUserToken(User user, String jwtToken) {
    var token = Token.builder()
        .user(user)
        .token(jwtToken)
        .tokenType(TokenType.BEARER)
        .expired(false)
        .revoked(false)
        .build();
    tokenRepository.save(token);
  }

  private void revokeAllUserTokens(User user) {
    var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
    if (validUserTokens.isEmpty())
      return;
    validUserTokens.forEach(token -> {
      token.setExpired(true);
      token.setRevoked(true);
    });
    tokenRepository.saveAll(validUserTokens);
  }

  public void refreshToken(
          HttpServletRequest request,
          HttpServletResponse response
  ) throws IOException {
    final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    final String refreshToken;
    final String userEmail;
    if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Token 格式錯誤");
    }
    refreshToken = authHeader.substring(7);
    userEmail = jwtService.extractUsername(refreshToken);
    if (userEmail != null) {
      var user = this.repository.findByEmail(userEmail)
              .orElseThrow();
      if (jwtService.isTokenValid(refreshToken, user)) {
        var accessToken = jwtService.generateToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, accessToken);
        var authResponse = AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
        new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
      }else {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "用戶帳號被鎖定");
      }
    }else {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "該用戶不存在");
    }
  }
}
