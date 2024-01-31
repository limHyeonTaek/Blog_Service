package com.blogProject.common.member.service;

import static com.blogProject.exception.ErrorCode.MEMBER_NOT_FOUND;

import com.blogProject.common.member.dto.model.MemberDto;
import com.blogProject.common.member.entity.Member;
import com.blogProject.common.member.exception.MemberException;
import com.blogProject.common.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final MemberRepository memberRepository;

  @Override
  public UserDetails loadUserByUsername(String username) {
    Member member = memberRepository.findByEmail(username)
        .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
    return MemberDto.fromEntity(member);
  }
}
