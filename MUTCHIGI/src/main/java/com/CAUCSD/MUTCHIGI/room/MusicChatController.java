package com.CAUCSD.MUTCHIGI.room;

import com.CAUCSD.MUTCHIGI.room.chat.ChatEntity;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionConnectEvent;

import java.util.UUID;

@Controller
public class MusicChatController {

    private ChatEntity chatEntity;


    @MessageMapping("/send/{chatRoomId}")
    @SendTo("/topic/{chatRoomId}")
    public ChatEntity setMessage(@DestinationVariable long chatRoomId, @Payload ChatEntity chatEntity) {
        System.out.println(chatEntity.getChatMessage());
        /*
        if (chatEntity.getChatMessage().contains("특정키워드")) {
            // 특정 작업 수행
            messagingTemplate.convertAndSend("/topic/messages/" + roomId, "특별한 응답 메시지");
        } else {
            messagingTemplate.convertAndSend("/topic/messages/" + roomId, message);
        }

         */
        ChatEntity chatEntity1 = new ChatEntity();
        chatEntity1.setUserName("System");
        chatEntity1.setChatMessage("확인용입니다.");
        return chatEntity1;
    }
}
