package com.example.hnh.global.chat.dto;

import com.example.hnh.global.chat.ChatRoom;
import lombok.Getter;

@Getter
public class CreateRoomResponseDto {

    private String roomId;

    private String name;


    public CreateRoomResponseDto(ChatRoom chatRoom){
        this.roomId = chatRoom.getRoomId();
        this.name = chatRoom.getName();
    }
}
