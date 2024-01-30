package com.blogProject.common.member.api;


import com.blogProject.common.member.dto.Signin;
import com.blogProject.common.member.dto.Signup;
import com.blogProject.common.member.dto.model.MemberDto;
import com.blogProject.common.member.service.AuthService;
import com.blogProject.config.jwt.TokenProvider;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/member")
@RequiredArgsConstructor
@RestController
public class AuthController {

  private final AuthService authService;
  private final TokenProvider tokenProvider;

  // 회원가입
  @PostMapping("/signup")
  public ResponseEntity<MemberDto> signup(@RequestBody @Valid Signup request) {
    return ResponseEntity.ok(authService.signup(request));
  }

  // 로그인
  @PostMapping("/signin")
  public ResponseEntity<MemberDto> signin(@RequestBody @Valid Signin request) {
    MemberDto memberDto = authService.signin(request);
    String token = tokenProvider.generateToken(memberDto);

    HttpHeaders headers = new HttpHeaders();
    headers.set(HttpHeaders.AUTHORIZATION, token);

    return ResponseEntity.ok()
        .headers(headers)
        .body(memberDto);
  }
}
