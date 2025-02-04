package com.example.hnh.group;

import com.example.hnh.board.Board;
import com.example.hnh.global.BaseEntity;
import com.example.hnh.interestgroup.InterestGroup;
import com.example.hnh.meet.Meet;
import com.example.hnh.member.Member;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "`group`")
public class Group extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long categoryId;

    @Column(nullable = false)
    private String name;

    @Column(name = "imagepath")
    private String imagePath;

    @Column(nullable = false)
    private String detail;

    @OneToMany(mappedBy = "group")
    private List<Board> boards = new ArrayList<>();

    @OneToMany(mappedBy = "group")
    private List<Meet> meets = new ArrayList<>();

    @OneToMany(mappedBy = "group")
    private List<Member> members = new ArrayList<>();

    @OneToMany(mappedBy = "group")
    private List<InterestGroup> interestGroups = new ArrayList<>();


    public Group() {}

    public Group(Long userId, Long categoryId, String name, String imagePath, String detail) {
        this.userId = userId;
        this.categoryId = categoryId;
        this.name = name;
        this.imagePath = imagePath;
        this.detail = detail;
    }

    // 그룹 수정 메서드
    public void updateGroup(String name, String detail, String imagePath) {
        this.name = name;
        this.detail = detail;
        this.imagePath = imagePath;
    }
}
