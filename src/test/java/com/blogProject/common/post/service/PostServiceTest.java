package com.blogProject.common.post.service;

import static com.blogProject.constant.CategoryConstants.CATEGORY_NAME;
import static com.blogProject.constant.MemberConstants.EMAIL;
import static com.blogProject.constant.PostConstants.IMAGE_URL;
import static com.blogProject.constant.PostConstants.NOT_EXIST_CATEGORY;
import static com.blogProject.constant.PostConstants.NOT_EXIST_IMAGE_ID;
import static com.blogProject.constant.PostConstants.POST_CONTENT;
import static com.blogProject.constant.PostConstants.POST_TITLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.blogProject.common.category.entity.Category;
import com.blogProject.common.category.exception.CategoryException;
import com.blogProject.common.category.repository.CategoryRepository;
import com.blogProject.common.image.exception.ImageException;
import com.blogProject.common.image.repository.ImageRepository;
import com.blogProject.common.image.s3Uploader.S3Uploader;
import com.blogProject.common.member.entity.Member;
import com.blogProject.common.member.exception.MemberException;
import com.blogProject.common.member.repository.MemberRepository;
import com.blogProject.common.post.converter.PostConverter;
import com.blogProject.common.post.dto.model.PostDto;
import com.blogProject.common.post.dto.model.WritePost;
import com.blogProject.common.post.entity.Post;
import com.blogProject.common.post.exception.PostException;
import com.blogProject.common.post.repository.PostRepository;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

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
  @Mock
  private ImageRepository imageRepository;

  @Mock
  private S3Uploader s3Uploader;


  @InjectMocks
  private PostService postService;

  private Post post;
  private PostDto postDto;
  private Member member;
  private Authentication authentication;
  private WritePost request;


  @BeforeEach
  public void setUp() {

    request = WritePost.builder()
        .title(POST_TITLE)
        .contents(POST_CONTENT)
        .build();

    post = Post.builder()
        .id(1L)
        .title(POST_TITLE)
        .contents(POST_CONTENT)
        .imageUrl(IMAGE_URL)
        .member(member)
        .build();

    postDto = PostDto.builder()
        .title(POST_TITLE)
        .contents(POST_CONTENT)
        .categoryName(CATEGORY_NAME)
        .imageUrl(IMAGE_URL)
        .memberName(EMAIL)
        .build();

    member = Member.builder()
        .email(EMAIL)
        .build();

    MultipartFile file = new MockMultipartFile("file", "originalFileName.jpg",
        MediaType.IMAGE_JPEG_VALUE, "filedata".getBytes());

    authentication = mock(Authentication.class);

    Category category = Category.builder()
        .name(CATEGORY_NAME)
        .id(1L)
        .build();
  }

  @Nested
  @DisplayName("포스트Service 테스트")
  class PostCRUDTest {


    @Test
    @DisplayName("게시글 생성 테스트")
    void createPost() {
      // given
      when(authentication.getName()).thenReturn(member.getEmail());
      when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(member));
      when(postConverter.dtoToEntity((WritePost) any(), any())).thenReturn(post);
      when(postRepository.save(any())).thenReturn(post);
      when(postConverter.entityToDto((Post) any())).thenReturn(postDto);

      // when
      PostDto result = postService.createPost(request, authentication);

      // then
      assertNotNull(result);
      assertEquals(POST_TITLE, result.getTitle());
      assertEquals(POST_CONTENT, result.getContents());
      assertEquals(EMAIL, result.getMemberName());
    }

    @Test
    @DisplayName("이미지 업로드 테스트")
    void uploadImageTest() throws IOException {
      // given
      MultipartFile file = mock(MultipartFile.class);
      when(s3Uploader.upload(any(), anyString())).thenReturn("http://test.com/test.jpg");

      // when
      postService.uploadImage(file);

      // then
      verify(imageRepository, times(1)).save(any());
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
    void updatePostTest() {
      // given
      when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
      when(postRepository.save(any(Post.class))).thenReturn(post);
      when(postConverter.entityToDto(any(Post.class))).thenReturn(postDto);

      // when
      PostDto result = postService.updatePost(1L, request);

      // then
      assertNotNull(result);
      assertEquals(POST_TITLE, result.getTitle());
      assertEquals(POST_CONTENT, result.getContents());
    }

    @Test
    @DisplayName("게시글 삭제 테스트")
    void deletePost() {
      // given
      when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

      when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
      doNothing().when(s3Uploader).deleteFileFromS3(anyString());
      doNothing().when(postRepository).delete(any(Post.class));

      // when
      postService.deletePost(1L);

      // then
      verify(postRepository, times(1)).findById(anyLong());
      verify(s3Uploader, times(1)).deleteFileFromS3(anyString());
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
      Page<PostDto> result = postService.searchPosts("tit", PageRequest.of(0, 10));

      // then
      assertEquals(1, result.getContent().size());
    }
  }


  @Nested
  @DisplayName("포스트 Service Exception 테스트")
  class PostExceptionTest {

    @Test
    @DisplayName("게시글 생성 실패 테스트 - 회원 정보 없음")
    void createPostMemberNotFoundTest() {
      // given
      when(authentication.getName()).thenReturn(member.getEmail());
      when(memberRepository.findByEmail(anyString())).thenReturn(Optional.empty());

      // then
      assertThrows(MemberException.class, () -> {
        // when
        postService.createPost(request, authentication);
      });
    }


    @Test
    @DisplayName("카테고리 설정 실패 테스트 - 존재하지 않는 카테고리 이름")
    void setPostPropertiesWithNonExistentCategoryNameTest() {
      // given
      request.setCategoryName(NOT_EXIST_CATEGORY);

      when(categoryRepository.findByName(NOT_EXIST_CATEGORY)).thenReturn(Optional.empty());

      // then
      assertThrows(CategoryException.class, () -> {
        // when
        postService.setPostProperties(post, request);
      });
    }

    @Test
    @DisplayName("이미지 설정 실패 테스트 - 존재하지 않는 이미지 ID")
    void setPostPropertiesWithNonExistentImageIdTest() {
      // given
      request.setImageId(NOT_EXIST_IMAGE_ID);

      when(imageRepository.findById(NOT_EXIST_IMAGE_ID)).thenReturn(Optional.empty());

      // then
      assertThrows(ImageException.class, () -> {
        // when
        postService.setPostProperties(post, request);
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
    void updatePostPostNotFoundTest() {
      // given
      when(postRepository.findById(anyLong())).thenReturn(Optional.empty());

      // then
      assertThrows(PostException.class, () -> {
        // when
        postService.updatePost(1L, request);
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
}

