package com.CAUCSD.MUTCHIGI.room.chat;

import com.CAUCSD.MUTCHIGI.quizSong.QuizSongRelation;
import com.CAUCSD.MUTCHIGI.quizSong.QuizSongRelationReopository;
import com.CAUCSD.MUTCHIGI.quizSong.answer.AnswerEntity;
import com.CAUCSD.MUTCHIGI.quizSong.answer.AnswerRepository;
import com.CAUCSD.MUTCHIGI.quizSong.hint.HintEntity;
import com.CAUCSD.MUTCHIGI.quizSong.hint.HintRepository;
import com.CAUCSD.MUTCHIGI.room.Member.MemberEntity;
import com.CAUCSD.MUTCHIGI.room.Member.MemberRepository;
import com.CAUCSD.MUTCHIGI.room.Member.RoomAuthority;
import com.CAUCSD.MUTCHIGI.room.RoomEntity;
import com.CAUCSD.MUTCHIGI.room.RoomRepository;
import com.CAUCSD.MUTCHIGI.song.SongEntity;
import com.CAUCSD.MUTCHIGI.song.SongRepository;
import com.CAUCSD.MUTCHIGI.song.singer.SingerEntity;
import com.CAUCSD.MUTCHIGI.song.singer.SingerRepository;
import com.CAUCSD.MUTCHIGI.song.singer.relation.SingerSongRelation;
import com.CAUCSD.MUTCHIGI.song.singer.relation.SingerSongRelationRepository;
import com.CAUCSD.MUTCHIGI.user.UserEntity;
import com.CAUCSD.MUTCHIGI.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpAttributes;
import org.springframework.messaging.simp.SimpAttributesContextHolder;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
    private QuizSongRelationReopository quizSongRelationReopository;

    @Autowired
    private HintRepository hintRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private SingerSongRelationRepository singerSongRelationRepository;

    @Autowired
    private final ScheduledExecutorService scheduler;

    @Autowired
    public MusicChatService(SimpMessagingTemplate messagingTemplate, ScheduledExecutorService scheduler) {
        this.messagingTemplate = messagingTemplate;
        this.scheduler = scheduler;
    }

    public SendChatDTO joinRoomChat(JoinMemberDTO joinMemberDTO) {
        SimpAttributes simpAttributes = SimpAttributesContextHolder.currentAttributes();
        Object userId = simpAttributes.getAttribute("user-id");
        simpAttributes.setAttribute("chatRoom-id", joinMemberDTO.getRoomId());
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

    public SendChatDTO setMessageChat(long chatRoomId,ReceiveChatDTO receiveChatDTO){
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
        if(receiveChatDTO.getQsRelationId() == -1){
            sendChatDTO.setUserName(userEntity.getName());
            sendChatDTO.setChatMessage(receiveChatDTO.getChatMessage());
        }else{
            sendChatDTO.setUserName(userEntity.getName());
            sendChatDTO.setChatMessage(receiveChatDTO.getChatMessage());

            sendCorrectAnswerMessage(chatRoomId, receiveChatDTO);
        }

        return sendChatDTO;
    }

    public SendNextSongDTO getNextSongAndStart(long chatRoomId, int songIndex){
        SendNextSongDTO sendNextSongDTO = new SendNextSongDTO();
        SimpAttributes simpAttributes = SimpAttributesContextHolder.currentAttributes();

        //즉 처음 시작하는 거면 qsRelationList 세션에 저장
        if(songIndex == 0){
            RoomEntity roomEntity = roomRepository.findById(chatRoomId).orElse(null);
            if(roomEntity == null){
                return null;
            }
            List<QuizSongRelation> qsRelationList = quizSongRelationReopository
                    .findByQuizEntity_QuizId(roomEntity.getQuiz().getQuizId());

            List<Long> qsRelationIDList = new ArrayList<>();
            for(QuizSongRelation qsRelation : qsRelationList){
                qsRelationIDList.add(qsRelation.getSongEntity().getSongId());
            }
            simpAttributes.setAttribute("qsRelationList", qsRelationIDList);
            System.out.println("songIndex 0 : " + qsRelationIDList);
        }

        Object attribute = simpAttributes.getAttribute("qsRelationList");
        List<Long> getQSRelationIDList = null;
        if (attribute instanceof List<?>) {
            getQSRelationIDList = (List<Long>) attribute;
        } else {
            getQSRelationIDList = new ArrayList<>(); // 예를 들어 빈 리스트로 초기화
        }

        //다음 곡에대한 DTO 전송
        long qsRelationID = getQSRelationIDList.get(songIndex);
        QuizSongRelation quizSongRelation = quizSongRelationReopository.findById(qsRelationID).orElse(null);
        if(quizSongRelation != null){
            sendNextSongDTO.setQsRelationId(qsRelationID);
            sendNextSongDTO.setSongURL(quizSongRelation.getSongEntity().getPlayURL());
            sendNextSongDTO.setOriginalSongURL(quizSongRelation.getSongEntity().getPlayURL());

            LocalTime startTime = quizSongRelation.getSongEntity().getSongTime();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            String timeStamp = startTime.format(formatter);

            sendNextSongDTO.setTimeStamp(timeStamp);

            //해당 문제에 대한 정답 미리 세션에 저장
            List<AnswerEntity> answerEntityList = answerRepository.findByQuizSongRelation(quizSongRelation);
            List<String> answerList = new ArrayList<>();
            for(AnswerEntity answerEntity : answerEntityList){
                answerList.add(answerEntity.getAnswer());
            }
            simpAttributes.setAttribute("answerList", answerList);
            System.out.println("songIndex Next : " + answerList);

            List<HintEntity> hintList = hintRepository.findByQuizSongRelation(quizSongRelation);
            sendHintWithEvent(chatRoomId, hintList);

        }else{
            return null;
        }

        return sendNextSongDTO;
    }

    public VoteDTO VoteToSkip (long chatRoomId, VoteDTO voteDTO){
        if(voteDTO.getVoteNum() == -1)
            voteDTO.setVoteNum(0);
        else{
            voteDTO.setVoteNum(voteDTO.getVoteNum() + 1);
        }
        return voteDTO;
    }

    @Async
    public void sendCorrectAnswerMessage(long chatRoomId, ReceiveChatDTO receiveChatDTO){
        SimpAttributes simpAttributes = SimpAttributesContextHolder.currentAttributes();
        Object attribute = simpAttributes.getAttribute("answerList");
        List<String> answerList = null;
        System.out.println("여기까진 오케이");
        if (attribute instanceof List<?>) {
            answerList = (List<String>) attribute;
        } else {
            answerList = new ArrayList<>(); // 예를 들어 빈 리스트로 초기화
        }
        System.out.println("정답인 거 체크 할 때 answerList : " + answerList);

        for(String answer : answerList){
            if(answer.equals(receiveChatDTO.getChatMessage())){
                System.out.println("여기 이프문 테스트");
                String correctDestination ="/topic/correct/" + chatRoomId;
                CorrectAnswerDTO correctAnswerDTO = new CorrectAnswerDTO();

                long user_id = (long) simpAttributes.getAttribute("user-id");
                UserEntity userEntity = userRepository.findById(user_id).orElse(null);
                if(userEntity != null){
                    correctAnswerDTO.setAnswerUserName(userEntity.getName());
                }

                QuizSongRelation quizSongRelation = quizSongRelationReopository.findById(receiveChatDTO.getQsRelationId()).orElse(null);
                if(quizSongRelation != null){
                    System.out.println("여기까지 테스트");
                    correctAnswerDTO.setAnswerSong(quizSongRelation.getSongEntity().getSongName());
                    SingerSongRelation singerSongRelation = singerSongRelationRepository.findBySong(quizSongRelation.getSongEntity());
                    correctAnswerDTO.setAnswerSinger(singerSongRelation.getSinger().getSingerName());
                    messagingTemplate.convertAndSend(correctDestination, correctAnswerDTO);
                }
            }
        }

    }

    public void sendHintWithEvent(long chatRoomId ,List<HintEntity> hintEntityList){
        hintEntityList.sort(Comparator.comparing(HintEntity::getHintTime));
        int second = 0;
        for(HintEntity hintEntity : hintEntityList){
            scheduler.schedule(( )->{
                messagingTemplate.convertAndSend("/topic/hint" + chatRoomId , hintEntity);
            }, hintEntity.getHintTime().toSecondOfDay() - second, TimeUnit.SECONDS);
            second = hintEntity.getHintTime().toSecondOfDay();
        }
    }

}
