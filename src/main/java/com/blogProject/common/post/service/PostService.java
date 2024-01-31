package com.blogProject.common.post.service;

import com.blogProject.common.category.entity.Category;
import com.blogProject.common.category.exception.CategoryException;
import com.blogProject.common.category.repository.CategoryRepository;
import com.blogProject.common.post.converter.PostConverter;
import com.blogProject.common.post.dto.model.PostDto;
import com.blogProject.common.post.entity.Post;
import com.blogProject.common.post.exception.PostException;
import com.blogProject.common.post.repository.PostRepository;
import com.blogProject.exception.ErrorCode;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PostService {

  private final PostRepository postRepository;
  private final CategoryRepository categoryRepository;
  private final PostConverter postConverter;

  // 게시글 생성 (카테고리 없이도 생성 가능)
  public PostDto createPost(PostDto postDto, UserDetails userDetails) {
    Post post = postConverter.dtoToEntity(postDto, userDetails);
    post = postRepository.save(post);
    return postConverter.entityToDto(post);
  }

  // 게시글을 생성할 때 카테고리도 같이 설정
  @Transactional
  public PostDto createPostWithCategory(PostDto postDto, String categoryName,
      UserDetails userDetails) {
    Category category = categoryRepository.findByName(categoryName)
        .orElseThrow(() -> new CategoryException(ErrorCode.CATEGORY_NOT_FOUND, categoryName));
    postDto.setCategoryName(categoryName);
    Post post = postConverter.dtoToEntity(postDto, userDetails);
    post.setCategory(category);
    post = postRepository.save(post);
    return postConverter.entityToDto(post);
  }

  // 게시글 조회
  public PostDto getPostById(Long id) {
    Post post = findPost(id);
    return postConverter.entityToDto(post);
  }

  // 전체 게시글 조회 (최신순으로)
  public List<PostDto> getAllPosts() {
    List<Post> posts = postRepository.findAllByOrderByCreatedDateDesc();
    return postConverter.entityToDto(posts);
  }

  // 게시글 수정
  @Transactional
  @PostAuthorize("isAuthenticated() " + "and returnObject.memberName == principal.username")
  public PostDto updatePost(Long id, PostDto postDto) {
    Post post = findPost(id);
    Category category = categoryRepository.findByName(postDto.getCategoryName()).orElse(null);
    post.setTitle(postDto.getTitle());
    post.setContents(postDto.getContents());
    post.setCategory(category);
    postRepository.save(post);
    return postConverter.entityToDto(post);
  }

  // 게시글 삭제
  @Transactional
  @PreAuthorize("isAuthenticated() and @postRepository.findById(#id).orElse(null)?.member.email == principal.username")
  public void deletePost(Long id) {
    Post post = findPost(id);
    postRepository.delete(post);
  }

  // 제목이나 본문 일부만 검색만으로 검색가능
  public List<PostDto> searchPosts(String keyword) {
    List<Post> postdtos = postRepository.findByTitleContainingOrContentContaining(keyword);
    return postConverter.entityToDto(postdtos);
  }

  public Post findPost(Long id) {
    return postRepository.findById(id)
        .orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND));
  }


}
