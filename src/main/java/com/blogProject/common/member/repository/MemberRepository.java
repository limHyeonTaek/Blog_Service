package com.blogProject.common.member.repository;

import com.blogProject.common.member.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

  Optional<Member> findByEmail(String email);

  boolean existsByEmail(String email);

  boolean existsByName(String name);

  boolean existsByPhoneNumber(String phoneNumber);

}
