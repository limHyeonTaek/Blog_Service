package com.blogProject.common.member.api;

import static com.blogProject.common.member.entity.Role.ADMIN;
import static com.blogProject.constant.MemberConstants.EMAIL;
import static com.blogProject.constant.MemberConstants.MEMBER_NAME;
import static com.blogProject.constant.MemberConstants.PASSWORD;
import static com.blogProject.constant.MemberConstants.PHONE_NUMBER;
import static com.blogProject.exception.ErrorCode.INVALID_LOGIN_REQUEST;
import static com.blogProject.exception.ErrorCode.MEMBER_ALREADY_EXISTS;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.blogProject.common.member.dto.Signin;
import com.blogProject.common.member.dto.Signup;
import com.blogProject.common.member.dto.model.MemberDto;
import com.blogProject.common.member.exception.MemberException;
import com.blogProject.common.member.service.AuthService;
import com.blogProject.config.jwt.TokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

  private static final String TOKEN = "token";

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private AuthService authService;

  @MockBean
  private TokenProvider tokenProvider;

  private Signup signupRequest;
  private MemberDto memberDto;

  @BeforeEach
  void setUp() {
    signupRequest = Signup.builder()
        .name(MEMBER_NAME)
        .email(EMAIL)
        .password(PASSWORD)
        .phoneNumber(PHONE_NUMBER)
        .role(ADMIN)
        .build();

    memberDto = MemberDto.builder()
        .memberId(1L)
        .name(MEMBER_NAME)
        .email(EMAIL)
        .password(PASSWORD)
        .phoneNumber(PHONE_NUMBER)
        .role(ADMIN)
        .build();
  }

  @Nested
  @DisplayName("Auth Controller 테스트")
  class AuthTest {

    @Test
    @DisplayName("정상적인 회원가입")
    void signupTest() throws Exception {
      // given
      Mockito.when(authService.signup(any())).thenReturn(memberDto);

      //when & then
      mockMvc.perform(post("/api/auth/signup")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(signupRequest)))
          .andExpect(status().isCreated())
          .andExpect(content().string(memberDto.getEmail()));
    }

    @Test
    @DisplayName("정상적인 로그인")
    void signinTest() throws Exception {
      // given
      Signin request = Signin.builder()
          .email(EMAIL)
          .password(PASSWORD)
          .build();

      Mockito.when(authService.signin(any())).thenReturn(memberDto);
      Mockito.when(tokenProvider.generateToken(any())).thenReturn(TOKEN);

      // when & then
      mockMvc.perform(post("/api/auth/signin")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.token").value(TOKEN))
          .andExpect(jsonPath("$.memberEmail").value(memberDto.getEmail()));
    }
  }

  @Nested
  @DisplayName("Auth Controller Exception 테스트")
  class AuthExceptionTest {

    @Test
    @DisplayName("이미 메일이 있는 에러")
    void signupTest_whenEmailExists() throws Exception {
      // given
      Mockito.when(authService.signup(any())).thenThrow(new MemberException(MEMBER_ALREADY_EXISTS));

      // when & then
      mockMvc.perform(post("/api/auth/signup")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(signupRequest)))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.errorCode").value(MEMBER_ALREADY_EXISTS.toString()))
          .andExpect(jsonPath("$.message").value(MEMBER_ALREADY_EXISTS.getMessage()));
    }

    @Test
    @DisplayName("잘못된 로그인 요청 에러")
    void signinTest_whenInvalidCredentials() throws Exception {
      // given
      Mockito.doThrow(new MemberException(INVALID_LOGIN_REQUEST))
          .when(authService)
          .signin(any(Signin.class));

      //when & then
      mockMvc.perform(post("/api/auth/signin")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(signupRequest)))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.errorCode").value(INVALID_LOGIN_REQUEST.toString()))
          .andExpect(jsonPath("$.message").value(INVALID_LOGIN_REQUEST.getMessage()));
    }
  }
}




