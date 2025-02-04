package com.example.hnh.global.chat;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ChatMessage {
    public enum MessageType {
        ENTER, TALK , EXIT , BAN, REJOIN
    }
    private MessageType type;
    private String roomId;
    private String sender;
    private String message;
}
