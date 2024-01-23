package com.blogProject.post.service;

import com.blogProject.category.entity.Category;
import com.blogProject.category.exception.CategoryNotFoundException;
import com.blogProject.category.repository.CategoryRepository;
import com.blogProject.post.converter.PostConverter;
import com.blogProject.post.dto.model.PostDto;
import com.blogProject.post.entity.Post;
import com.blogProject.post.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PostService {

  private final PostRepository postRepository;
  private final CategoryRepository categoryRepository;
  private final PostConverter postConverter;


  // 게시글 생성 (카테고리 없이도 생성 가능)
  public PostDto createPost(PostDto postDto) {
    Post post = postConverter.dtoToEntity(postDto);
    post = postRepository.save(post);
    return postConverter.entityToDto(post);
  }

  // 게시글을 생성할 때 카테고리도 같이 설정
  @Transactional
  public PostDto createPostWithCategory(PostDto postDto, String categoryName) {
    Category category = categoryRepository.findByName(categoryName)
        .orElseThrow(() -> new CategoryNotFoundException("해당 카테고리가 존재하지 않습니다."));
    postDto.setCategoryName(categoryName);
    Post post = postConverter.dtoToEntity(postDto);
    post.setCategory(category);
    post = postRepository.save(post);
    return postConverter.entityToDto(post);
  }
}
