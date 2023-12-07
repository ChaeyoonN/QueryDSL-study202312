package com.example.study.repository;

import com.example.study.entity.Member;
import com.example.study.entity.QMember;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.example.study.entity.QMember.member;

// QueryDSL용 인터페이스의 구현체는 반드시 이름의 끝이 Impl로 끝나야 자동으로 인식되어서
// 인터페이스 타입(MemberRepository)의 객체로도 사용이 가능합니다.  :  멤버레파지토리가 커스텀을 상속받으니까 임플이 멤버레파지토리의 구현체가 된다는 말이다.
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Member> findByName(String name) {
        return queryFactory
                .selectFrom(member)
                .where(member.userName.eq(name))
                .fetch();
    }
}
