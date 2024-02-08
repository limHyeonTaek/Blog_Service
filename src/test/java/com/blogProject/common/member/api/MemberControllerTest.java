package com.blogProject.common.member.api;

import static com.blogProject.constant.MemberConstants.EMAIL;
import static com.blogProject.constant.MemberConstants.MEMBER_NAME_UPDATE;
import static com.blogProject.constant.MemberConstants.PHONE_NUMBER_UPDATE;
import static com.blogProject.exception.ErrorCode.MEMBER_NOT_FOUND;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.blogProject.common.member.dto.UpdateMember;
import com.blogProject.common.member.dto.model.MemberDto;
import com.blogProject.common.member.exception.MemberException;
import com.blogProject.common.member.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class MemberControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private MemberService memberService;

  private static final Long MEMBER_ID = 1L;

  @Nested
  @DisplayName("회원 Controller 테스트")
  class MemberTest {

    @Test
    @DisplayName("회원 정보 수정 테스트")
    @WithMockUser(username = EMAIL, roles = "USER")
    void updateMemberTest() throws Exception {
      UpdateMember request = UpdateMember.builder()
          .name(MEMBER_NAME_UPDATE)
          .phoneNumber(PHONE_NUMBER_UPDATE)
          .build();

      when(memberService.updateMember(anyLong(), any())).thenReturn(new MemberDto());

      mockMvc.perform(put("/api/member/edit/" + MEMBER_ID)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isOk())
          .andExpect(content().string("성공적으로 수정되었습니다."));
    }

    @Test
    @DisplayName("회원 탈퇴 테스트")
    @WithMockUser(username = EMAIL, roles = "USER")
    void deleteMemberTest() throws Exception {
      doNothing().when(memberService).deleteMember(anyLong(), any());

      mockMvc.perform(delete("/api/member/" + MEMBER_ID)
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(content().string("성공적으로 삭제되었습니다."));
    }
  }

  @Nested
  @DisplayName("회원 Controller Exception 테스트")
  class MemberExceptionTest {

    @Test
    @DisplayName("회원 정보 수정 실패 테스트 - 회원이 존재하지 않음")
    @WithMockUser(username = EMAIL, roles = "USER")
    void updateMemberFailTest() throws Exception {
      UpdateMember request = UpdateMember.builder()
          .name(MEMBER_NAME_UPDATE)
          .phoneNumber(PHONE_NUMBER_UPDATE)
          .build();

      when(memberService.updateMember(anyLong(), any())).thenThrow(
          new MemberException(MEMBER_NOT_FOUND));

      mockMvc.perform(put("/api/member/edit/" + MEMBER_ID)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.errorCode").value(MEMBER_NOT_FOUND.toString()))
          .andExpect(jsonPath("$.message").value(MEMBER_NOT_FOUND.getMessage()));
    }

    @Test
    @DisplayName("회원 탈퇴 실패 테스트 - 회원이 존재하지 않음")
    @WithMockUser(username = EMAIL, roles = "USER")
    void deleteMemberFailTest() throws Exception {
      doThrow(new MemberException(MEMBER_NOT_FOUND)).when(memberService)
          .deleteMember(anyLong(), any());

      mockMvc.perform(delete("/api/member/" + MEMBER_ID)
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.errorCode").value(MEMBER_NOT_FOUND.toString()))
          .andExpect(jsonPath("$.message").value(MEMBER_NOT_FOUND.getMessage()));
    }
  }
}
