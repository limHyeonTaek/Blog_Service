package com.blogProject.common.member.api;


import com.blogProject.common.member.dto.Signin;
import com.blogProject.common.member.dto.SigninResponse;
import com.blogProject.common.member.dto.Signup;
import com.blogProject.common.member.dto.model.MemberDto;
import com.blogProject.common.member.service.AuthService;
import com.blogProject.config.jwt.TokenProvider;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/auth")
@RequiredArgsConstructor
@RestController
public class AuthController {

  private final AuthService authService;
  private final TokenProvider tokenProvider;

  // 회원가입
  @PostMapping("/signup")
  public ResponseEntity<String> signup(@Valid @RequestBody Signup request) {
    MemberDto memberDto = authService.signup(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(memberDto.getEmail());
  }


  // 로그인
  @PostMapping("/signin")
  public ResponseEntity<SigninResponse> signin(@Valid @RequestBody Signin request) {
    MemberDto memberDto = authService.signin(request);
    String token = tokenProvider.generateToken(memberDto);

    return ResponseEntity.ok(new SigninResponse(token, memberDto.getEmail()));
  }

}
