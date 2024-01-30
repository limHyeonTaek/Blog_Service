package com.blogProject.config.jwt;

import com.blogProject.common.member.dto.model.MemberDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * JWT 발행 객체
 */
@RequiredArgsConstructor
@Component
public class TokenProvider {

  @Value("${spring.jwt.key}")
  private String key;
  private SecretKey secretKey;
  private static final long TOKEN_EXPIRE_TIME = 1000 * 60 * 60; // 1시간
  private static final String KEY_ROLES = "roles";

  @PostConstruct
  private void setSecretKey() {
    secretKey = Keys.hmacShaKeyFor(key.getBytes());
  }

  // 토큰생성
  public String generateToken(MemberDto memberDto) {
    String authorities = memberDto.getRole().getKey();

    Date now = new Date();
    Date expiredDate = new Date(now.getTime() + TOKEN_EXPIRE_TIME);

    return Jwts.builder()
        .subject(memberDto.getEmail())
        .claim(KEY_ROLES, authorities)
        .issuedAt(now)
        .expiration(expiredDate)
        .signWith(secretKey, Jwts.SIG.HS512)
        .compact();
  }

  // 토큰을 통한 유저 인증 정보 생성
  public Authentication getAuthentication(String token) {
    Claims claims = parseClaims(token);

    List<SimpleGrantedAuthority> authorities = getAuthorities(claims);

    User user = new User(claims.getSubject(), "", authorities);
    return new UsernamePasswordAuthenticationToken(user, token, authorities);
  }

  // 토큰 유효성 검증

  public boolean validateToken(String token) {
    if (StringUtils.hasText(token)) {
      Claims claims = parseClaims(token);
      return claims.getExpiration().after(new Date());
    }

    return false;
  }

  // 토큰에서 권한 정보 파싱

  private List<SimpleGrantedAuthority> getAuthorities(Claims claims) {
    return Collections.singletonList(new SimpleGrantedAuthority(
        claims.get(KEY_ROLES).toString()));
  }

  // 토큰 복호화
  private Claims parseClaims(String token) {
    try {
      return Jwts.parser().verifyWith(secretKey).build()
          .parseSignedClaims(token).getPayload();
    } catch (ExpiredJwtException e) {
      return e.getClaims();
    }
  }
}
