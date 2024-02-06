package com.blogProject.common.member.api;

import com.blogProject.common.member.dto.UpdateMember;
import com.blogProject.common.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

  private final MemberService memberService;

  @PutMapping("/edit/{id}")
  public ResponseEntity<String> updateMember(@PathVariable Long id,
      @RequestBody UpdateMember request) {
    memberService.updateMember(id, request);
    return ResponseEntity.ok("성공적으로 수정되었습니다.");
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<String> deleteMember(@PathVariable Long id, Authentication authentication) {
    memberService.deleteMember(id, authentication);
    return ResponseEntity.ok("성공적으로 삭제되었습니다.");
  }


}
