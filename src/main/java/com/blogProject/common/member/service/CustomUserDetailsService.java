package com.blogProject.common.member.service;

import static com.blogProject.exception.ErrorCode.MEMBER_NOT_FOUND;

import com.blogProject.common.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


/**
 * Spring security의 UserDetailsService를 구현한 구현체입니다. MemberService와 달리 인증에 관한 역할이기에 클래스를 분리하였습니다.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final MemberRepository memberRepository;

  /**
   * authenticate 메소드가 실행되면, 해당 메소드를 호출하여 유저 검증 후 인증된 유저 객체를 리턴합니다.
   *
   * @param username the username identifying the user whose data is required.
   * @return 유저 인증 객체
   * @throws UsernameNotFoundException 유저 찾지 못함 예외 발생
   */
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return (UserDetails) memberRepository.findByEmail(username)
        .orElseThrow(() -> new UsernameNotFoundException(
            MEMBER_NOT_FOUND.getMessage()));
  }
}
