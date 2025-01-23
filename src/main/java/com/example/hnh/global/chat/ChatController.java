package com.example.hnh.global.chat;

import com.example.hnh.global.chat.dto.CreateRoomRequestDto;
import com.example.hnh.global.chat.dto.CreateRoomResponseDto;
import com.example.hnh.global.chat.dto.GetRoomResponseDto;
import com.example.hnh.user.dto.CommonResponseBody;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/chats")
public class ChatController {
    private final ChatService chatService;

    @PostMapping
    public ResponseEntity<CommonResponseBody<CreateRoomResponseDto>> createRoom(@RequestBody CreateRoomRequestDto requestDto) {
        ChatRoom chatRoom = chatService.createRoom(requestDto.getName() , requestDto.getGroupId());

        return ResponseEntity.ok().body(
                new CommonResponseBody<>("채팅방 생성" ,new CreateRoomResponseDto(chatRoom)));
    }

    @GetMapping
    public List<GetRoomResponseDto> getAllRoom() {
        return chatService.getAllRoom();
    }

}