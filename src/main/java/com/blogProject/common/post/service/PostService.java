package com.blogProject.common.post.service;

import static com.blogProject.exception.ErrorCode.CATEGORY_NOT_FOUND;
import static com.blogProject.exception.ErrorCode.MEMBER_NOT_FOUND;
import static com.blogProject.exception.ErrorCode.MEMBER_WITHDRAWAL;
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
import com.blogProject.common.post.s3Uploader.S3Uploader;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class PostService {

  private final PostRepository postRepository;
  private final CategoryRepository categoryRepository;
  private final PostConverter postConverter;
  private final MemberRepository memberRepository;
  private final S3Uploader s3Uploader;

  // 게시글 생성 (Optional 사용해서 하나로 변경)
  @Transactional
  @PreAuthorize("isAuthenticated()")
  public PostDto createPost(PostDto postDto,
      Authentication authentication,
      Optional<MultipartFile> file,
      Optional<String> categoryName) throws IOException {
    Member member = getMember(authentication);
    isDeleted(member);

    Post post = postConverter.dtoToEntity(postDto, member);

    if (file.isPresent()) {
      String url = s3Uploader.upload(file.get(), "post");
      post.setImageUrl(url);
    }

    if (categoryName.isPresent()) {
      Category category = categoryRepository.findByName(categoryName.get())
          .orElseThrow(() -> new CategoryException(CATEGORY_NOT_FOUND, categoryName.get()));
      post.setCategory(category);
    }

    post = postRepository.save(post);
    return postConverter.entityToDto(post);
  }


  private static void isDeleted(Member member) {
    if (member.isDeleted()) {
      throw new MemberException(MEMBER_WITHDRAWAL);
    }
  }


  private Member getMember(Authentication authentication) {
    return memberRepository.findByEmail(authentication.getName())
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
  @PostAuthorize("isAuthenticated() and returnObject.memberName == principal.username")
  public PostDto updatePost(Long id, PostDto postDto, MultipartFile file) throws IOException {
    Post post = findPost(id);
    Category category = categoryRepository.findByName(postDto.getCategoryName()).orElse(null);
    String url = s3Uploader.upload(file, "post");
    post.setImageUrl(url);
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
    String imageUrl = post.getImageUrl();
    if (imageUrl != null && !imageUrl.isEmpty()) {
      String fileName = getFileNameFromUrl(imageUrl);
      s3Uploader.deleteFileFromS3(fileName);
    }
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

  // 파일 이름 가져오기 (URL 형식)
  public static String getFileNameFromUrl(String url) {
    try {
      URL urlObj = new URL(url);
      String path = urlObj.getPath();
      return path.substring(path.lastIndexOf("/") + 1);
    } catch (MalformedURLException e) {
      throw new IllegalArgumentException("잘못된 URL 형식입니다.", e);
    }
  }

}
