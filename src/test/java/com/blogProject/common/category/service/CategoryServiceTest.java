package com.blogProject.common.category.service;

import static com.blogProject.constant.CategoryConstants.CATEGORY_NAME;
import static com.blogProject.constant.CategoryConstants.UPDATE_CATEGORY_NAME;
import static com.blogProject.constant.MemberConstants.EMAIL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.blogProject.common.category.converter.CategoryConverter;
import com.blogProject.common.category.dto.CategoryDto;
import com.blogProject.common.category.entity.Category;
import com.blogProject.common.category.exception.CategoryException;
import com.blogProject.common.category.repository.CategoryRepository;
import com.blogProject.common.member.entity.Member;
import com.blogProject.common.member.exception.MemberException;
import com.blogProject.common.member.repository.MemberRepository;
import com.blogProject.common.post.entity.Post;
import com.blogProject.common.post.repository.PostRepository;
import com.blogProject.exception.GlobalException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

  @Mock
  private CategoryRepository categoryRepository;

  @Mock
  private CategoryConverter categoryConverter;

  @Mock
  private PostRepository postRepository;

  @Mock
  private MemberRepository memberRepository;

  @InjectMocks
  private CategoryService categoryService;

  private Category category;
  private CategoryDto categoryDto;
  private Member member;
  private Authentication authentication;
  private Post post;

  @BeforeEach
  public void setUp() {
    category = Category.builder().id(1L).name(CATEGORY_NAME).build();
    categoryDto = new CategoryDto();
    categoryDto.setCategoryName(CATEGORY_NAME);
    member = Member.builder().email(EMAIL).build();
    post = Post.builder().member(member).build();
    authentication = mock(Authentication.class);
  }

  @Nested
  @DisplayName("카테고리 Service 테스트")
  class CategoryCRUDTest {

    @Test
    @DisplayName("카테고리 생성 테스트")
    void createCategory() {
      // given
      when(authentication.getName()).thenReturn(EMAIL);
      when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(member));
      when(categoryRepository.existsByName(anyString())).thenReturn(false);
      when(categoryConverter.entityToDto(any(Category.class))).thenReturn(categoryDto);
      when(categoryRepository.save(any(Category.class))).thenReturn(category);

      // when
      CategoryDto result = categoryService.createCategory(CATEGORY_NAME, authentication);

      // then
      assertEquals(CATEGORY_NAME, result.getCategoryName());
    }

    @Test
    @DisplayName("카테고리 보기 테스트")
    void getCategory() {
      // given
      when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
      when(categoryConverter.entityToDto(any(Category.class))).thenReturn(categoryDto);

      // when
      CategoryDto result = categoryService.getCategory(1L);

      // then
      assertEquals(CATEGORY_NAME, result.getCategoryName());
    }

    @Test
    @DisplayName("카테고리 전체 보기 테스트")
    void getAllCategory() {
      // given
      List<Category> categories = Arrays.asList(category, category);
      when(categoryRepository.findAll()).thenReturn(categories);
      when(categoryConverter.entityToDto(any(Category.class))).thenReturn(categoryDto);

      // when
      List<CategoryDto> result = categoryService.getAllCategory();

      // then
      assertEquals(2, result.size());
      assertEquals(CATEGORY_NAME, result.get(0).getCategoryName());
      assertEquals(CATEGORY_NAME, result.get(1).getCategoryName());
    }

    @Test
    @DisplayName("카테고리 수정 테스트")
    void updateCategory() {
      // given
      CategoryDto updatedCategoryDto = new CategoryDto();
      updatedCategoryDto.setCategoryName(UPDATE_CATEGORY_NAME);
      when(categoryRepository.existsByName(anyString())).thenReturn(false);
      when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
      when(categoryConverter.entityToDto(any(Category.class))).thenReturn(updatedCategoryDto);
      when(categoryRepository.save(any(Category.class))).thenReturn(category);

      // when
      CategoryDto result = categoryService.updateCategory(1L, updatedCategoryDto);

      // then
      assertEquals(UPDATE_CATEGORY_NAME, result.getCategoryName());
    }

    @Test
    @DisplayName("카테고리 삭제 테스트")
    void deleteCategory() {
      // given
      when(authentication.getName()).thenReturn(EMAIL);
      when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
      when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
      doNothing().when(categoryRepository).delete(any(Category.class));

      // when
      categoryService.deleteCategory(1L, authentication);

      // then
      verify(categoryRepository, times(1)).delete(any(Category.class));
    }
  }

  @Nested
  @DisplayName("카테고리 Service Exception 테스트")
  class CategoryExceptionTest {

    @Test
    @DisplayName("카테고리 생성 실패 테스트 - 이미 삭제된 회원")
    void createCategory_withDeletedMember() {
      // given
      when(authentication.getName()).thenReturn(EMAIL);
      Member deletedMember = Member.builder().email(EMAIL).isDeleted(true).build();
      when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(deletedMember));

      // then
      assertThrows(MemberException.class, () -> {
        categoryService.createCategory(CATEGORY_NAME, authentication);
      });
    }


    @Test
    @DisplayName("카테고리 생성 실패 테스트 - 이미 존재하는 카테고리 이름")
    void createCategory_withDuplicateName() {
      // given
      when(authentication.getName()).thenReturn(EMAIL);
      when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(member));
      when(categoryRepository.existsByName(anyString())).thenReturn(true);

      // then
      assertThrows(CategoryException.class, () -> {
        categoryService.createCategory(CATEGORY_NAME, authentication);
      });
    }


    @Test
    @DisplayName("카테고리 수정 실패 테스트 - 이미 존재하는 카테고리 이름")
    void updateCategory_withDuplicateName() {
      // given
      when(categoryRepository.existsByName(anyString())).thenReturn(true);

      // then
      assertThrows(CategoryException.class, () -> {
        categoryService.updateCategory(1L, categoryDto);
      });
    }

    @Test
    @DisplayName("카테고리 삭제 실패 테스트 - 삭제 권한 없음")
    void deleteCategory_noPermission() {
      // given
      when(authentication.getName()).thenReturn("differentEmail");
      when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

      // then
      assertThrows(GlobalException.class, () -> {
        categoryService.deleteCategory(1L, authentication);
      });
    }
  }
}
