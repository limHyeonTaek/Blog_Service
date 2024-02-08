package com.blogProject.common.category.api;

import static com.blogProject.constant.CategoryConstants.CATEGORY_NAME;
import static com.blogProject.constant.MemberConstants.EMAIL;
import static com.blogProject.exception.ErrorCode.ACCESS_DENIED_EXCEPTION;
import static com.blogProject.exception.ErrorCode.CATEGORY_ALREADY_EXISTS;
import static com.blogProject.exception.ErrorCode.CATEGORY_NOT_FOUND;
import static com.blogProject.exception.ErrorCode.MEMBER_NOT_FOUND;
import static com.blogProject.exception.ErrorCode.MEMBER_WITHDRAWAL;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.blogProject.common.category.dto.CategoryDto;
import com.blogProject.common.category.exception.CategoryException;
import com.blogProject.common.category.service.CategoryService;
import com.blogProject.common.member.exception.MemberException;
import com.blogProject.exception.GlobalException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = EMAIL, roles = "USER")
public class CategoryControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private CategoryService categoryService;

  private CategoryDto categoryDto;

  @BeforeEach
  void setUp() {
    categoryDto = CategoryDto.builder().categoryName(CATEGORY_NAME).build();
  }

  @Nested
  @DisplayName("카테고리 Controller 테스트")
  class CategoryCRUDTest {

    @Test
    @DisplayName("카테고리 생성 성공")
    void createCategoryTest() throws Exception {
      // given
      when(categoryService.createCategory(anyString(), any())).thenReturn(categoryDto);

      // when & then
      mockMvc.perform(post("/api/category").contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(categoryDto))).andExpect(status().isCreated())
          .andExpect(jsonPath("$.categoryName", is(categoryDto.getCategoryName())));
    }

    @Test
    @DisplayName("모든 카테고리 조회 성공")
    void getAllCategoryTest() throws Exception {
      // given
      List<CategoryDto> categoryDtoList = Collections.singletonList(categoryDto);
      when(categoryService.getAllCategory()).thenReturn(categoryDtoList);

      // when & then
      mockMvc.perform(get("/api/category").contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$[0].categoryName", is(categoryDtoList.get(0).getCategoryName())));
    }

    @Test
    @DisplayName("카테고리 업데이트 성공")
    void updateCategoryTest() throws Exception {
      // given
      when(categoryService.updateCategory(anyLong(), any())).thenReturn(categoryDto);

      // when & then
      mockMvc.perform(put("/api/category/" + 1L).contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(categoryDto))).andExpect(status().isOk())
          .andExpect(jsonPath("$.categoryName", is(categoryDto.getCategoryName())));
    }

    @Test
    @DisplayName("카테고리 삭제 성공")
    void deleteCategoryTest() throws Exception {
      // given
      doNothing().when(categoryService).deleteCategory(anyLong(), any());

      // when & then
      mockMvc.perform(delete("/api/category/" + 1L).contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk()).andExpect(content().string("성공적으로 삭제 되었습니다."));
    }
  }

  @Nested
  @DisplayName("카테고리 Controller Exception 테스트")
  class CategoryErrorTest {

    @Test
    @DisplayName("존재하지 않는 카테고리 업데이트 에러")
    void updateCategoryNotFoundTest() throws Exception {
      // given
      when(categoryService.updateCategory(anyLong(), any())).thenThrow(
          new CategoryException(CATEGORY_NOT_FOUND));

      // when & then
      mockMvc.perform(put("/api/category/" + 1L).contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(categoryDto)))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.errorCode").value(CATEGORY_NOT_FOUND.toString()))
          .andExpect(jsonPath("$.message").value(CATEGORY_NOT_FOUND.getMessage()));
    }

    @Test
    @DisplayName("존재하지 않는 카테고리 삭제 에러")
    void deleteCategoryNotFoundTest() throws Exception {
      // given
      doThrow(new CategoryException(CATEGORY_NOT_FOUND)).when(categoryService)
          .deleteCategory(anyLong(), any());

      // when & then
      mockMvc.perform(delete("/api/category/" + 1L).contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.errorCode").value(CATEGORY_NOT_FOUND.toString()))
          .andExpect(jsonPath("$.message").value(CATEGORY_NOT_FOUND.getMessage()));
    }

    @Test
    @DisplayName("이미 존재하는 카테고리 생성 에러")
    void createCategoryAlreadyExistsTest() throws Exception {
      // given
      when(categoryService.createCategory(anyString(), any())).thenThrow(
          new CategoryException(CATEGORY_ALREADY_EXISTS));

      //when & then
      mockMvc.perform(post("/api/category")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(categoryDto)))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.errorCode").value(CATEGORY_ALREADY_EXISTS.toString()))
          .andExpect(jsonPath("$.message").value(CATEGORY_ALREADY_EXISTS.getMessage()));
    }

    @Test
    @DisplayName("탈퇴한 회원의 카테고리 생성 에러")
    void createCategoryWithWithdrawalMemberTest() throws Exception {
      // given
      when(categoryService.createCategory(anyString(), any())).thenThrow(
          new MemberException(MEMBER_WITHDRAWAL));

      //when & then
      mockMvc.perform(post("/api/category")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(categoryDto)))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.errorCode").value(MEMBER_WITHDRAWAL.toString()))
          .andExpect(jsonPath("$.message").value(MEMBER_WITHDRAWAL.getMessage()));
    }

    @Test
    @DisplayName("존재하지 않는 회원의 카테고리 생성 에러")
    void createCategoryWithNotFoundMemberTest() throws Exception {
      // given
      when(categoryService.createCategory(anyString(), any())).thenThrow(
          new MemberException(MEMBER_NOT_FOUND));

      // when & then
      mockMvc.perform(post("/api/category")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(categoryDto)))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.errorCode").value(MEMBER_NOT_FOUND.toString()))
          .andExpect(jsonPath("$.message").value(MEMBER_NOT_FOUND.getMessage()));
    }

    @Test
    @DisplayName("다른 회원의 카테고리 삭제 에러")
    void deleteCategoryWithAccessDeniedTest() throws Exception {
      // given
      doThrow(new GlobalException(ACCESS_DENIED_EXCEPTION)).when(categoryService)
          .deleteCategory(anyLong(), any());

      // when & then
      mockMvc.perform(delete("/api/category/" + 1L).contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isForbidden())
          .andExpect(jsonPath("$.errorCode").value(ACCESS_DENIED_EXCEPTION.toString()))
          .andExpect(jsonPath("$.message").value(ACCESS_DENIED_EXCEPTION.getMessage()));
    }
  }
}



