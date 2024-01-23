package com.blogProject.post.converter;

import com.blogProject.category.entity.Category;
import com.blogProject.category.exception.CategoryNotFoundException;
import com.blogProject.category.repository.CategoryRepository;
import com.blogProject.post.dto.model.PostDto;
import com.blogProject.post.entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostConverter {

  private final CategoryRepository categoryRepository;

  public PostDto entityToDto(Post post) {
    PostDto postDto = new PostDto();
    postDto.setTitle(post.getTitle());
    postDto.setContents(post.getContents());
    if (post.getCategory() != null) {
      postDto.setCategoryName(post.getCategory().getName());
    }
    return postDto;
  }

  public Post dtoToEntity(PostDto postDto) {
    Post post = new Post();
    post.setTitle(postDto.getTitle());
    post.setContents(postDto.getContents());

    if (postDto.getCategoryName() != null) {
      Category category = categoryRepository.findByName(postDto.getCategoryName())
          .orElseThrow(() -> new CategoryNotFoundException("해당 카테고리가 존재하지 않습니다."));
      post.setCategory(category);
    }

    return post;
  }
}
