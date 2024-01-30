package com.blogProject.common.member.entity;

import static com.blogProject.exception.ErrorCode.INVALID_REQUEST;

import com.blogProject.common.member.exception.MemberException;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public enum Role {
  USER("ROLE_USER"),
  ADMIN("ROLE_ADMIN");

  private final String key;

  public static Role fromKey(String key) {
    return Arrays.stream(values())
        .filter(o -> o.getKey().equals(key))
        .findFirst()
        .orElseThrow(() -> new MemberException(
            INVALID_REQUEST));
  }
}
