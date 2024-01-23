package com.blogProject.post.repository;

import com.blogProject.post.entity.Post;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
  List<Post> findAllByOrderByCreatedDateDesc();

  @Query("SELECT p FROM Post p WHERE p.title LIKE %?1% OR p.contents LIKE %?1%")
  List<Post> findByTitleContainingOrContentContaining(String keyword);
}
