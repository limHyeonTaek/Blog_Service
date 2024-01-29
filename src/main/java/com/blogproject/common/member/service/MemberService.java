package com.blogProject.common.member.service;

import com.blogProject.common.category.exception.NameAlreadyExistsException;
import com.blogProject.common.member.converter.MemberConverter;
import com.blogProject.common.member.dto.SignUp;
import com.blogProject.common.member.entity.Member;
import com.blogProject.common.member.repository.MemberRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MemberService implements UserDetailsService {

  private final MemberRepository memberRepository;
  private final MemberConverter memberConverter;
  @Override
  public UserDetails loadUserByUsername(String memberEmail) throws UsernameNotFoundException {
    return this.memberRepository.findByEmail(memberEmail)
        .orElseThrow(() -> new UsernameNotFoundException("해당 이메일이 존재하지 않습니다. -> " + memberEmail));
  }

  public SignUp register(SignUp signUp) {
    Member member = memberConverter.dtoToEntity(signUp);
    boolean exists = memberRepository.existsByName(member.getName());
    if (exists) {
      throw new NameAlreadyExistsException("이미 존재하는 회원 입니다.");
    }

    member = memberRepository.save(member);
    return memberConverter.entityToDto(member);
  }



}
