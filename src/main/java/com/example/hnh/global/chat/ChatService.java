package com.example.hnh.global.chat;


import com.example.hnh.global.chat.dto.GetRoomResponseDto;
import com.example.hnh.group.GroupService;
import com.example.hnh.group.dto.GroupRankingResponseDto;
import com.example.hnh.group.dto.GroupResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatService {

    private final ObjectMapper objectMapper;
    private Map<String, ChatRoom> chatRooms;
    private final GroupService groupService;

    @PostConstruct
    private void init() {
        chatRooms = new LinkedHashMap<>();
        // 1.전체 그룹 조회
        List<GroupRankingResponseDto> groupList =  groupService.findAllGroupsWithRanking();
        // 2.각 그룹id 으로 createRoom
        groupList.forEach(g -> createRoom(String.valueOf(g.getGroupId()) , g.getGroupName()));
    }

    public List<GetRoomResponseDto> getAllRoom() {

        List<GetRoomResponseDto> rooms = new ArrayList<>();
        Set<String> roomId = chatRooms.keySet();
        for (String s : roomId){
            rooms.add(new GetRoomResponseDto(chatRooms.get(s)));
        }
        return rooms;
    }


    public ChatRoom getRoomById(String roomId) {
        return chatRooms.get(roomId);
    }


    public ChatRoom createRoom(String groupId , String name ) {
//        String randomId = UUID.randomUUID().toString();
        ChatRoom chatRoom = ChatRoom.builder()
                .roomId(groupId)
                .name(name)
                .build();
        chatRooms.put(groupId, chatRoom);
        return chatRoom;
    }

    public <T> void sendMessage(WebSocketSession session, T message) {
        try {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

}
