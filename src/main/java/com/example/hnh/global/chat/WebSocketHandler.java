package com.example.hnh.global.chat;


import com.example.hnh.global.error.errorcode.ErrorCode;
import com.example.hnh.global.error.exception.CustomException;
import com.example.hnh.global.util.JwtProvider;
import com.example.hnh.group.Group;
import com.example.hnh.group.GroupRepository;
import com.example.hnh.member.MemberRepository;
import com.example.hnh.user.User;
import com.example.hnh.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Objects;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class WebSocketHandler extends TextWebSocketHandler {


    private final ObjectMapper objectMapper;
    private final ChatService chatService;
    private final UserRepository userRepository;
    private final MemberRepository memberRepository;
    private final GroupRepository groupRepository;

    //웹소켓 연결
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    }

    //메시징
    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> textMessage) throws Exception {

        String payload = (String) textMessage.getPayload();
        ChatMessage chatMessage = objectMapper.readValue(payload, ChatMessage.class);

        String uriQuery  = Objects.requireNonNull(session.getUri()).getQuery();
        String userId = uriQuery.substring(uriQuery.lastIndexOf("=") +1);
        User user = userRepository.findByIdOrElseThrow(Long.valueOf(userId));
        chatMessage.setSender(user.getName());
        boolean groupExist = groupRepository.existsById(Long.valueOf(chatMessage.getRoomId()));
        Group group = groupRepository.findByGroupOrElseThrow(Long.valueOf(chatMessage.getRoomId()));
        if (groupExist) {
            chatService.createRoom(chatMessage.getRoomId(), group.getName());
        }
        Optional<ChatRoom> optionalChatRoom = chatService.getRoomById(chatMessage.getRoomId());
        if (optionalChatRoom.isEmpty()){

            chatService.sendMessage(session, "해당 그룹을 찾을수 없습니다.");
        }else {
            ChatRoom room = optionalChatRoom.get();
            room.handleActions(session, chatMessage, chatService , memberRepository);
        }
    }


    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        super.handleTransportError(session, exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
