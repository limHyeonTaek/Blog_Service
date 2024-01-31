package com.blogProject.common.member.service;


import static com.blogProject.exception.ErrorCode.MEMBER_ALREADY_EXISTS;

import com.blogProject.common.member.dto.Signin;
import com.blogProject.common.member.dto.Signup;
import com.blogProject.common.member.dto.model.MemberDto;
import com.blogProject.common.member.entity.Member;
import com.blogProject.common.member.exception.MemberException;
import com.blogProject.common.member.repository.MemberRepository;
import com.blogProject.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AuthService {

  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManagerBuilder authenticationManagerBuilder;

  // 회원가입
  @Transactional
  public MemberDto signup(Signup request) {
    if (memberRepository.existsByEmail(request.getEmail())) {
      throw new MemberException(MEMBER_ALREADY_EXISTS);
    }

    Member savedMember = memberRepository.save(
        request.toEntity(passwordEncoder.encode(request.getPassword())));

    return MemberDto.fromEntity(savedMember);
  }

  // 로그인
  public MemberDto signin(Signin request) {
    try {
      Authentication authentication = authenticationManagerBuilder.getObject()
          .authenticate(new UsernamePasswordAuthenticationToken(
              request.email(), request.password()));
      return MemberDto.fromEntity((Member) authentication.getPrincipal());
    } catch (UsernameNotFoundException | BadCredentialsException e) {
      throw new MemberException(ErrorCode.INVALID_LOGIN_REQUEST);
    }
  }
}

