package com.example.hnh.global.chat.config;

import com.example.hnh.global.chat.WebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@RequiredArgsConstructor
@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer {

    private final WebSocketHandler webSocketHandler;

    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry){

        registry
                .addHandler(webSocketHandler,"/ws/chat")
                .setAllowedOrigins("*");
    }

//    @Bean
//    public WebSocketHandler signalingSocketHandler(){
//        return new WebSocketHandler();
//    }
}
