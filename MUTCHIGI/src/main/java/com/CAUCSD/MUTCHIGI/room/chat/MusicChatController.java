package com.CAUCSD.MUTCHIGI.room.chat;

import com.CAUCSD.MUTCHIGI.quizSong.hint.HintAllDTO;
import com.CAUCSD.MUTCHIGI.quizSong.hint.HintDTO;
import com.CAUCSD.MUTCHIGI.room.Member.MemberRepository;
import com.CAUCSD.MUTCHIGI.room.RoomRepository;
import com.CAUCSD.MUTCHIGI.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.List;

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
    public SendChatDTO joinRoom(@DestinationVariable long chatRoomId, @Payload JoinMemberDTO joinMemberDTO) {
        // chatMessage에서 roomId를 가져와서 필요한 처리 수행
        System.out.println("User joined room: " + joinMemberDTO.getRoomId());

        return musicChatService.joinRoomChat(joinMemberDTO);
    }


    @MessageMapping("/send/{chatRoomId}")
    @SendTo("/topic/{chatRoomId}")
    public SendChatDTO setMessage(@DestinationVariable long chatRoomId, @Payload ReceiveChatDTO receiveChatDTO) {
        System.out.println(receiveChatDTO.getChatMessage());
        return musicChatService.setMessageChat(chatRoomId, receiveChatDTO);

    }

    @MessageMapping("/getSong/{chatRoomId}/{songIndex}")
    @SendTo("/topic/song/{chatRoomId}")
    public SendNextSongDTO getNextSong(
            @DestinationVariable long chatRoomId ,
            @DestinationVariable int songIndex
    ){
        System.out.println("songIndex : " + songIndex);
        return musicChatService.getNextSongAndStart(chatRoomId, songIndex);
    }

    @MessageMapping("/skipVote/{chatRoomId}")
    @SendTo("/topic/vote/{chatRoomId}")
    public VoteDTO VoteSkip(
            @DestinationVariable long chatRoomId,
            @Payload VoteDTO voteDTO
    ){
        return musicChatService.VoteToSkip(chatRoomId,voteDTO);
    }

    @MessageMapping("/getHint/{chatRoomId}/{qsRelationId}")
    @SendTo("/topic/hint/{chatRoomId}")
    public List<HintAllDTO> getHint(
            @DestinationVariable long chatRoomId,
            @DestinationVariable long qsRelationId
    ){
        return musicChatService.getHintFromDB(chatRoomId,qsRelationId);
    }

    @MessageMapping("/kickMember/{chatRoomId}/{userId}")
    @SendTo("/topic/kick/{chatRoomId}")
    public KickedUserDTO kickMember(
            @DestinationVariable long chatRoomId,
            @DestinationVariable long userId
    ){
        return musicChatService.kickMember(chatRoomId,userId);
    }
}
