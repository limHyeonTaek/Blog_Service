package com.blogProject.common.category.service;

import static com.blogProject.exception.ErrorCode.ACCESS_DENIED_EXCEPTION;
import static com.blogProject.exception.ErrorCode.CATEGORY_NOT_FOUND;
import static com.blogProject.exception.ErrorCode.MEMBER_NOT_FOUND;
import static com.blogProject.exception.ErrorCode.MEMBER_WITHDRAWAL;

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
import com.blogProject.exception.ErrorCode;
import com.blogProject.exception.GlobalException;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CategoryService {

  private final CategoryRepository categoryRepository;
  private final CategoryConverter categoryConverter;
  private final PostRepository postRepository;
  private final MemberRepository memberRepository;

  @Transactional
  public CategoryDto createCategory(String categoryName, Authentication authentication) {
    String email = authentication.getName();
    Member member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));

    if (member.isDeleted()) {
      throw new MemberException(MEMBER_WITHDRAWAL);
    }
    if (categoryRepository.existsByName(categoryName)) {
      throw new CategoryException(ErrorCode.CATEGORY_ALREADY_EXISTS, categoryName);
    }

    Category category = new Category();
    category.setName(categoryName);
    return categoryConverter.entityToDto(categoryRepository.save(category));
  }

  public CategoryDto getCategory(Long id) {
    Category category = findCategory(id);
    return categoryConverter.entityToDto(category);
  }

  public List<CategoryDto> getAllCategory() {
    return categoryRepository.findAll().stream().map(categoryConverter::entityToDto)
        .collect(Collectors.toList());
  }

  @Transactional
  public CategoryDto updateCategory(Long id, CategoryDto categoryDto) {
    String categoryName = categoryDto.getCategoryName();
    if (categoryRepository.existsByName(categoryName)) {
      throw new CategoryException(CATEGORY_NOT_FOUND, categoryName);
    }
    Category category = findCategory(id);

    category.setName(categoryName);
    Category updatedCategory = categoryRepository.save(category);
    return categoryConverter.entityToDto(updatedCategory);
  }

  @Transactional
  public void deleteCategory(Long id, Authentication authentication) {
    Post post = postRepository.findById(id).orElse(null);
    if (post != null && !post.getMember().getEmail().equals(authentication.getName())) {
      throw new GlobalException(ACCESS_DENIED_EXCEPTION);
    }
    Category category = findCategory(id);
    removeCategoryFromPosts(id);
    categoryRepository.delete(category);
  }

  public Category findCategory(Long id) {
    return categoryRepository.findById(id)
        .orElseThrow(() -> new CategoryException(CATEGORY_NOT_FOUND));
  }

  private void removeCategoryFromPosts(Long categoryId) {
    postRepository.removeCategoryFromPosts(categoryId);
  }
}
