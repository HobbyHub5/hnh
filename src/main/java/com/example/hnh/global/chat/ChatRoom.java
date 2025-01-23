package com.example.hnh.global.chat;

import com.example.hnh.member.Member;
import com.example.hnh.member.MemberRepository;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.socket.WebSocketSession;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Getter
@NoArgsConstructor(force = true)
public class ChatRoom {

    private final String roomId;
    private final String name;
    private final Set<WebSocketSession> sessions = new HashSet<>();


    @Builder
    public ChatRoom (String roomId, String name) {
        this.roomId = roomId;
        this.name = name;
    }

    public void handleActions(WebSocketSession session, ChatMessage chatMessage, ChatService chatService , MemberRepository memberRepository) {
        String uriQuery  = Objects.requireNonNull(session.getUri()).getQuery();
        String userId = uriQuery.substring(uriQuery.lastIndexOf("=") +1);
        if(chatMessage.getType().equals(ChatMessage.MessageType.ENTER)){
            Optional<Member> optionalMember = memberRepository.findByUserIdAndGroupId(Long.valueOf(userId),Long.valueOf(chatMessage.getRoomId()));
            //해당 그룹 멤버에 속해있는지 확인
            if (optionalMember.isEmpty()){
                chatService.sendMessage(session, chatMessage.getSender() +"님께서는 해당그룹의 멤버가 아닙니다.");
            }else {
                // 해당 chatRoom 에 속해있는지 확인
                if (!sessions.contains(session)){
                    sessions.add(session);
                    chatMessage.setMessage(chatMessage.getSender() + "님이 입장하셨습니다.");
                    sendMessage(chatMessage.getMessage(), chatService);
                }else{
                    chatService.sendMessage(session, chatMessage.getSender() +"님께서는 이미 참여 되어있습니다.");
                }
            }
        }
        if (chatMessage.getType().equals(ChatMessage.MessageType.TALK)){
            if (sessions.contains(session)){
                String talkMessage = chatMessage.getSender() +" : " + chatMessage.getMessage();
                sendMessage(talkMessage, chatService);
            }else{
                chatService.sendMessage(session, chatMessage.getSender() +"님께서는 해당 채팅방에 참여 되어있지 않습니다.");
            }
        }
    }

    public <T> void sendMessage(T message, ChatService chatService) {
        sessions.parallelStream().forEach(session ->chatService.sendMessage(session, message));
    }

}
