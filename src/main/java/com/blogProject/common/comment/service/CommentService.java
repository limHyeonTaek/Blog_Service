package com.blogProject.common.comment.service;

import static com.blogProject.exception.ErrorCode.MEMBER_NOT_FOUND;
import static com.blogProject.exception.ErrorCode.POST_NOT_FOUND;

import com.blogProject.common.comment.dto.model.CommentDto;
import com.blogProject.common.comment.entity.Comment;
import com.blogProject.common.comment.repository.CommentRepository;
import com.blogProject.common.member.entity.Member;
import com.blogProject.common.member.exception.MemberException;
import com.blogProject.common.member.repository.MemberRepository;
import com.blogProject.common.post.entity.Post;
import com.blogProject.common.post.exception.PostException;
import com.blogProject.common.post.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

  private final CommentRepository commentRepository;
  private final MemberRepository memberRepository;
  private final PostRepository postRepository;

  @Transactional
  public CommentDto writeComment(Long postId, String request, Authentication authentication) {
    Member member = memberRepository.findByEmail(authentication.getName())
        .orElseThrow(() -> new MemberException(
            MEMBER_NOT_FOUND));
    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new PostException(POST_NOT_FOUND));
    Comment comment = Comment.builder()
        .post(post)
        .member(member)
        .comments(request)
        .build();
    commentRepository.save(comment);
    return CommentDto.fromEntity(comment);
  }

//  public void deleteComment(Long commentId) {
//    commentRepository.deleteById(commentId);
//  }
}
