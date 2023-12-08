package com.example.study.repository;

import com.example.study.entity.Member;
import com.example.study.entity.QMember;
import com.example.study.entity.Team;
import com.fasterxml.jackson.databind.deser.std.StdKeyDeserializer;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.lang.model.SourceVersion;
import javax.persistence.EntityManager;

import java.util.List;

import static com.example.study.entity.QMember.*;
import static com.example.study.entity.QTeam.team;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {
    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    EntityManager em; // JPA 관리 핵심 객체

    // QueryDSL로 쿼리문을 작성하기 위한 핵심 객체
    JPAQueryFactory factory;

    @BeforeEach
    void settingObject(){
        factory = new JPAQueryFactory(em);
    }

    @Test
    void testInsertData() {

//        Team teamA = Team.builder()
//                .name("teamA")
//                .build();
//        Team teamB = Team.builder()
//                .name("teamB")
//                .build();
//
//        teamRepository.save(teamA);
//        teamRepository.save(teamB);

        Member member1 = Member.builder()
                .userName("member9")
                .age(50)
//                .team(teamA)
                .build();
        Member member2 = Member.builder()
                .userName("member10")
                .age(50)
//                .team(teamA)
                .build();
        Member member3 = Member.builder()
                .userName("member11")
                .age(30)
//                .team(teamB)
                .build();
        Member member4 = Member.builder()
                .userName("member12")
                .age(80)
//                .team(teamB)
                .build();

        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);
        memberRepository.save(member4);
    }

    @Test
    @DisplayName("testJPA")
    void testJPA () {
        List<Member> members = memberRepository.findAll();

        members.forEach(System.out::println);
    }

    @Test
    @DisplayName("textJPQL")
    void testJPQL() {
        //given
        String jpqlQuery = "SELECT m FROM Member m WHERE m.userName = :userName";

        //when

        // EntityManager를 활용하여 직접 jpql을 작성하고, 파라미터를 사용할 수 있음.
        Member foundMember = em.createQuery(jpqlQuery, Member.class)
                .setParameter("userName", "member2")
                .getSingleResult();

        //then
        Assertions.assertEquals("teamA", foundMember.getTeam().getName());

        System.out.println("\n\n\n");

        System.out.println("foundMember = " + foundMember);
        System.out.println("foundMember.getTeam() = " + foundMember.getTeam());

        System.out.println("\n\n\n");
    }

    @Test
    @DisplayName("TestQueryDSL")
    void testQueryDSL() {
        //given
//        QMember m = member; // 빌드과정에서 미리 객체 생성되있어서 바로 필드 가져올 수 있음.

        //when
        Member findMember = factory.select(member)
                .from(member)
                .where(member.userName.eq("member1"))
                .fetchOne();


        //then
        assertEquals(findMember.getUserName(), "member1");
    }

    @Test
    @DisplayName("search")
    void search() {
        //given
//        QMember m = member; // 정적 import문 추가함.
        String searchName = "member2";
        int searchAge = 20;

        //when
        Member foundMember = factory.selectFrom(member)
                .where(member.userName.eq(searchName), member.age.eq(searchAge)
//                        .and(m.age.eq(searchAge))
                ).fetchOne();


        //then
        assertNotNull(foundMember);
        assertEquals("teamA", foundMember.getTeam().getName());

        /*
         JPAQueryFactory를 이용해서 쿼리문을 조립한 후 반환 인자를 결정합니다.
         - fetchOne(): 단일 건 조회. 여러 건 조회시 예외 발생.
         - fetchFirst(): 단일 건 조회. 여러 개가 조회돼도 첫 번째 값만 반환
         - fetch(): List 형태로 반환
         * JPQL이 제공하는 모든 검색 조건을 queryDsl에서도 사용 가능
         *
         * member.userName.eq("member1") // userName = 'member1'
         * member.userName.nq("member1") // userName != 'member1'
         * member.userName.eq("member1").not() // userName != 'member1'
         * member.userName.isNotNull() // 이름이 is not null
         * member.age.in(10, 20) // age in (10,20)
         * member.age.notIn(10, 20) // age not in (10,20)
         * member.age.between(10, 30) // age between 10, 30
         * member.age.goe(30) // age >= 30
         * member.age.gt(30) // age > 30
         * member.age.loe(30) // age <= 30
         * member.age.lt(30) // age < 30
         * member.userName.like("_김%") // userName LIKE '_김%'
         * member.userName.contains("김") // userName LIKE '%김%'
         * member.userName.startsWith("김") // userName LIKE '김%'
         * member.userName.endsWith("김") // userName LIKE '%김'
         */

    }

    @Test
    @DisplayName("결과 반환하기")
    void testFetchResult() {
        // fetch
        List<Member> fetch1 = factory.selectFrom(member).fetch();

        System.out.println("\n\n============ fetch ==============");
        fetch1.forEach(System.out::println);

        // fetchOne
        Member fetch2 = factory.selectFrom(member)
                .where(member.id.eq(3L))
                .fetchOne();

        System.out.println("\n\n=========== fetch2 ===============");
        System.out.println("fetch2 = " + fetch2);

        // fetchFirst
        Member fetch3 = factory.selectFrom(member)
                .fetchFirst();

        System.out.println("\n\n========== fetchFirst ==========");
        System.out.println("fetch3 = " + fetch3);




    }

    @Test
    @DisplayName("QueryDSL custom 설정 확인")
    void QueryDslCustom() {
        //given
        String name = "member4";

        //when
        List<Member> result = memberRepository.findByName(name);

        //then
        assertEquals(1, result.size());
        assertEquals("teamB", result.get(0).getTeam().getName());
    }

    @Test
    @DisplayName("회원 정렬 조회")
    void sort() {
        //given

        //when
        List<Member> result = factory.selectFrom(member)
//                .where(원하는 조건)
                .orderBy(member.age.desc())
                .fetch();
        //then
        assertEquals(result.size(), 12);

        System.out.println("\n\n\n");
        result.forEach(System.out::println);
        System.out.println("\n\n\n");
    }

    @Test
    @DisplayName("queryDSL paging")
    void paging() {
        //given

        //when
        List<Member> result = factory.selectFrom(member)
                .orderBy(member.userName.desc())
                .offset(3) // 시작지점: 4번째행. querydsl에서 offset은 1번행이 0이다.
                .limit(3) // 페이지 사이즈 : 3개
                //4번째행부터 3개 가져오기
                .fetch();

        //then
        assertEquals(result.size(), 3);
        assertEquals(result.get(2).getUserName(), "member3");

    }
    @Test
    @DisplayName("그룹 함수의 종류")
    void aggregation() {
        //given

        //when
        List<Tuple> result = factory.select(
                        member.count(),
                        member.age.sum(),
                        member.age.avg(),
                        member.age.max(),
                        member.age.min()
                ) // 직접 조회할 컬럼 나누기 위해 select만 부름.
                .from(member)
                .fetch(); // result.size()는 하나고, 그 안에 튜플이 있다.

        Tuple tuple = result.get(0); // 리스트에서 1번째 값 꺼내기

        //then
        assertEquals(tuple.get(member.count()), 8);
        assertEquals(tuple.get(member.age.sum()), 360);
        assertEquals(tuple.get(member.age.avg()), 45);
        assertEquals(tuple.get(member.age.min()), 10);
        assertEquals(tuple.get(member.age.max()), 80);

        System.out.println("\n\n\n");

        System.out.println("result = " + result);
        System.out.println("result의 크기 = " + result.size());
        System.out.println("tuple = " + tuple);

        System.out.println("\n\n\n");
    }

    @Test
    @DisplayName("GROUP BY, HAVING")
    void testGroupBy() {
        //given

        //when
        List<Long> result = factory.select(member.age.count())
                .from(member)
                .groupBy(member.age)
                .having(member.age.count().goe(2)) // 같은 나이 개수가 2개 이상인 것을 그룹화
                .orderBy(member.age.asc())
                .fetch();

        //then
        assertEquals(result.size(), 3);
        System.out.println("\n\n\n");
        result.forEach(System.out::println); // 2 3 2 출력됨. 2개 3개 2개 의미.
        System.out.println("\n\n\n");
    }

    @Test
    @DisplayName("join 해보기")
    void join() {
        //given

        // Oracle DB의 경우 Oracle의 조인 문법도 사용이 가능하다.
        // SELECT * FROM employees, departments WHERE ~~~
        // select().from(employees, departments).where(~~~)

        //when
        List<Member> result = factory.selectFrom(member)
                // join(기준 entity.조인 대상, 별칭)
                .join(member.team, team) // 이너조인
                .where(team.name.eq("teamA"))
                .fetch(); // 팀에이 인 멤버 조회

        //then
        System.out.println("\n\n\n");
        result.forEach(System.out::println);
        System.out.println("\n\n\n");
    }

    /*
    ex) 회원과 팀을 조인하면서, 팀 이름이 teamA인 팀만 조회, 회원은 모두 조회.
    SQL: SELECT m.*, t.* FROM tbl_member m LEFT JOIN tbl_team t ON m.team_id = t.team_id AND t.name = 'teamA';
    JPQL: SELECT m, t FROM Member m LEFT JOIN m.team t ON t.name = 'teamA'
     */
    @Test
    @DisplayName("left outer join 테스트")
    void leftJoinTest() {
        //given

        //when
        List<Tuple> result = factory.select(member, team)
                .from(member)
                .leftJoin(member.team, team)
                .on(team.name.eq("teamA"))
                .fetch();


        //then
        System.out.println("\n\n\n");
        result.forEach(tuple -> System.out.println("tuple = " + tuple));
        System.out.println("\n\n\n");
    }
    
    @Test
    @DisplayName("sub query 사용하기(나이가 가장 많은 회원을 조회)")
    void subQueryTest() {
        //given
        // 같은 테이블에서 서브쿼리를 적용하려면 별도로 Qclass의 객체를 생성해야 합니다.
        QMember memberSub = new QMember("memberSub");

        //when
        List<Member> result = factory.selectFrom(member)
                .where(member.age.eq(
                        // 나이가 가장 많은 사람을 조회하는 서브쿼리문
                        JPAExpressions // 서브쿼리를 사용할 수 있게 해 주는 클래스
                                .select(memberSub.age.max())
                                .from(memberSub)
                )).fetch();

        //then
        System.out.println("\n\n\n");
        result.forEach(System.out::println);
        System.out.println("\n\n\n");
    }

    @Test
    @DisplayName("나이가 평균 나이 이상인 회원을 조회")
    void subQueryGoe() {
        //given
        QMember m2 = new QMember("m2");
        //when
        List<Member> result = factory.selectFrom(member)
                .where(member.age.goe(
                        // JPAExpressions는 from절을 제외하고, select와 where절에서 사용이 가능.
                        // JPQL도 마찬가지로 from절 서브쿼리 사용 불가
                        // -> Native SQL을 작성하던지, MyBatis or JDBCTemplate 이용, 따로따로 두 번 조회도 사용.
                        JPAExpressions
                                .select(m2.age.avg())
                                .from(m2)
                ))
                .fetch();

        //then
        assertEquals(result.size(), 7);
    }

    @Test
    @DisplayName("동적 sql 테스트")
    void dynamicQueryTest() {
        //given
        String name = null;
        int age = 30;

        //when
        List<Member> result = memberRepository.findUser(name, null);

        //then
        assertEquals(result.size(), 12);

        System.out.println("\n\n\n");
        result.forEach(System.out::println);
        System.out.println("\n\n\n");
    }



}