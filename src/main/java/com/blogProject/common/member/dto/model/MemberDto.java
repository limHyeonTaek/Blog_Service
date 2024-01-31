package com.blogProject.common.member.dto.model;


import com.blogProject.common.member.entity.Member;
import com.blogProject.common.member.entity.Role;
import java.util.Collection;
import java.util.Collections;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDto implements UserDetails {

  private String name;
  private String email;
  private String password;
  private String phoneNumber;
  private Role role;

  public static MemberDto fromEntity(Member member) {
    return MemberDto.builder()
        .name(member.getName())
        .email(member.getEmail())
        .password(member.getPassword())
        .phoneNumber(member.getPhoneNumber())
        .role(member.getRole())
        .build();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.singletonList(new SimpleGrantedAuthority(role.getKey()));
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
