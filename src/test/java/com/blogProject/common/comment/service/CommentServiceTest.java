package com.blogProject.common.comment.service;

import static com.blogProject.common.member.entity.Role.ADMIN;
import static com.blogProject.constant.CommentConstants.COMMENT_CONTENT;
import static com.blogProject.constant.CommentConstants.UPDATE_COMMENT_COTNET;
import static com.blogProject.constant.MemberConstants.EMAIL;
import static com.blogProject.constant.MemberConstants.MEMBER_NAME;
import static com.blogProject.constant.MemberConstants.PASSWORD;
import static com.blogProject.constant.MemberConstants.PHONE_NUMBER;
import static com.blogProject.constant.PostConstants.POST_CONTENT;
import static com.blogProject.constant.PostConstants.POST_TITLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.blogProject.common.comment.dto.model.CommentDto;
import com.blogProject.common.comment.entity.Comment;
import com.blogProject.common.comment.exception.CommentException;
import com.blogProject.common.comment.repository.CommentRepository;
import com.blogProject.common.member.entity.Member;
import com.blogProject.common.member.exception.MemberException;
import com.blogProject.common.member.repository.MemberRepository;
import com.blogProject.common.post.entity.Post;
import com.blogProject.common.post.exception.PostException;
import com.blogProject.common.post.repository.PostRepository;
import com.blogProject.exception.GlobalException;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

@SpringBootTest
public class CommentServiceTest {

  @Autowired
  private CommentService commentService;

  @MockBean
  private CommentRepository commentRepository;

  @MockBean
  private MemberRepository memberRepository;

  @MockBean
  private PostRepository postRepository;

  @Mock
  private Authentication authentication;

  private Member member;
  private Post post;
  private Comment comment;

  @BeforeEach
  public void setUp() {
    member = Member.builder()
        .email(EMAIL)
        .name(MEMBER_NAME)
        .password(PASSWORD)
        .phoneNumber(PHONE_NUMBER)
        .role(ADMIN)
        .isDeleted(false)
        .build();

    post = Post.builder()
        .id(1L)
        .title(POST_TITLE)
        .contents(POST_CONTENT)
        .member(member)
        .build();

    comment = Comment.builder()
        .id(1L)
        .post(post)
        .member(member)
        .comments(COMMENT_CONTENT)
        .build();

    when(authentication.getName()).thenReturn(member.getEmail());
    when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(member));
    when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
    when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));
  }

  @Test
  @DisplayName("댓글 생성 테스트")
  public void writeCommentTest() {
    // given
    when(commentRepository.save(any(Comment.class))).thenReturn(comment);

    // when
    CommentDto result = commentService.writeComment(1L, COMMENT_CONTENT, authentication);

    // then
    assertNotNull(result);
    assertEquals(COMMENT_CONTENT, result.getComments());
  }

  @Test
  @DisplayName("댓글 수정 테스트")
  public void updateCommentTest() {
    // given
    comment.setComments(UPDATE_COMMENT_COTNET);

    // when
    CommentDto result = commentService.updateComment(1L, UPDATE_COMMENT_COTNET, authentication);

    // then
    assertNotNull(result);
    assertEquals(UPDATE_COMMENT_COTNET, result.getComments());
  }

  @Test
  @DisplayName("댓글 삭제 테스트")
  public void deleteCommentTest() {
    // given
    doNothing().when(commentRepository).delete(any(Comment.class));

    // when
    commentService.deleteComment(1L, authentication);

    // then
    verify(commentRepository, times(1)).delete(any(Comment.class));
  }

  @Test
  @DisplayName("댓글 조회 테스트")
  public void getCommentsTest() {
    // given
    Page<Comment> page = new PageImpl<>(Collections.singletonList(comment));
    when(commentRepository.findByPost(any(Post.class), any(Pageable.class))).thenReturn(page);

    // when
    Page<CommentDto> result = commentService.getComments(1L, PageRequest.of(0, 10));

    // then
    assertEquals(1, result.getContent().size());
  }

  @Test
  @DisplayName("회원이 존재하지 않을 때 댓글 작성")
  public void writeCommentTest_MemberNotFound() {
    // given
    when(memberRepository.findByEmail(anyString())).thenReturn(Optional.empty());

    // when & then
    assertThrows(MemberException.class, () -> {
      commentService.writeComment(1L, COMMENT_CONTENT, authentication);
    });
  }

  @Test
  @DisplayName("게시글이 존재하지 않을 때 댓글 작성")
  public void writeCommentTest_PostNotFound() {
    // given
    when(postRepository.findById(anyLong())).thenReturn(Optional.empty());

    // when & then
    assertThrows(PostException.class, () -> {
      commentService.writeComment(1L, COMMENT_CONTENT, authentication);
    });
  }

  @Test
  @DisplayName("댓글이 존재하지 않을 때 댓글 수정")
  public void updateCommentTest_CommentNotFound() {
    // given
    when(commentRepository.findById(anyLong())).thenReturn(Optional.empty());

    // when & then
    assertThrows(CommentException.class, () -> {
      commentService.updateComment(1L, UPDATE_COMMENT_COTNET, authentication);
    });
  }

  @Test
  @DisplayName("다른 회원의 댓글을 수정하려 할 때")
  public void updateCommentTest_AccessDenied() {
    // given
    when(authentication.getName()).thenReturn("another@test.com");

    // when & then
    assertThrows(GlobalException.class, () -> {
      commentService.updateComment(1L, UPDATE_COMMENT_COTNET, authentication);
    });
  }

  @Test
  @DisplayName("댓글이 존재하지 않을 때 댓글 삭제")
  public void deleteCommentTest_CommentNotFound() {
    // given
    when(commentRepository.findById(anyLong())).thenReturn(Optional.empty());

    // when & then
    assertThrows(CommentException.class, () -> {
      commentService.deleteComment(1L, authentication);
    });
  }

  @Test
  @DisplayName("다른 회원의 댓글을 삭제하려 할 때")
  public void deleteCommentTest_AccessDenied() {
    // given
    when(authentication.getName()).thenReturn("another@test.com");

    // when & then
    assertThrows(GlobalException.class, () -> {
      commentService.deleteComment(1L, authentication);
    });
  }

  @Test
  @DisplayName("게시글이 존재하지 않을 때 댓글 조회")
  public void getCommentsTest_PostNotFound() {
    // given
    when(postRepository.findById(anyLong())).thenReturn(Optional.empty());

    // when & then
    assertThrows(PostException.class, () -> {
      commentService.getComments(1L, PageRequest.of(0, 10));
    });
  }


}
