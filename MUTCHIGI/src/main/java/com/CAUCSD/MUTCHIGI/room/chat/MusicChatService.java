package com.CAUCSD.MUTCHIGI.room.chat;

import com.CAUCSD.MUTCHIGI.room.Member.MemberEntity;
import com.CAUCSD.MUTCHIGI.room.Member.MemberRepository;
import com.CAUCSD.MUTCHIGI.room.Member.RoomAuthority;
import com.CAUCSD.MUTCHIGI.room.RoomEntity;
import com.CAUCSD.MUTCHIGI.room.RoomRepository;
import com.CAUCSD.MUTCHIGI.user.UserEntity;
import com.CAUCSD.MUTCHIGI.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpAttributes;
import org.springframework.messaging.simp.SimpAttributesContextHolder;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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

    public SendChatDTO joinRoomChat(JoinMemberDTO joinMemberDTO) {
        SimpAttributes simpAttributes = SimpAttributesContextHolder.currentAttributes();
        Object userId = simpAttributes.getAttribute("user-id");
        long userIdLong = -1;
        if(userId != null) {
            userIdLong = Long.parseLong(String.valueOf(userId));
        }
        System.out.println("userId : " + userIdLong);

        String errorDestination = "/userDisconnect/" + userIdLong + "/queue/errors";
        System.out.println(errorDestination);

        RoomEntity roomEntity = roomRepository.findById(joinMemberDTO.getRoomId()).orElse(null);
        if (roomEntity == null) {
            System.out.println("Room not found");
            messagingTemplate.convertAndSend(errorDestination, "ROOM_NOT_FOUND");
            return null;
        }
        if(!roomEntity.isPublicRoom()){ // 비공개방인 경우
            if(!roomEntity.getPassword().equals(joinMemberDTO.getRoomPassword())) { // 비밀번호 대조해보고 다르면
                System.out.println("Password does not match" + joinMemberDTO.getRoomPassword() + "is not Equals " + roomEntity.getPassword());
                messagingTemplate.convertAndSend(errorDestination, "INVALID_PASSWORD");
                return null;
            }
        }

        SendChatDTO sendChatDTO = new SendChatDTO();
        sendChatDTO.setUserName("[System]");

        UserEntity userEntity = userRepository.findById(userIdLong).orElse(null);
        if(userEntity == null){
            sendChatDTO.setUserName("[Warning]");
            sendChatDTO.setChatMessage("저장되지 않은 User의 입장입니다.");
        }else{
            if(memberRepository.findByRoomEntity_RoomIdAndUserEntity_UserId(joinMemberDTO.getRoomId(), userIdLong).isEmpty()){
                MemberEntity memberEntity = new MemberEntity();
                memberEntity.setUserEntity(userEntity);
                memberEntity.setRoomEntity(roomRepository.findById(joinMemberDTO.getRoomId()).orElse(null));
                if(memberRepository.findByRoomEntity_RoomId(joinMemberDTO.getRoomId()).isEmpty()){
                    memberEntity.setRoomAuthority(RoomAuthority.FIRST); // 방에 아무도 없으면 방장으로 임명
                    sendChatDTO.setChatMessage(userEntity.getName()+"님이 방장으로 " + joinMemberDTO.getRoomId() + "번 방에 들어왔습니다.");
                }else{
                    memberEntity.setRoomAuthority(RoomAuthority.SECONDARY);
                    sendChatDTO.setChatMessage(userEntity.getName()+"님이 " + joinMemberDTO.getRoomId() + "번 방에 들어왔습니다.");
                }

                memberRepository.save(memberEntity);
                // 환영 메시지 생성
                

            }else{
                sendChatDTO.setUserName("[Warning]");
                sendChatDTO.setChatMessage("이미 존재하는 User의 입장입니다.");
            }
        }
        return sendChatDTO;
    }

    public SendChatDTO setMessageChat(ReceiveChatDTO receiveChatDTO){
        SimpAttributes simpAttributes = SimpAttributesContextHolder.currentAttributes();
        Object userId = simpAttributes.getAttribute("user-id");
        long userIdLong = -1;
        if(userId != null) {
            userIdLong = Long.parseLong(String.valueOf(userId));
        }
        System.out.println("userId : " + userIdLong);

        UserEntity userEntity = userRepository.findById(userIdLong).orElse(null);
        String errorDestination = "/userDisconnect/" + userIdLong + "/queue/errors";
        
        if(userEntity == null) {
            messagingTemplate.convertAndSend(errorDestination, "USET_NOT_FOUND");
            return null;
        }
        SendChatDTO sendChatDTO = new SendChatDTO();
        sendChatDTO.setUserName(userEntity.getName());
        sendChatDTO.setChatMessage(receiveChatDTO.getChatMessage());

        return sendChatDTO;
    }
}
