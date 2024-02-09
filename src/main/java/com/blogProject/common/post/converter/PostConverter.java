package com.blogProject.common.post.converter;

import static com.blogProject.exception.ErrorCode.CATEGORY_NOT_FOUND;

import com.blogProject.common.category.entity.Category;
import com.blogProject.common.category.exception.CategoryException;
import com.blogProject.common.category.repository.CategoryRepository;
import com.blogProject.common.member.entity.Member;
import com.blogProject.common.post.dto.model.PostDto;
import com.blogProject.common.post.entity.Post;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PostConverter {

  private final CategoryRepository categoryRepository;

  public PostDto entityToDto(Post post) {
    PostDto postDto = new PostDto();
    postDto.setTitle(post.getTitle());
    postDto.setContents(post.getContents());
    postDto.setCreatedDate(post.getCreatedDate());
    postDto.setUpdatedDate(post.getUpdatedDate());
    postDto.setMemberName(post.getMember().getEmail());
    postDto.setImageUrl(post.getImageUrl());
    if (post.getCategory() != null) {
      postDto.setCategoryName(post.getCategory().getName());
    }
    return postDto;
  }

  public List<PostDto> entityToDto(Page<Post> posts) {
    return posts.stream().map(this::entityToDto).collect(Collectors.toList());
  }

  public Post dtoToEntity(PostDto postDto, Member member) {
    Post post = new Post();
    post.setTitle(postDto.getTitle());
    post.setContents(postDto.getContents());
    post.setMember(member);

    if (postDto.getCategoryName() != null) {
      Category category = categoryRepository.findByName(postDto.getCategoryName())
          .orElseThrow(() -> new CategoryException(CATEGORY_NOT_FOUND));
      post.setCategory(category);
    }

    return post;
  }
}

