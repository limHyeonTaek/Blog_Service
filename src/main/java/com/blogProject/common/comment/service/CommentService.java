package com.blogProject.common.comment.service;

import static com.blogProject.exception.ErrorCode.ACCESS_DENIED;
import static com.blogProject.exception.ErrorCode.COMMENT_NOT_FOUND;
import static com.blogProject.exception.ErrorCode.MEMBER_NOT_FOUND;
import static com.blogProject.exception.ErrorCode.POST_NOT_FOUND;

import com.blogProject.common.comment.dto.WriteReply;
import com.blogProject.common.comment.dto.model.CommentDto;
import com.blogProject.common.comment.dto.model.ReplyDto;
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

  // 댓글 생성
  @Transactional
  public CommentDto writeComment(Long postId, String request, Authentication authentication) {
    Member member = getMemberByEmail(authentication.getName());
    Post post = getPostById(postId);
    Comment comment = Comment.builder()
        .post(post)
        .member(member)
        .comments(request)
        .build();
    commentRepository.save(comment);
    return CommentDto.fromEntity(comment);
  }

  // 댓글 수정(대댓글 포함)
  @Transactional
  public CommentDto updateComment(Long commentId, String request, Authentication authentication) {
    Comment comment = findCommentAndMatchMember(commentId, authentication);
    comment.setComments(request);
    return CommentDto.fromEntity(comment);
  }

  // 댓글 삭제(대댓글 포함)
  @Transactional
  public void deleteComment(Long commentId, Authentication authentication) {
    Comment comment = findCommentAndMatchMember(commentId, authentication);
    commentRepository.delete(comment);
  }

  // 댓글 조회(대댓글 포함)
  @Transactional
  public Page<ReplyDto> getComments(Long postId, Pageable pageable) {
    Post post = getPostById(postId);
    Page<Comment> comments = commentRepository.findByPost(post, pageable);
    return comments.map(ReplyDto::fromEntity);
  }

  // 대댓글 생성
  @Transactional
  public ReplyDto writeReply(Long commentId, WriteReply request, Authentication authentication) {
    Member member = getMemberByEmail(authentication.getName());
    Comment parentComment = getCommentById(commentId);
    Comment childComment = Comment.builder()
        .member(member)
        .post(parentComment.getPost())
        .comments(request.getReply())
        .parentComment(parentComment)
        .build();
    commentRepository.save(childComment);
    return ReplyDto.fromEntity(childComment);
  }

  private Member getMemberByEmail(String email) {
    return memberRepository.findByEmail(email)
        .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
  }

  private Post getPostById(Long postId) {
    return postRepository.findById(postId)
        .orElseThrow(() -> new PostException(POST_NOT_FOUND));
  }

  private Comment getCommentById(Long commentId) {
    return commentRepository.findById(commentId)
        .orElseThrow(() -> new CommentException(COMMENT_NOT_FOUND));
  }

  private Comment findCommentAndMatchMember(Long commentId, Authentication authentication) {
    Comment comment = getCommentById(commentId);
    Member member = getMemberByEmail(authentication.getName());
    if (!comment.isWrittenBy(member)) {
      throw new GlobalException(ACCESS_DENIED);
    }
    return comment;
  }
}

