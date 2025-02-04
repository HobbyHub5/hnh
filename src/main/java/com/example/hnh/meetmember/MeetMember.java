package com.example.hnh.meetmember;

import com.example.hnh.global.BaseEntity;
import com.example.hnh.meet.Meet;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "meet_member")
public class MeetMember extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long memberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meet_id", nullable = false)
    private Meet meet;


    public MeetMember() {}

    public MeetMember(Long memberId, Meet meet) {
        this.memberId = memberId;
        this.meet = meet;
    }
}
