package com.blogProject.common.post.api;

import static com.blogProject.constant.MemberConstants.EMAIL;
import static com.blogProject.constant.PostConstants.IMAGE_URL;
import static com.blogProject.constant.PostConstants.NOT_EXIST_CATEGORY;
import static com.blogProject.constant.PostConstants.NOT_EXIST_IMAGE_ID;
import static com.blogProject.constant.PostConstants.POST_CONTENT;
import static com.blogProject.constant.PostConstants.POST_TITLE;
import static com.blogProject.exception.ErrorCode.CATEGORY_NOT_FOUND;
import static com.blogProject.exception.ErrorCode.IMAGE_NOT_FOUND;
import static com.blogProject.exception.ErrorCode.MEMBER_WITHDRAWAL;
import static com.blogProject.exception.ErrorCode.POST_NOT_FOUND;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.blogProject.common.category.exception.CategoryException;
import com.blogProject.common.category.repository.CategoryRepository;
import com.blogProject.common.image.exception.ImageException;
import com.blogProject.common.image.repository.ImageRepository;
import com.blogProject.common.member.exception.MemberException;
import com.blogProject.common.post.dto.model.PostDto;
import com.blogProject.common.post.dto.model.WritePost;
import com.blogProject.common.post.entity.Post;
import com.blogProject.common.post.exception.PostException;
import com.blogProject.common.post.service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class PostControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private PostService postService;

  @Mock
  private CategoryRepository categoryRepository;

  @Mock
  private ImageRepository imageRepository;

  private PostDto postDto;
  private WritePost request;
  private Post post;

  @BeforeEach
  void setUp() {

    request = WritePost.builder()
        .title(POST_TITLE)
        .contents(POST_CONTENT)
        .build();

    postDto = PostDto.builder()
        .PostId(1L)
        .title(POST_TITLE)
        .contents(POST_CONTENT)
        .categoryName(null)
        .memberName(EMAIL)
        .build();

    post = Post.builder()
        .id(1L)
        .title(POST_TITLE)
        .contents(POST_CONTENT)
        .imageUrl(IMAGE_URL)
        .build();

  }

  @Nested
  @DisplayName("포스트 Controller 테스트")
  class PostTest {

    @Test
    @DisplayName("게시글 생성 성공")
    @WithMockUser(username = EMAIL, roles = "USER")
    void createPostTest() throws Exception {
      // given
      when(postService.createPost(any(WritePost.class), any())).thenReturn(postDto);

      // when & then
      mockMvc.perform(post("/api/post")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.title", is(postDto.getTitle())))
          .andExpect(jsonPath("$.contents", is(postDto.getContents())));
    }


    @Test
    @DisplayName("게시글 수정 성공")
    @WithMockUser(username = EMAIL, roles = "USER")
    void updatePostTest() throws Exception {
      // given
      when(postService.updatePost(anyLong(), any(WritePost.class))).thenReturn(postDto);

      // when & then
      mockMvc.perform(patch("/api/post/" + 1L)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.title", is(postDto.getTitle())))
          .andExpect(jsonPath("$.contents", is(postDto.getContents())));
    }


    @Test
    @DisplayName("게시글 삭제 테스트")
    @WithMockUser(username = EMAIL, roles = "USER")
    void deletePostTest() throws Exception {
      // given
      doNothing().when(postService).deletePost(anyLong());

      // when & then
      mockMvc.perform(delete("/api/post/" + postDto.getPostId())
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk());
    }

    @Test
    @DisplayName("게시글 조회 테스트")
    void getPostByIdTest() throws Exception {
      // given
      when(postService.getPostById(anyLong())).thenReturn(postDto);

      // when & then
      mockMvc.perform(get("/api/post/get/" + postDto.getPostId())
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.title", is(postDto.getTitle())))
          .andExpect(jsonPath("$.contents", is(postDto.getContents())));
    }

    /**
     * 테스트 시에 500 에러가 나는데 해결하지 못했습니다. POSTMAN 형식으로 API 테스트를 진행할 때는 문제가 없는데, 테스트코드에서 500 에러가 남(페이징 썻을
     * 때만)
     */
    @Test
    @DisplayName("게시글 전체 조회 테스트")
    void getAllPostsTest() throws Exception {
      // given
      Page<PostDto> page = new PageImpl<>(Collections.singletonList(postDto));
      when(postService.getAllPosts(any(Pageable.class))).thenReturn(page);

      // when & then
      mockMvc.perform(get("/api/post/get")
              .param("page", "0")
              .param("size", "10"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.content[0].title", is(postDto.getTitle())))
          .andExpect(jsonPath("$.content[0].contents", is(postDto.getContents())));
    }

    /**
     * 테스트 시에 500 에러가 나는데 해결하지 못했습니다 POSTMAN 형식으로 API 테스트를 진행할 때는 문제가 없는데 테스트코드에서 500 에러가 남
     */
    @Test
    @DisplayName("게시글 검색 테스트")
    void searchPostsTest() throws Exception {
      // given
      Page<PostDto> postDtos = new PageImpl<>(Collections.singletonList(postDto));

      when(postService.searchPosts(anyString(), any(Pageable.class))).thenReturn(postDtos);

      // when & then
      mockMvc.perform(get("/api/post/get/search")
              .contentType(MediaType.APPLICATION_JSON)
              .param("keyword", "tit")
              .param("page", "0")
              .param("size", "10"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.content[0].title", is(postDto.getTitle())))
          .andExpect(jsonPath("$.content[0].contents", is(postDto.getContents())));
    }

    @Test
    @DisplayName("이미지 업로드 성공")
    @WithMockUser(username = EMAIL, roles = "USER")
    void uploadImageTest() throws Exception {
      // given
      MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg",
          "test image content".getBytes());

      // when & then
      mockMvc.perform(multipart("/api/post/uploadImage")
              .file(file)
              .contentType(MediaType.MULTIPART_FORM_DATA))
          .andExpect(status().isOk())
          .andExpect(content().string("이미지가 업로드 되었습니다."));
    }
  }


  @Nested
  @DisplayName("포스트 Controller Exception 테스트")
  class PostExceptionTest {

    @Test
    @DisplayName("게시글 생성 실패 테스트")
    @WithMockUser(username = EMAIL, roles = "USER")
    void createPostFailTest() throws Exception {
      // given
      doThrow(new MemberException(MEMBER_WITHDRAWAL))
          .when(postService).createPost(any(WritePost.class), any(Authentication.class));

      // when & then
      mockMvc.perform(post("/api/post")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.errorCode").value(MEMBER_WITHDRAWAL.toString()))
          .andExpect(jsonPath("$.message").value(MEMBER_WITHDRAWAL.getMessage()));
    }


    @Test
    @DisplayName("게시글 수정 실패 테스트")
    @WithMockUser(username = EMAIL, roles = "USER")
    void updatePostFailTest() throws Exception {
      // given
      doThrow(new PostException(POST_NOT_FOUND))
          .when(postService).updatePost(anyLong(), any(WritePost.class));

      // when & then
      mockMvc.perform(patch("/api/post/" + 1L)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.errorCode").value(POST_NOT_FOUND.toString()))
          .andExpect(jsonPath("$.message").value(POST_NOT_FOUND.getMessage()));
    }

    @Test
    @DisplayName("게시글 삭제 실패 테스트")
    @WithMockUser(username = EMAIL, roles = "USER")
    void deletePostFailTest() throws Exception {
      // given
      doThrow(new PostException(POST_NOT_FOUND)).when(postService).deletePost(anyLong());

      // when & then
      mockMvc.perform(delete("/api/post/" + postDto.getPostId())
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.errorCode").value(POST_NOT_FOUND.toString()))
          .andExpect(jsonPath("$.message").value(POST_NOT_FOUND.getMessage()));
    }

    @Test
    @DisplayName("게시글 조회 실패 테스트")
    void getPostByIdFailTest() throws Exception {
      // given
      when(postService.getPostById(anyLong())).thenThrow(new PostException(POST_NOT_FOUND));

      // when & then
      mockMvc.perform(get("/api/post/get/" + postDto.getPostId())
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.errorCode").value(POST_NOT_FOUND.toString()))
          .andExpect(jsonPath("$.message").value(POST_NOT_FOUND.getMessage()));
    }

    @Test
    @DisplayName("카테고리가 존재하지 않을 때 게시글 생성 실패 테스트")
    @WithMockUser(username = EMAIL, roles = "USER")
    void createPostFailWhenCategoryNotFound() throws Exception {
      // Given
      request.setCategoryName(NOT_EXIST_CATEGORY);
      when(categoryRepository.findByName(NOT_EXIST_CATEGORY)).thenThrow(
          new CategoryException(CATEGORY_NOT_FOUND));

      // When & Then
      mockMvc.perform(post("/api/posts")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("이미지 ID가 존재하지 않을 때 게시글 생성 실패 테스트")
    @WithMockUser(username = EMAIL, roles = "USER")
    void createPostFailWhenImageNotFound() throws Exception {
      // Given
      request.setImageId(NOT_EXIST_IMAGE_ID);
      when(imageRepository.findById(NOT_EXIST_IMAGE_ID)).thenThrow(
          new ImageException(IMAGE_NOT_FOUND));

      // When & Then
      mockMvc.perform(post("/api/posts")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isNotFound());
    }


  }
}
