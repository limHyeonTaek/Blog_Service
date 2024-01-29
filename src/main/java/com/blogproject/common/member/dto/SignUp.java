package com.blogProject.common.member.dto;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignUp {

  @NotBlank
  private String name;

  @NotBlank
  private String email;

  @NotBlank
  private String password;

  @NotBlank
  private String phoneNumber;

}
