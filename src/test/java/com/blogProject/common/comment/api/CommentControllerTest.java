package com.blogProject.common.comment.api;

import static com.blogProject.constant.CommentConstants.COMMENT_CONTENT;
import static com.blogProject.constant.MemberConstants.EMAIL;
import static com.blogProject.exception.ErrorCode.MEMBER_NOT_FOUND;
import static com.blogProject.exception.ErrorCode.POST_NOT_FOUND;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.blogProject.common.comment.dto.model.CommentDto;
import com.blogProject.common.comment.dto.model.ReplyDto;
import com.blogProject.common.comment.service.CommentService;
import com.blogProject.common.member.exception.MemberException;
import com.blogProject.common.post.exception.PostException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class CommentControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private CommentService commentService;

  private CommentDto commentDto;
  private ReplyDto replyDto;

  private static final Long COMMENT_ID = 2L;
  private static final Long POST_ID = 1L;

  @BeforeEach
  void setUp() {
    replyDto = ReplyDto.builder()
        .commentId(COMMENT_ID)
        .postId(POST_ID)
        .member(EMAIL)
        .comments(COMMENT_CONTENT)
        .parentId(1L)
        .build();

    commentDto = CommentDto.builder()
        .commentId(COMMENT_ID)
        .postId(POST_ID)
        .member(EMAIL)
        .comments(COMMENT_CONTENT)
        .build();
  }

  @Nested
  @DisplayName("댓글 Controller 테스트")
  class CommentTest {

    @Test
    @DisplayName("댓글 작성 테스트")
    @WithMockUser(username = EMAIL, roles = "USER")
    void writeCommentTest() throws Exception {
      // given
      when(commentService.writeComment(anyLong(), any(), any())).thenReturn(commentDto);

      // when & then
      mockMvc.perform(post("/api/comments/" + POST_ID)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(COMMENT_CONTENT)))
          .andExpect(status().isOk())
          .andExpect(content().string("댓글 작성이 완료되었습니다."));
    }

    @Test
    @DisplayName("댓글 수정 테스트")
    @WithMockUser(username = EMAIL, roles = "USER")
    void updateCommentTest() throws Exception {
      // given
      when(commentService.updateComment(anyLong(), any(), any())).thenReturn(commentDto);

      // when & then
      mockMvc.perform(put("/api/comments/" + COMMENT_ID)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(COMMENT_CONTENT)))
          .andExpect(status().isOk())
          .andExpect(content().string("수정이 완료 되었습니다."));
    }

    @Test
    @DisplayName("댓글 삭제 테스트")
    @WithMockUser(username = EMAIL, roles = "USER")
    void deleteCommentTest() throws Exception {
      // given
      doNothing().when(commentService).deleteComment(anyLong(), any());

      // when & then
      mockMvc.perform(delete("/api/comments/" + COMMENT_ID)
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(content().string("댓글 삭제가 완료되었습니다."));
    }

    /**
     * 테스트 시에 500 에러가 나는데 해결하지 못했습니다. POSTMAN 형식으로 API 테스트를 진행할 때는 문제가 없는데, 테스트코드에서 500 에러가 남(페이징 썻을
     * 때만)
     */
    @Test
    @DisplayName("댓글 조회 테스트")
    @WithMockUser(username = EMAIL, roles = "USER")
    void getCommentsTest() throws Exception {
      // given
      Page<ReplyDto> commentDtoPage = new PageImpl<>(List.of(replyDto));
      when(commentService.getComments(anyLong(), any())).thenReturn(commentDtoPage);

      // when & then
      mockMvc.perform(get("/api/comments/" + POST_ID)
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.content[0].email", is(EMAIL)))
          .andExpect(jsonPath("$.content[0].comments", is(COMMENT_CONTENT)));
    }
  }

  @Nested
  @DisplayName("댓글 Controller Exception 테스트")
  class CommentExceptionTest {

    @Test
    @DisplayName("댓글 작성 실패 테스트 - 회원이 존재하지 않음")
    @WithMockUser(username = EMAIL, roles = "USER")
    void writeCommentMemberNotFoundTest() throws Exception {
      // given
      when(commentService.writeComment(anyLong(), any(), any())).thenThrow(
          new MemberException(MEMBER_NOT_FOUND));

      // when & then
      mockMvc.perform(post("/api/comments/" + POST_ID)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(COMMENT_CONTENT)))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.errorCode").value(MEMBER_NOT_FOUND.toString()))
          .andExpect(jsonPath("$.message").value(MEMBER_NOT_FOUND.getMessage()));
    }

    @Test
    @DisplayName("댓글 수정 실패 테스트 - 회원이 존재하지 않음")
    @WithMockUser(username = EMAIL, roles = "USER")
    void updateCommentMemberNotFoundTest() throws Exception {
      // given
      when(commentService.updateComment(anyLong(), any(), any())).thenThrow(
          new MemberException(MEMBER_NOT_FOUND));

      // when & then
      mockMvc.perform(put("/api/comments/" + COMMENT_ID)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(COMMENT_CONTENT)))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.errorCode").value(MEMBER_NOT_FOUND.toString()))
          .andExpect(jsonPath("$.message").value(MEMBER_NOT_FOUND.getMessage()));
    }

    @Test
    @DisplayName("댓글 삭제 실패 테스트 - 회원이 존재하지 않음")
    @WithMockUser(username = EMAIL, roles = "USER")
    void deleteCommentMemberNotFoundTest() throws Exception {
      // given
      doThrow(new MemberException(MEMBER_NOT_FOUND)).when(commentService)
          .deleteComment(anyLong(), any());

      // when & then
      mockMvc.perform(delete("/api/comments/" + COMMENT_ID)
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.errorCode").value(MEMBER_NOT_FOUND.toString()))
          .andExpect(jsonPath("$.message").value(MEMBER_NOT_FOUND.getMessage()));
    }

    @Test
    @DisplayName("댓글 조회 실패 테스트 - 게시글이 존재하지 않음")
    @WithMockUser(username = EMAIL, roles = "USER")
    void getCommentsPostNotFoundTest() throws Exception {
      // given
      when(commentService.getComments(anyLong(), any())).thenThrow(
          new PostException(POST_NOT_FOUND));

      // when & then
      mockMvc.perform(get("/api/comments/" + POST_ID)
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.errorCode").value(POST_NOT_FOUND.toString()))
          .andExpect(jsonPath("$.message").value(POST_NOT_FOUND.getMessage()));
    }

  }
}
