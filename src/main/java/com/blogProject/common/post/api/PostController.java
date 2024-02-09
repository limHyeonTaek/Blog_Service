package com.blogProject.common.post.api;

import com.blogProject.common.post.dto.model.PostDto;
import com.blogProject.common.post.service.PostService;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/post")
public class PostController {

  private final PostService postService;

  // 게시판 생성 API
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<PostDto> createPost(
      @RequestParam("title") String title,
      @RequestParam("contents") String contents,
      @RequestParam(required = false) String categoryName,
      @RequestParam(name = "file", required = false) MultipartFile file,
      Authentication authentication) throws IOException {
    PostDto postDto = PostDto.builder()
        .title(title)
        .contents(contents)
        .build();
    PostDto newPostDto = postService.createPost(postDto, authentication, Optional.ofNullable(file),
        Optional.ofNullable(categoryName));
    return ResponseEntity.status(HttpStatus.CREATED).body(newPostDto);
  }


  // 게시글 조회 API
  @GetMapping("/get/{id}")
  public ResponseEntity<PostDto> getPostById(@PathVariable Long id) {
    PostDto postDto = postService.getPostById(id);
    return ResponseEntity.ok(postDto);
  }

  // 게시글 전체 조회(최신순) API
  @GetMapping("/get")
  public ResponseEntity<Page<PostDto>> getAllPosts(Pageable pageable) {
    Page<PostDto> postDtos = postService.getAllPosts(pageable);
    return ResponseEntity.ok(postDtos);
  }


  // 게시글 수정 API
  @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<PostDto> updatePost(@PathVariable Long id,
      @RequestParam("title") String title,
      @RequestParam("contents") String contents,
      @RequestParam("file") MultipartFile file) throws IOException {
    PostDto postDto = PostDto.builder()
        .title(title)
        .contents(contents)
        .build();

    PostDto updatedPost = postService.updatePost(id, postDto, file);
    return ResponseEntity.ok(updatedPost);
  }


  // 게시글 삭제 API
  @DeleteMapping("/{id}")
  public ResponseEntity<String> deletePost(@PathVariable Long id) {
    postService.deletePost(id);
    return ResponseEntity.ok("성공적으로 삭제되었습니다.");
  }

  // 게시글 검색 (본문 + 제목) API
  @GetMapping("/get/search")
  public ResponseEntity<Page<PostDto>> searchPosts(@RequestParam String keyword,
      Pageable pageable) {
    Page<PostDto> posts = postService.searchPosts(keyword, pageable);
    return ResponseEntity.ok(posts);
  }

}