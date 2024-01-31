package com.blogProject.common.post.api;

import com.blogProject.common.post.dto.model.PostDto;
import com.blogProject.common.post.service.PostService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/post")
public class PostController {

  private final PostService postService;

  // 게시판 생성 API
  @PostMapping
  public ResponseEntity<?> createPost(@RequestBody PostDto postDto,
      @RequestParam(required = false) String categoryName,
      @AuthenticationPrincipal UserDetails userDetails) {
    PostDto newPostDto =
        (categoryName != null) ? postService.createPostWithCategory(postDto, categoryName,
            userDetails)
            : postService.createPost(postDto, userDetails);
    return new ResponseEntity<>(newPostDto, HttpStatus.OK);
  }

  // 게시판 조회 API
  @GetMapping("/get/{id}")
  public ResponseEntity<?> getPostById(@PathVariable Long id) {
    PostDto postDto = postService.getPostById(id);
    return new ResponseEntity<>(postDto, HttpStatus.OK);
  }

  // 게시판 조회 API(최신순)
  @GetMapping("/get")
  public ResponseEntity<?> getAllPosts() {
    List<PostDto> postDtos = postService.getAllPosts();
    return new ResponseEntity<>(postDtos, HttpStatus.OK);
  }

  // 게시판 수정 API
  @PatchMapping("/{id}")
  public ResponseEntity<?> updatePost(@PathVariable Long id, @RequestBody PostDto postDto) {
    PostDto updatedPost = postService.updatePost(id, postDto);
    return new ResponseEntity<>(updatedPost, HttpStatus.OK);
  }

  // 삭제 API
  @DeleteMapping("/{id}")
  public ResponseEntity<?> deletePost(@PathVariable Long id) {
    postService.deletePost(id);
    return new ResponseEntity<>("성공적으로 삭제 되었습니다. ", HttpStatus.OK);
  }

  // 검색 API
  @GetMapping("/get/search")
  public ResponseEntity<?> searchPosts(@RequestParam String keyword) {
    List<PostDto> posts = postService.searchPosts(keyword);
    return new ResponseEntity<>(posts, HttpStatus.OK);
  }

}