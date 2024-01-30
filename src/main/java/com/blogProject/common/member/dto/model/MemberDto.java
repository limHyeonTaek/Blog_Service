package com.blogProject.common.member.dto.model;


import com.blogProject.common.member.entity.Member;
import com.blogProject.common.member.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDto {

  private String name;
  private String email;
  private Role role;

  public static MemberDto fromEntity(Member member) {
    return MemberDto.builder()
        .name(member.getName())
        .email(member.getEmail())
        .role(member.getRole())
        .build();
  }
}
