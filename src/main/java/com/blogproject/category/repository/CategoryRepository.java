package com.blogProject.category.repository;

import com.blogProject.category.entity.Category;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

  boolean existsByName(String categoryName);
  Optional<Category> findByName(String name);
}