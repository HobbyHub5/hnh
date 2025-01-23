package com.example.hnh.group.dto;

import com.example.hnh.group.Group;
import lombok.Getter;

@Getter
public class GroupRankingResponseDto {

    private Long groupId;        // 그룹 ID

    private String groupName;    // 그룹 이름

    private String imagePath;    // 그룹 이미지 경로

    private String userName;     // 그룹 생성자 이름

    private int interestCount;   // 그룹 관심 수



    public GroupRankingResponseDto(Long groupId, String groupName, String imagePath, String userName, int interestCount) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.imagePath = imagePath;
        this.userName = userName;
        this.interestCount = interestCount;
    }


    public static GroupRankingResponseDto toDto(Group group, String userName, int interestCount) {
        return new GroupRankingResponseDto(
                group.getId(),
                group.getName(),
                group.getImagePath(),
                userName,
                interestCount
        );
    }
}
