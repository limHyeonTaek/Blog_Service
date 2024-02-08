package com.blogProject.common.member.dto;


import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.Builder;

@Builder
public record Signin(
    @NotBlank
    @Email(message = "이메일 형식으로 입력해 주세요.")
    String email,

    @NotBlank
    @Pattern(regexp = "[a-zA-Z1-9]{8,16}",
        message = "비밀번호는 영어와 숫자를 포함해서 8~16자리 입니다.")
    String password
) {

}
