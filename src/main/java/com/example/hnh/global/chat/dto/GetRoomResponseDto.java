package com.example.hnh.global.chat.dto;

import com.example.hnh.global.chat.ChatRoom;
import lombok.Getter;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
public class GetRoomResponseDto {

    private String roomId;

    private String name;

    private List<String> sessionId;

    public GetRoomResponseDto(ChatRoom chatRoom){
        this.roomId = chatRoom.getRoomId();
        this.name = chatRoom.getName();

        this.sessionId = new ArrayList<>();
        Set<WebSocketSession> sessions = chatRoom.getSessions();
        for (WebSocketSession s : sessions) {
            sessionId.add(s.getId());
        }
    }
}
