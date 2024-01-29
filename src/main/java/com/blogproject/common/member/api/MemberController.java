package com.blogProject.common.member.api;

import com.blogProject.common.member.dto.SignUp;
import com.blogProject.common.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {

  private final MemberService memberService;

  // 회원가입 기능
  @PostMapping("/signup")
  public ResponseEntity<?> register(@RequestBody SignUp signUp) {
    SignUp newSignup = memberService.register(signUp);
    return new ResponseEntity<>(newSignup, HttpStatus.OK);
  }

}
