package com.blogProject.common.post.service;

import static com.blogProject.constant.CategoryConstants.CATEGORY_NAME;
import static com.blogProject.constant.MemberConstants.EMAIL;
import static com.blogProject.constant.PostConstants.POST_CONTENT;
import static com.blogProject.constant.PostConstants.POST_TITLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

  @Mock
  private PostRepository postRepository;

  @Mock
  private CategoryRepository categoryRepository;

  @Mock
  private PostConverter postConverter;

  @Mock
  private MemberRepository memberRepository;

  @InjectMocks
  private PostService postService;

  private Post post;
  private PostDto postDto;
  private Member member;
  private Authentication authentication;
  private Category category;

  @BeforeEach
  public void setUp() {
    post = Post.builder()
        .id(1L)
        .title(POST_TITLE)
        .contents(POST_CONTENT)
        .build();

    postDto = PostDto.builder()
        .title(POST_TITLE)
        .contents(POST_CONTENT)
        .categoryName(CATEGORY_NAME)
        .build();

    member = Member.builder()
        .email(EMAIL)
        .build();
    authentication = mock(Authentication.class);

    category = new Category();
    category.setName(CATEGORY_NAME);
    category.setId(1L);
  }

  @Test
  @DisplayName("게시글 생성 테스트")
  void createPost() {
    // given
    when(authentication.getName()).thenReturn(member.getEmail());
    when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(member));
    when(postConverter.dtoToEntity(any(PostDto.class), any(Member.class))).thenReturn(post);
    when(postRepository.save(any(Post.class))).thenReturn(post);
    when(postConverter.entityToDto(any(Post.class))).thenReturn(postDto);

    // when
    PostDto result = postService.createPost(postDto, authentication);

    // then
    assertNotNull(result);
  }

  @Test
  @DisplayName("게시글 카테고리와 함께 생성 테스트")
  void createPostWithCategory() {
    // given
    when(authentication.getName()).thenReturn(member.getEmail());
    when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(member));
    when(categoryRepository.findByName(anyString())).thenReturn(Optional.of(category));
    when(postConverter.dtoToEntity(any(PostDto.class), any(Member.class))).thenReturn(post);
    when(postRepository.save(any(Post.class))).thenReturn(post);
    when(postConverter.entityToDto(any(Post.class))).thenReturn(postDto);

    // when
    PostDto result = postService.createPostWithCategory(postDto, category.getName(),
        authentication);

    // then
    assertNotNull(result);
  }

  @Test
  @DisplayName("게시글 조회 테스트")
  void getPostById() {
    // given
    when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
    when(postConverter.entityToDto(any(Post.class))).thenReturn(postDto);

    // when
    PostDto result = postService.getPostById(1L);

    // then
    assertNotNull(result);
  }

  @Test
  @DisplayName("전체 게시글 조회 테스트")
  void getAllPosts() {
    // given
    Page<Post> page = new PageImpl<>(Collections.singletonList(post));
    when(postRepository.findAllByOrderByCreatedDateDesc(any(Pageable.class))).thenReturn(page);
    when(postConverter.entityToDto(any(Post.class))).thenReturn(postDto);

    // when
    Page<PostDto> result = postService.getAllPosts(PageRequest.of(0, 10));

    // then
    assertEquals(1, result.getContent().size());
  }

  @Test
  @DisplayName("게시글 수정 테스트")
  void updatePost() {
    // given
    when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
    when(categoryRepository.findByName(anyString())).thenReturn(Optional.of(category));
    when(postRepository.save(any(Post.class))).thenReturn(post);
    when(postConverter.entityToDto(any(Post.class))).thenReturn(postDto);

    // when
    PostDto result = postService.updatePost(1L, postDto);

    // then
    assertNotNull(result);
  }

  @Test
  @DisplayName("게시글 삭제 테스트")
  void deletePost() {
    // given
    when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

    // when
    postService.deletePost(1L);

    // then
    verify(postRepository, times(1)).delete(any(Post.class));
  }

  @Test
  @DisplayName("게시글 검색 테스트")
  void searchPosts() {
    // given
    Page<Post> page = new PageImpl<>(Collections.singletonList(post));
    when(postRepository.findByTitleContainingOrContentContaining(anyString(),
        any(Pageable.class))).thenReturn(page);
    when(postConverter.entityToDto(any(Post.class))).thenReturn(postDto);

    // when
    Page<PostDto> result = postService.searchPosts("test", PageRequest.of(0, 10));

    // then
    assertEquals(1, result.getContent().size());
  }

  @Test
  @DisplayName("게시글 생성 실패 테스트 - 회원 정보 없음")
  void createPost_memberNotFound() {
    // given
    when(authentication.getName()).thenReturn(member.getEmail());
    when(memberRepository.findByEmail(anyString())).thenReturn(Optional.empty());

    // then
    assertThrows(MemberException.class, () -> {
      // when
      postService.createPost(postDto, authentication);
    });
  }

  @Test
  @DisplayName("게시글 생성 실패 테스트 - 카테고리 정보 없음")
  void createPostWithCategory_categoryNotFound() {
    // given
    when(authentication.getName()).thenReturn(member.getEmail());
    when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(member));
    when(categoryRepository.findByName(anyString())).thenReturn(Optional.empty());

    // then
    assertThrows(CategoryException.class, () -> {
      // when
      postService.createPostWithCategory(postDto, category.getName(), authentication);
    });
  }

  @Test
  @DisplayName("게시글 조회 실패 테스트 - 게시글 정보 없음")
  void getPostById_postNotFound() {
    // given
    when(postRepository.findById(anyLong())).thenReturn(Optional.empty());

    // then
    assertThrows(PostException.class, () -> {
      // when
      postService.getPostById(1L);
    });
  }

  @Test
  @DisplayName("게시글 수정 실패 테스트 - 게시글 정보 없음")
  void updatePost_postNotFound() {
    // given
    when(postRepository.findById(anyLong())).thenReturn(Optional.empty());

    // then
    assertThrows(PostException.class, () -> {
      // when
      postService.updatePost(1L, postDto);
    });
  }

  @Test
  @DisplayName("게시글 삭제 실패 테스트 - 게시글 정보 없음")
  void deletePost_postNotFound() {
    // given
    when(postRepository.findById(anyLong())).thenReturn(Optional.empty());

    // then
    assertThrows(PostException.class, () -> {
      // when
      postService.deletePost(1L);
    });
  }

}
