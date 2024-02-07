package com.blogProject.common.comment.repository;

import com.blogProject.common.comment.entity.Comment;
import com.blogProject.common.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

  Page<Comment> findByPost(Post post, Pageable pageable);

}
