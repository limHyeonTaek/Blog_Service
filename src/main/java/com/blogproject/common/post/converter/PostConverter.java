package com.blogProject.common.post.converter;

import com.blogProject.common.category.entity.Category;
import com.blogProject.common.category.exception.CategoryNotFoundException;
import com.blogProject.common.category.repository.CategoryRepository;
import com.blogProject.common.post.dto.model.PostDto;
import com.blogProject.common.post.entity.Post;
import java.util.List;
import java.util.stream.Collectors;
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
    postDto.setCreatedDate(post.getCreatedDate());
    postDto.setUpdatedDate(post.getUpdatedDate());
    if (post.getCategory() != null) {
      postDto.setCategoryName(post.getCategory().getName());
    }
    return postDto;
  }

  public List<PostDto> entityToDto(List<Post> posts) {
    return posts.stream()
        .map(this::entityToDto)
        .collect(Collectors.toList());
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