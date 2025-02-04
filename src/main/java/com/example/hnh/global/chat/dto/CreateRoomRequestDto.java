package com.example.hnh.global.chat.dto;

import lombok.Getter;

@Getter
public class CreateRoomRequestDto {

    private String name;
    private String groupId;

    public CreateRoomRequestDto(String name , String groupId){
        this.name = name;
        this.groupId = groupId;
    }
}
