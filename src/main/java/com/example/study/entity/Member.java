package com.example.study.entity;

import lombok.*;

import javax.persistence.*;

@Getter @Setter
@EqualsAndHashCode(of = "id")
@ToString(exclude = "team") // 양방향 매핑에서 꼭 제외해야 함.
@NoArgsConstructor @AllArgsConstructor
@Builder
@Entity
@Table(name = "tbl_member")

public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;
    private String userName;
    private int age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

}
