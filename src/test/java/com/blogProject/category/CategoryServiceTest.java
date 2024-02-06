package com.blogProject.category;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import com.blogProject.common.category.repository.CategoryRepository;
import com.blogProject.common.category.service.CategoryService;
import com.blogProject.common.member.entity.Member;
import com.blogProject.common.member.repository.MemberRepository;
import com.blogProject.common.post.entity.Post;
import com.blogProject.common.post.repository.PostRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
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

  @Test
  @DisplayName("카테고리 생성 테스트")
  void createCategory() {
    // given
    String categoryName = "category";
    Authentication authentication = mock(Authentication.class);
    when(authentication.getName()).thenReturn("test@test.com");
    Member member = Member.builder()
        .email("test@test.com")
        .isDeleted(false)
        .build();
    when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(member));
    when(categoryRepository.existsByName(anyString())).thenReturn(false);
    Category category = new Category();
    category.setName(categoryName);
    CategoryDto categoryDto = new CategoryDto();
    categoryDto.setCategoryName(categoryName);
    when(categoryConverter.entityToDto(any(Category.class))).thenReturn(categoryDto);
    when(categoryRepository.save(any(Category.class))).thenReturn(category);

    // when
    CategoryDto result = categoryService.createCategory(categoryName, authentication);

    // then
    assertEquals(categoryName, result.getCategoryName());
  }

  @Test
  @DisplayName("카테고리 보기 테스트")
  void getCategory() {
    // given
    Category category = Category.builder()
        .id(1L)
        .name("category1")
        .build();
    CategoryDto categoryDto = new CategoryDto();
    categoryDto.setCategoryName("category");
    when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
    when(categoryConverter.entityToDto(any(Category.class))).thenReturn(categoryDto);

    // when
    CategoryDto result = categoryService.getCategory(1L);

    // then
    assertEquals("category", result.getCategoryName());
  }

  @Test
  @DisplayName("카테고리 전체 보기")
  void getAllCategory() {
    // given
    Category category1 = Category.builder()
        .id(1L)
        .name("category1")
        .build();
    Category category2 = Category.builder()
        .id(1L)
        .name("category2")
        .build();
    List<Category> categories = Arrays.asList(category1, category2);
    CategoryDto categoryDto1 = new CategoryDto();
    categoryDto1.setCategoryName("category1");
    CategoryDto categoryDto2 = new CategoryDto();
    categoryDto2.setCategoryName("category2");
    when(categoryRepository.findAll()).thenReturn(categories);
    when(categoryConverter.entityToDto(any(Category.class))).thenReturn(categoryDto1);

    // when
    List<CategoryDto> result = categoryService.getAllCategory();

    // then
    assertEquals(2, result.size());
  }

  @Test
  @DisplayName("카테고리 수정 테스트")
  void updateCategory() {
    // given
    Category category = Category.builder()
        .id(1L)
        .name("category1")
        .build();
    CategoryDto categoryDto = new CategoryDto();
    categoryDto.setCategoryName("updatedCategory");
    when(categoryRepository.existsByName(anyString())).thenReturn(false);
    when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
    when(categoryConverter.entityToDto(any(Category.class))).thenReturn(categoryDto);
    when(categoryRepository.save(any(Category.class))).thenReturn(category);

    // when
    CategoryDto result = categoryService.updateCategory(1L, categoryDto);

    // then
    assertEquals("updatedCategory", result.getCategoryName());
  }

  @Test
  @DisplayName("카테고리 삭제 테스트")
  void deleteCategory() {
    // given
    Category category = Category.builder()
        .id(1L)
        .name("category1")
        .build();

    Member member = Member.builder()
        .email("test@test.com")
        .build();

    Post post = Post.builder()
        .member(member)
        .build();

    Authentication mockAuth = mock(Authentication.class);
    when(mockAuth.getName()).thenReturn("test@test.com");

    when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
    when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
    doNothing().when(categoryRepository).delete(any(Category.class));

    // when
    categoryService.deleteCategory(1L, mockAuth);

    // then
    verify(categoryRepository, times(1)).delete(any(Category.class));
  }

}

