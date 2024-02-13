package com.blogProject.common.comment.repository;

import com.blogProject.common.comment.entity.Comment;
import com.blogProject.common.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

  @Query("select c from Comment c join fetch c.member join fetch c.post where c.post = :post")
  Page<Comment> findByPostWithMemberAndPost(@Param("post") Post post, Pageable pageable);

}
