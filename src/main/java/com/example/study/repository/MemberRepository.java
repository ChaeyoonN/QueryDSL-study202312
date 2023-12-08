package com.example.study.repository;

import com.example.study.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long>,
        MemberRepositoryCustom { // 인터페이스는 다중 상속 가능!



}
