package com.CAUCSD.MUTCHIGI.room;

import com.CAUCSD.MUTCHIGI.room.chat.ChatEntity;
import com.CAUCSD.MUTCHIGI.room.chat.ChatMember;
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

    @MessageMapping("/joinRoom/{chatRoomId}")
    @SendTo("/topic/{chatRoomId}")
    public ChatEntity joinRoom(ChatMember chatMember, @DestinationVariable long chatRoomId) {
        // chatMessage에서 roomId를 가져와서 필요한 처리 수행
        long roomId = chatRoomId;
        System.out.println("User joined room: " + roomId);
        // 환영 메시지 생성
        ChatEntity chatEntity = new ChatEntity();
        chatEntity.setUserName("윤도경");
        chatEntity.setChatMessage("윤도경님이 " + roomId + "번 채팅방에 들어왔습니다.");

        // 메시지를 클라이언트에게 전송
        return chatEntity; // 여기서 반환된 메시지는 @SendTo에 설정된 주제로 전송됨
    }


    @MessageMapping("/send/{chatRoomId}")
    @SendTo("/topic/{chatRoomId}")
    public ChatEntity setMessage(@DestinationVariable long chatRoomId, @Payload ChatEntity chatEntity) {
        System.out.println(chatEntity.getChatMessage());
        ChatEntity chatEntity1 = new ChatEntity();
        chatEntity1.setUserName("System");
        chatEntity1.setChatMessage("확인용입니다.");
        return chatEntity1;
    }
}
