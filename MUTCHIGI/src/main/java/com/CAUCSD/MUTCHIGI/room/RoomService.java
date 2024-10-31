package com.CAUCSD.MUTCHIGI.room;

import com.CAUCSD.MUTCHIGI.quiz.QuizEntity;
import com.CAUCSD.MUTCHIGI.quiz.QuizRepository;
import com.CAUCSD.MUTCHIGI.room.Member.RoomAuthority;
import com.CAUCSD.MUTCHIGI.room.Member.MemberEntity;
import com.CAUCSD.MUTCHIGI.room.Member.MemberRepository;
import com.CAUCSD.MUTCHIGI.user.UserEntity;
import com.CAUCSD.MUTCHIGI.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MemberRepository memberRepository;

    public long createRoom(MakeRoomDTO makeRoomDTO) {
        RoomEntity roomEntity = new RoomEntity();

        QuizEntity quizEntity = new QuizEntity();
        quizEntity = quizRepository.findById(makeRoomDTO.getQuizId()).orElse(null);
        if(quizEntity == null) {
            return -1;
        }

        roomEntity.setQuiz(quizEntity);
        roomEntity.setRoomName(makeRoomDTO.getRoomName());
        roomEntity.setPublicRoom(makeRoomDTO.isPublicRoom());
        roomEntity.setPassword(makeRoomDTO.getPassword());
        roomEntity.setMaxPlayer(makeRoomDTO.getMaxPlayer());
        roomEntity.setRoomReleaseDate(LocalDate.now());
        roomEntity.setParticipateAllowed(makeRoomDTO.isParticipateAllowed());

        roomEntity = roomRepository.save(roomEntity);


        return roomEntity.getRoomId();
    }

    public List<Long> getRoomIDList(int page, int offset, int modId, int typeId, String quizTitle, boolean publicRoom) {
        PageRequest pageRequest = PageRequest.of(page, offset, Sort.by("roomReleaseDate").descending());

        Page<RoomEntity> roomEntities;

        // modId와 typeId에 따라 조건 분기
        if (modId == 0 && typeId == 0) {
            // modId와 typeId가 모두 0인 경우
            roomEntities = roomRepository.findByQuiz_QuizNameContainingAndPublicRoom(quizTitle, publicRoom, pageRequest);
        } else if (modId == 0) {
            // modId가 0이고 typeId가 있는 경우
            roomEntities = roomRepository.findByQuiz_QuizNameContainingAndQuiz_TypeIdAndPublicRoom(quizTitle, typeId, publicRoom, pageRequest);
        } else if (typeId == 0) {
            // typeId가 0이고 modId가 있는 경우
            roomEntities = roomRepository.findByQuiz_QuizNameContainingAndQuiz_ModIdAndPublicRoom(quizTitle, modId, publicRoom, pageRequest);
        } else {
            // modId와 typeId가 모두 있는 경우
            roomEntities = roomRepository.findByQuiz_QuizNameContainingAndQuiz_ModIdAndQuiz_TypeIdAndPublicRoom(quizTitle, modId, typeId, publicRoom, pageRequest);
        }

        return roomEntities.stream()
                .map(RoomEntity::getRoomId)
                .toList();
    }

    public List<UserEntity> getUserListFromDB(long roomId){
        List<MemberEntity> memberList = memberRepository.findByRoomEntity_RoomId(roomId);
        List<UserEntity> userList = new ArrayList<>();
        for(MemberEntity memberEntity : memberList){
            UserEntity userEntity = new UserEntity();
            userEntity = userRepository.findById(memberEntity.getUserEntity().getUserId()).orElse(null);
            userList.add(userEntity);
        }
        return userList;
    }

    public long getSuperUserIDFromDB(long roomId){
        List<MemberEntity> memberList = memberRepository.findByRoomEntity_RoomId(roomId);
        for(MemberEntity memberEntity : memberList){
            if(memberEntity.getRoomAuthority() == RoomAuthority.FIRST){
                return memberEntity.getUserEntity().getUserId();
            }
        }
        return -1;
    }
}
