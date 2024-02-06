package com.blogProject.common.comment.service;

import static com.blogProject.exception.ErrorCode.ACCESS_DENIED;
import static com.blogProject.exception.ErrorCode.COMMENT_NOT_FOUND;
import static com.blogProject.exception.ErrorCode.MEMBER_NOT_FOUND;
import static com.blogProject.exception.ErrorCode.POST_NOT_FOUND;

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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

  private final CommentRepository commentRepository;
  private final MemberRepository memberRepository;
  private final PostRepository postRepository;

  // 댓글 작성
  @Transactional
  public CommentDto writeComment(Long postId, String request, Authentication authentication) {
    Member member = memberRepository.findByEmail(authentication.getName())
        .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new PostException(POST_NOT_FOUND));
    Comment comment = Comment.builder()
        .post(post)
        .member(member)
        .comments(request)
        .build();
    CommentDto commentDto = CommentDto.fromEntity(comment);
    commentRepository.save(comment);
    return commentDto;
  }

  // 댓글 수정
  @Transactional
  public CommentDto updateComment(Long commentId, String request, Authentication authentication) {
    Comment comment = findCommentAndMatchMember(commentId,
        authentication);

    comment.setComments(request);
    return CommentDto.fromEntity(comment);
  }

  // 댓글 삭제
  @Transactional
  public void deleteComment(Long commentId, Authentication authentication) {
    Comment comment = findCommentAndMatchMember(commentId, authentication);

    commentRepository.delete(comment);
  }

  // 댓글 보기(페이징 처리)
  public Page<CommentDto> getComments(Long postId, Pageable pageable) {
    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new PostException(POST_NOT_FOUND));
    Page<Comment> comments = commentRepository.findByPost(post, pageable);
    return comments.map(CommentDto::fromEntity);
  }


  private Comment findCommentAndMatchMember(Long commentId, Authentication authentication) {
    Comment comment = commentRepository.findById(commentId)
        .orElseThrow(() -> new CommentException(COMMENT_NOT_FOUND));

    if (!comment.getMember().getEmail().equals(authentication.getName())) {
      throw new GlobalException(ACCESS_DENIED);
    }
    return comment;
  }

}
