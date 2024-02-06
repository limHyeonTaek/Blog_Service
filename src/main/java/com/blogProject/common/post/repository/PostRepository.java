package com.blogProject.common.post.repository;

import com.blogProject.common.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

  Page<Post> findAllByOrderByCreatedDateDesc(Pageable pageable);

  @Query("SELECT p FROM Post p WHERE p.title LIKE %?1% OR p.contents LIKE %?1%")
  Page<Post> findByTitleContainingOrContentContaining(String keyword, Pageable pageable);

  @Modifying
  @Query("update Post p set p.category = null where p.category.id = :categoryId")
  void removeCategoryFromPosts(@Param("categoryId") Long categoryId);

}
