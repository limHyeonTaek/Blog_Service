package com.blogProject.common.member.service;

import static com.blogProject.exception.ErrorCode.MEMBER_NOT_FOUND;

import com.blogProject.common.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final MemberRepository memberRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return memberRepository.findByEmail(username)
        .orElseThrow(() -> new UsernameNotFoundException(
            MEMBER_NOT_FOUND.getMessage()));
  }
}
