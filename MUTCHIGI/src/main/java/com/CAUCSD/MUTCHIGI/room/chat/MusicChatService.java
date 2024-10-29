package com.CAUCSD.MUTCHIGI.room.chat;

import com.CAUCSD.MUTCHIGI.room.Member.MemberEntity;
import com.CAUCSD.MUTCHIGI.room.Member.MemberRepository;
import com.CAUCSD.MUTCHIGI.room.Member.RoomAuthority;
import com.CAUCSD.MUTCHIGI.room.RoomEntity;
import com.CAUCSD.MUTCHIGI.room.RoomRepository;
import com.CAUCSD.MUTCHIGI.user.UserEntity;
import com.CAUCSD.MUTCHIGI.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class MusicChatService {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    public MusicChatService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public ChatEntity joinRoomChat(JoinMember joinMember) {
        String errorDestination = "/userDisconnect/" + joinMember.getUserId() + "/queue/errors";
        System.out.println(errorDestination);

        RoomEntity roomEntity = roomRepository.findById(joinMember.getRoomId()).orElse(null);
        if (roomEntity == null) {
            System.out.println("Room not found");
            messagingTemplate.convertAndSend(errorDestination, "ROOM_NOT_FOUND");
            return null;
        }
        if(!roomEntity.isPublicRoom()){ // 비공개방인 경우
            if(!roomEntity.getPassword().equals(joinMember.getRoomPassword())) { // 비밀번호 대조해보고 다르면
                System.out.println("Password does not match" + joinMember.getRoomPassword() + "is not Equals " + roomEntity.getPassword());
                messagingTemplate.convertAndSend(errorDestination, "INVALID_PASSWORD");
                return null;
            }
        }

        ChatEntity chatEntity = new ChatEntity();
        chatEntity.setUserName("[System]");

        UserEntity userEntity = userRepository.findById(joinMember.getUserId()).orElse(null);
        if(userEntity == null){
            chatEntity.setUserName("[Warning]");
            chatEntity.setChatMessage("저장되지 않은 User의 입장입니다.");
        }else{
            if(memberRepository.findByRoomEntity_RoomIdAndUserEntity_UserId(joinMember.getRoomId(), joinMember.getUserId()).isEmpty()){
                MemberEntity memberEntity = new MemberEntity();
                memberEntity.setUserEntity(userEntity);
                memberEntity.setRoomEntity(roomRepository.findById(joinMember.getRoomId()).orElse(null));
                if(memberRepository.findByRoomEntity_RoomId(joinMember.getRoomId()).isEmpty()){
                    memberEntity.setRoomAuthority(RoomAuthority.FIRST); // 방에 아무도 없으면 방장으로 임명
                    chatEntity.setChatMessage(userEntity.getName()+"님이 방장으로 " + joinMember.getRoomId() + "번 방에 들어왔습니다.");
                }else{
                    memberEntity.setRoomAuthority(RoomAuthority.SECONDARY);
                    chatEntity.setChatMessage(userEntity.getName()+"님이 " + joinMember.getRoomId() + "번 방에 들어왔습니다.");
                }

                memberRepository.save(memberEntity);
                // 환영 메시지 생성
                

            }else{
                chatEntity.setUserName("[Warning]");
                chatEntity.setChatMessage("이미 존재하는 User의 입장입니다.");
            }
        }
        return chatEntity;
    }
}
