package com.blogProject.common.member.service;


import static com.blogProject.exception.ErrorCode.ACCESS_DENIED_EXCEPTION;
import static com.blogProject.exception.ErrorCode.MEMBER_NOT_FOUND;

import com.blogProject.common.member.dto.UpdateMember;
import com.blogProject.common.member.dto.model.MemberDto;
import com.blogProject.common.member.entity.Member;
import com.blogProject.common.member.exception.MemberException;
import com.blogProject.common.member.repository.MemberRepository;
import com.blogProject.exception.GlobalException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MemberService {

  private final MemberRepository memberRepository;

  public Member findByIdOrThrow(Long id) {
    return memberRepository.findById(id).orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
  }

  @Transactional
  @PostAuthorize("isAuthenticated() " + "and returnObject.email == principal.username")
  public MemberDto updateMember(Long id, UpdateMember request) {
    Member member = findByIdOrThrow(id);
    member.setName(request.getName());
    member.setPhoneNumber(request.getPhoneNumber());
    memberRepository.save(member);
    return MemberDto.fromEntity(member);
  }

  @Transactional
  public void deleteMember(Long id, Authentication authentication) {
    Member member = findByIdOrThrow(id);
    if (!member.getEmail().equals(authentication.getName())) {
      throw new GlobalException(ACCESS_DENIED_EXCEPTION);
    }
    member.setDeleted(true);
    memberRepository.save(member);
  }

}
