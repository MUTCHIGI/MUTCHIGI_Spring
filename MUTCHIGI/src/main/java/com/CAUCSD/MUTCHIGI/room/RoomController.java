package com.CAUCSD.MUTCHIGI.room;

import com.CAUCSD.MUTCHIGI.room.chat.ChatEntity;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionConnectEvent;

import java.util.UUID;

@Controller
public class RoomController {

    private final SimpMessagingTemplate messagingTemplate;

    public RoomController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        // UUID를 사용하여 roomId 생성
        String roomId = UUID.randomUUID().toString();

        // 클라이언트에게 roomId 전송 (적절한 경로로)
        messagingTemplate.convertAndSend("/topic/roomId", roomId);

        System.out.println("New room created with ID: " + roomId);
    }

    @MessageMapping("/send/{roomId}")
    public void setMessage(String message, @DestinationVariable String roomId) {
        System.out.println(message);
        if (message.contains("특정키워드")) {
            // 특정 작업 수행
            messagingTemplate.convertAndSend("/topic/messages/" + roomId, "특별한 응답 메시지");
        } else {
            messagingTemplate.convertAndSend("/topic/messages/" + roomId, message);
        }

    }
}
