package com.example.hnh.global.chat.config;

import com.example.hnh.global.chat.WebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

@RequiredArgsConstructor
@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer {

    private final WebSocketHandler webSocketHandler;

    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry){

        registry
                .addHandler(webSocketHandler,"/ws/chat")
                .setAllowedOrigins("*")
                .withSockJS()
                .setHeartbeatTime(30);

    }
    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer(){
        // WebSocket의 런타임 특성 제어
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(8192);
        container.setMaxBinaryMessageBufferSize(8192);
        return container;
    }

//    @Bean
//    public WebSocketHandler signalingSocketHandler(){
//        return new WebSocketHandler();
//    }
}
