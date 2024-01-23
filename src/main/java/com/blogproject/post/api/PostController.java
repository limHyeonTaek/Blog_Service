package com.blogProject.post.api;

import com.blogProject.post.dto.model.PostDto;
import com.blogProject.post.service.PostService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
      @RequestParam(required = false) String categoryName) {
    PostDto newPostDto =
        (categoryName != null) ? postService.createPostWithCategory(postDto, categoryName)
            : postService.createPost(postDto);
    return new ResponseEntity<>(newPostDto, HttpStatus.OK);
  }

  // 게시판 조회 API
  @GetMapping("/{id}")
  public ResponseEntity<?> getPostById(@PathVariable Long id) {
    PostDto postDto = postService.getPostById(id);
    return new ResponseEntity<>(postDto, HttpStatus.OK);
  }

  // 게시판 조회 API(최신순)
  @GetMapping
  public ResponseEntity<?> getAllPosts() {
    List<PostDto> postDtos = postService.getAllPosts();
    return new ResponseEntity<>(postDtos, HttpStatus.OK);
  }


}