package com.blogProject.common.post.service;

import static com.blogProject.exception.ErrorCode.CATEGORY_NOT_FOUND;
import static com.blogProject.exception.ErrorCode.MEMBER_NOT_FOUND;
import static com.blogProject.exception.ErrorCode.POST_NOT_FOUND;

import com.blogProject.common.category.entity.Category;
import com.blogProject.common.category.exception.CategoryException;
import com.blogProject.common.category.repository.CategoryRepository;
import com.blogProject.common.member.entity.Member;
import com.blogProject.common.member.exception.MemberException;
import com.blogProject.common.member.repository.MemberRepository;
import com.blogProject.common.post.converter.PostConverter;
import com.blogProject.common.post.dto.model.PostDto;
import com.blogProject.common.post.entity.Post;
import com.blogProject.common.post.exception.PostException;
import com.blogProject.common.post.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
  private final MemberRepository memberRepository;

  // 게시글 생성 (카테고리 없이도 생성 가능)
  @Transactional
  public PostDto createPost(PostDto postDto, UserDetails userDetails) {
    Member member = getMember(userDetails);
    Post post = postConverter.dtoToEntity(postDto, member);
    post = postRepository.save(post);
    return postConverter.entityToDto(post);
  }

  // 게시글 카테고리와 함꼐 생성
  @Transactional
  public PostDto createPostWithCategory(PostDto postDto, String categoryName,
      UserDetails userDetails) {
    Member member = getMember(userDetails);
    Category category = categoryRepository.findByName(categoryName)
        .orElseThrow(() -> new CategoryException(CATEGORY_NOT_FOUND, categoryName));
    postDto.setCategoryName(categoryName);
    Post post = postConverter.dtoToEntity(postDto, member);
    post.setCategory(category);
    post = postRepository.save(post);
    return postConverter.entityToDto(post);
  }


  private Member getMember(UserDetails userDetails) {
    return memberRepository.findByEmail(userDetails.getUsername())
        .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
  }


  // 게시글 조회
  public PostDto getPostById(Long id) {
    Post post = findPost(id);
    return postConverter.entityToDto(post);
  }

  // 전체 게시글 조회 (최신순으로)
  public Page<PostDto> getAllPosts(Pageable pageable) {
    Page<Post> posts = postRepository.findAllByOrderByCreatedDateDesc(pageable);
    return posts.map(postConverter::entityToDto);
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
  public Page<PostDto> searchPosts(String keyword, Pageable pageable) {
    Page<Post> postdtos = postRepository.findByTitleContainingOrContentContaining(keyword,
        pageable);
    return postdtos.map(postConverter::entityToDto);
  }

  public Post findPost(Long id) {
    return postRepository.findById(id)
        .orElseThrow(() -> new PostException(POST_NOT_FOUND));
  }


}
