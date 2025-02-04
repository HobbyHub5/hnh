package com.example.hnh.user.dto;

import lombok.Getter;

@Getter
public class DashboardResponseDto {

    private final Long memberCount;

    private final Long boardCount;

    private final Long interestCount;

    private final Long meetCount;

    private final String groupName;

    public DashboardResponseDto(Long memberCount, Long boardCount, Long interestCount, Long meetCount, String groupName) {
        this.memberCount = memberCount;
        this.boardCount = boardCount;
        this.interestCount = interestCount;
        this.meetCount = meetCount;
        this.groupName = groupName;
    }

    public DashboardResponseDto(DashboardResponseDto dashboardResponseDto) {
        this.memberCount = dashboardResponseDto.getMemberCount();
        this.boardCount = dashboardResponseDto.getBoardCount();
        this.interestCount = dashboardResponseDto.getInterestCount();
        this.meetCount = dashboardResponseDto.getMeetCount();
        this.groupName = dashboardResponseDto.getGroupName();
    }
}
