package com.blogProject.common.member.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMember {

  @NotBlank
  private String name;

  @NotBlank
  @Pattern(regexp = "[a-zA-Z1-9]{8,16}",
      message = "비밀번호는 영어와 숫자를 포함해서 8~16자리 입니다.")
  private String phoneNumber;

}
