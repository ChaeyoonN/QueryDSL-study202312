package com.example.study.entity;

import lombok.*;

import javax.persistence.*;
import java.lang.reflect.GenericArrayType;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@ToString(exclude = "members") // 양방향 매핑에서 꼭 제외해야 함.
@EqualsAndHashCode(of = "id")
@NoArgsConstructor @AllArgsConstructor
@Builder
@Entity
@Table(name = "tbl_team")
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_id")
    private Long id;
    private String name;

    @OneToMany(mappedBy = "team")
    @Builder.Default
    private List<Member> members = new ArrayList<>();


}
