package com.blogProject.common.member.dto;

import com.blogProject.common.member.dto.model.MemberDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class SigninResponse {

  private String token;
  private MemberDto memberDto;

}
