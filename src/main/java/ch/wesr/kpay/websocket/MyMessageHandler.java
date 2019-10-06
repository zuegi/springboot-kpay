package ch.wesr.kpay.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
//@Component
public class MyMessageHandler extends TextWebSocketHandler {

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // The WebSocket has been closed
        log.info("Session closed due to {}", status);
    }
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // The WebSocket has been opened
        // I might save this session object so that I can send messages to it outside of this method
        // Let's send the first message
        log.info("WebSocket Sessions has been established {}", session.getId());
//        session.sendMessage(new TextMessage("You are now connected to the server. This is the first message."));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws Exception {
        // A message has been received
        System.out.println("Message received: " + textMessage.getPayload());
    }
}
