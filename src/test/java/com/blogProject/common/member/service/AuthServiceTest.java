package com.blogProject.common.member.service;


import static com.blogProject.common.member.entity.Role.ADMIN;
import static com.blogProject.constant.MemberConstants.EMAIL;
import static com.blogProject.constant.MemberConstants.MEMBER_NAME;
import static com.blogProject.constant.MemberConstants.PASSWORD;
import static com.blogProject.constant.MemberConstants.PHONE_NUMBER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.blogProject.common.member.dto.Signin;
import com.blogProject.common.member.dto.Signup;
import com.blogProject.common.member.dto.model.MemberDto;
import com.blogProject.common.member.entity.Member;
import com.blogProject.common.member.exception.MemberException;
import com.blogProject.common.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

public class AuthServiceTest {

  @Mock
  private MemberRepository memberRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private AuthenticationManagerBuilder authenticationManagerBuilder;

  @Mock
  private AuthenticationManager authenticationManager;

  private AuthService authService;

  private Signup signup;
  private Signin signin;
  private Member member;
  private MemberDto memberDto;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    authService = new AuthService(memberRepository, passwordEncoder, authenticationManagerBuilder);

    signup = new Signup(MEMBER_NAME, EMAIL, PASSWORD, PHONE_NUMBER, ADMIN);
    signin = new Signin(EMAIL, PASSWORD);
    member = new Member(1L, MEMBER_NAME, EMAIL, PASSWORD, PHONE_NUMBER, ADMIN, false);
    memberDto = MemberDto.fromEntity(member);

    when(authenticationManagerBuilder.getObject()).thenReturn(authenticationManager);
    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenReturn(mock(Authentication.class));
  }

  @Test
  @DisplayName("정상적인 회원가입 테스트")
  public void testSignup() {
    // given
    when(memberRepository.existsByEmail(signup.getEmail())).thenReturn(false);
    when(memberRepository.existsByName(signup.getName())).thenReturn(false);
    when(memberRepository.save(any(Member.class))).thenReturn(member);

    // when
    MemberDto result = authService.signup(signup);

    // then
    verify(memberRepository, times(1)).save(any(Member.class));
    assertEquals(member.getId(), result.getMemberId());
    assertEquals(member.getName(), result.getName());
    assertEquals(member.getEmail(), result.getEmail());
    assertEquals(member.getPhoneNumber(), result.getPhoneNumber());
    assertEquals(member.getRole(), result.getRole());
  }

  @Test
  @DisplayName("이메일 중복 발생 테스트")
  public void testSignup_WithExistingEmail_ShouldThrowException() {
    // given
    when(memberRepository.existsByEmail(signup.getEmail())).thenReturn(true);

    // when & then
    assertThrows(MemberException.class, () -> {
      authService.signup(signup);
    });
    verify(memberRepository, never()).save(any(Member.class));
  }

  @Test
  @DisplayName("이름 중복 발생 테스트")
  public void testSignup_WithExistingName_ShouldThrowException() {
    // given
    when(memberRepository.existsByName(signup.getName())).thenReturn(true);

    // when & then
    assertThrows(MemberException.class, () -> {
      authService.signup(signup);
    });
    verify(memberRepository, never()).save(any(Member.class));
  }

  @Test
  @DisplayName("정상적인 로그인 테스트")
  public void testSignin() {
    // given
    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenReturn(new TestingAuthenticationToken(memberDto, null));

    // when
    MemberDto result = authService.signin(signin);

    // then
    verify(authenticationManager, times(1)).authenticate(
        any(UsernamePasswordAuthenticationToken.class));
    assertEquals(memberDto, result);
  }

  @Test
  @DisplayName("로그인 실패 - UsernameNotFoundException 테스트")
  public void testSignin_WithInvalidUsername_ShouldThrowException() {
    // given
    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenThrow(new UsernameNotFoundException("User not found"));

    // when & then
    assertThrows(MemberException.class, () -> {
      authService.signin(signin);
    });
  }

  @Test
  @DisplayName("로그인 실패 - BadCredentialsException 테스트")
  public void testSignin_WithInvalidPassword_ShouldThrowException() {
    // given
    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenThrow(new BadCredentialsException("Invalid credentials"));

    // when & then
    assertThrows(MemberException.class, () -> {
      authService.signin(signin);
    });
  }
}




