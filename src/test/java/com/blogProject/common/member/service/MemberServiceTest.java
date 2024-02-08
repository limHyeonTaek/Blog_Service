package com.blogProject.common.member.service;

import static com.blogProject.common.member.entity.Role.ADMIN;
import static com.blogProject.constant.MemberConstants.EMAIL;
import static com.blogProject.constant.MemberConstants.MEMBER_NAME;
import static com.blogProject.constant.MemberConstants.MEMBER_NAME_UPDATE;
import static com.blogProject.constant.MemberConstants.PASSWORD;
import static com.blogProject.constant.MemberConstants.PHONE_NUMBER;
import static com.blogProject.constant.MemberConstants.PHONE_NUMBER_UPDATE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.blogProject.common.member.dto.UpdateMember;
import com.blogProject.common.member.dto.model.MemberDto;
import com.blogProject.common.member.entity.Member;
import com.blogProject.common.member.exception.MemberException;
import com.blogProject.common.member.repository.MemberRepository;
import com.blogProject.exception.GlobalException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;

@SpringBootTest
@WithMockUser(username = EMAIL, roles = "ADMIN")
public class MemberServiceTest {

  @Autowired
  private MemberService memberService;

  @MockBean
  private MemberRepository memberRepository;

  @Mock
  private Authentication authentication;

  private Member member;

  @BeforeEach
  public void setUp() {
    member = Member.builder()
        .id(1L)
        .email(EMAIL)
        .name(MEMBER_NAME)
        .password(PASSWORD)
        .phoneNumber(PHONE_NUMBER)
        .role(ADMIN)
        .isDeleted(false)
        .build();

    when(authentication.getName()).thenReturn(member.getEmail());
    when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
    when(authentication.getName()).thenReturn(member.getEmail());
  }

  @Nested
  @DisplayName("회원 Service 테스트")
  class MemberTest {

    @Test
    @DisplayName("회원 정보 수정")
    public void updateMemberTest() {
      // given
      UpdateMember request = new UpdateMember(MEMBER_NAME_UPDATE, PHONE_NUMBER_UPDATE);

      when(memberRepository.save(any(Member.class))).thenReturn(member);

      // when
      MemberDto result = memberService.updateMember(1L, request);

      // then
      assertEquals(MEMBER_NAME_UPDATE, result.getName());
      assertEquals(PHONE_NUMBER_UPDATE, result.getPhoneNumber());
    }

    @Test
    @DisplayName("회원 계정 삭제")
    public void deleteMemberTest() {
      // given
      doNothing().when(memberRepository).delete(any(Member.class));

      // when
      memberService.deleteMember(1L, authentication);

      // then
      assertTrue(member.isDeleted());
    }
  }

  @Nested
  @DisplayName("회원 Service Exception 테스트")
  class MemberExceptionTest {

    @Test
    @DisplayName("회원 정보 수정 - 회원이 존재하지 않을 때")
    public void updateMemberTest_MemberNotFound() {
      // given
      UpdateMember request = new UpdateMember(MEMBER_NAME_UPDATE, PHONE_NUMBER_UPDATE);
      when(memberRepository.findById(anyLong())).thenReturn(Optional.empty());

      // when & then
      assertThrows(MemberException.class, () -> {
        memberService.updateMember(1L, request);
      });
    }

    @Test
    @DisplayName("회원 계정 삭제 - 회원이 존재하지 않을 때")
    public void deleteMemberTest_MemberNotFound() {
      // given
      when(memberRepository.findById(anyLong())).thenReturn(Optional.empty());

      // when & then
      assertThrows(MemberException.class, () -> {
        memberService.deleteMember(1L, authentication);
      });
    }

    @Test
    @DisplayName("회원 계정 삭제 - 다른 회원의 계정을 삭제하려 할 때")
    public void deleteMemberTest_AccessDenied() {
      // given
      when(authentication.getName()).thenReturn("another@test.com");

      // when & then
      assertThrows(GlobalException.class, () -> {
        memberService.deleteMember(1L, authentication);
      });
    }
  }
}

