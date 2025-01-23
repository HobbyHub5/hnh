package com.example.hnh.board;

import com.example.hnh.comment.Comment;
import com.example.hnh.global.BaseEntity;
import com.example.hnh.group.Group;
import com.example.hnh.member.Member;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "board")
public class Board extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(name = "imagepath")
    private String imagePath;

    private String detail;

    private Long view;

    private Long likeCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "board")
    private List<Comment> comments = new ArrayList<>();

    public Board() {}

    public Board(String title, String imagePath, String detail, Long view, Long likeCount, Group group, Member member) {
        this.title = title;
        this.imagePath = imagePath;
        this.detail = detail;
        this.view = view;
        this.likeCount = likeCount;
        this.group = group;
        this.member = member;
    }

    public void updateBoard(String title, String imagePath, String detail) {
        this.title = title;
        this.imagePath = imagePath;
        this.detail = detail;
    }

    public void setView(Long view) {
        this.view = view;
    }
}
