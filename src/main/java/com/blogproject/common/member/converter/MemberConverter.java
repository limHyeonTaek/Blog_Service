package com.blogProject.common.member.converter;

import com.blogProject.common.member.dto.SignIn;
import com.blogProject.common.member.dto.SignUp;
import com.blogProject.common.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberConverter {

  private final PasswordEncoder passwordEncoder;
  public Member dtoToEntity(SignUp signUp) {

    return Member.builder()
        .name(signUp.getName())
        .email(signUp.getEmail())
        .password(passwordEncoder.encode(signUp.getPassword()))
        .phoneNumber(signUp.getPhoneNumber())
        .build();
  }


  public SignUp entityToDto(Member member) {
    return SignUp.builder()
        .name(member.getName())
        .email(member.getEmail())
        .password(member.getPassword())
        .phoneNumber(member.getPhoneNumber())
        .build();
  }

  public Member dtoToEntity(SignIn signIn) {
    return Member.builder()
        .email(signIn.getEmail())
        .password(signIn.getPassword())
        .build();
  }

}
