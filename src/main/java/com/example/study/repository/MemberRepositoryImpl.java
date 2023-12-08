package com.example.study.repository;

import com.example.study.entity.Member;
import com.example.study.entity.QMember;
import com.querydsl.core.types.dsl.BooleanExpression;
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

    // WHERE절에 BooleanExpression을 리턴하는 메서드를 사용합니다.
    // nameEq, ageEq에서는 값이 없다면 null을 리턴하고, 그렇지 않을 셩우 논리 표현식 결과를 리턴합니다.
    // WHERE절에서는 null값인 경우 조건을 건너 뜁니다.(쿼리를 완성하지 않음)
    // 다 null이면 where절 동작하지 않으므로 전체 조회된다.
    @Override
    public List<Member> findUser(String nameParam, Integer ageParam) { // 객체타입으로 선언하면 null도 받을 수 있다.
        return queryFactory
                .selectFrom(member)
                .where(nameEq(nameParam),
                        ageEq(ageParam))
                .fetch();
    }

    private BooleanExpression ageEq(Integer ageParam){
        return ageParam != null ? member.age.eq(ageParam) : null;
    }
    // 조건식을 메서드화시켜서 null이 온다면 자리를 비우도록 한다.
    private BooleanExpression nameEq(String nameParam){
//        if(nameParam != null){
//            return member.userName.eq(nameParam);
//        }
//        return null;
        return nameParam != null ? member.userName.eq(nameParam) : null;

    }


}
