package com.CAUCSD.MUTCHIGI.room.chat;

import com.CAUCSD.MUTCHIGI.room.Member.MemberRepository;
import com.CAUCSD.MUTCHIGI.room.RoomRepository;
import com.CAUCSD.MUTCHIGI.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class MusicChatController {

    @Autowired
    private MusicChatService musicChatService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private RoomRepository roomRepository;

    @MessageMapping("/joinRoom/{chatRoomId}")
    @SendTo("/topic/{chatRoomId}")
    public ChatEntity joinRoom(@DestinationVariable long chatRoomId, @Payload JoinMember joinMember) {
        // chatMessage에서 roomId를 가져와서 필요한 처리 수행
        System.out.println("User joined room: " + joinMember.getRoomId());

        return musicChatService.joinRoomChat(joinMember);
    }


    @MessageMapping("/send/{chatRoomId}")
    @SendTo("/topic/{chatRoomId}")
    public ChatEntity setMessage(@DestinationVariable long chatRoomId, @Payload ChatEntity chatEntity) {
        System.out.println(chatEntity.getChatMessage());
        ChatEntity chatEntity1 = new ChatEntity();
        chatEntity1.setUserName("System");
        chatEntity1.setChatMessage("확인용입니다." + chatRoomId);
        return chatEntity1;
    }
}
