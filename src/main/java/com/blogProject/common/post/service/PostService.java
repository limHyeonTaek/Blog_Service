package com.blogProject.common.post.service;

import com.blogProject.common.category.entity.Category;
import com.blogProject.common.category.exception.CategoryNotFoundException;
import com.blogProject.common.category.repository.CategoryRepository;
import com.blogProject.common.post.converter.PostConverter;
import com.blogProject.common.post.dto.model.PostDto;
import com.blogProject.common.post.entity.Post;
import com.blogProject.common.post.exception.PostNotfoundException;
import com.blogProject.common.post.repository.PostRepository;
import jakarta.transaction.Transactional;
import java.util.List;
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

  // 게시글 조회
  public PostDto getPostById(Long id) {
    Post post = postRepository.findById(id)
        .orElseThrow(() -> new PostNotfoundException("게시된 글이 존재하지 않습니다."));
    return postConverter.entityToDto(post);
  }

  // 전체 게시글 조회 (최신순으로)
  public List<PostDto> getAllPosts() {
    List<Post> posts = postRepository.findAllByOrderByCreatedDateDesc();
    return postConverter.entityToDto(posts);
  }

  // 게시글 수정
  @Transactional
  public PostDto updatePost(Long id, PostDto postDto) {
    Post post = postRepository.findById(id)
        .orElseThrow(() -> new PostNotfoundException("게시된 글이 존재하지 않습니다."));
    Category category = categoryRepository.findByName(postDto.getCategoryName()).orElse(null);
    post.setTitle(postDto.getTitle());
    post.setContents(postDto.getContents());
    post.setCategory(category);
    postRepository.save(post);
    return postConverter.entityToDto(post);
  }

  // 게시글 삭제
  @Transactional
  public void deletePost(Long id) {
    Post post = postRepository.findById(id)
        .orElseThrow(() -> new PostNotfoundException("게시된 글이 존재하지 않습니다."));
    postRepository.delete(post);
  }

  // 제목이나 본문 일부만 검색만으로 검색가능
  public List<PostDto> searchPosts(String keyword) {
    List<Post> postdtos = postRepository.findByTitleContainingOrContentContaining(keyword);
    return postConverter.entityToDto(postdtos);
  }

}
