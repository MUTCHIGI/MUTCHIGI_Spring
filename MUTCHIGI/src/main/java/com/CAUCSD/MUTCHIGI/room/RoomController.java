package com.CAUCSD.MUTCHIGI.room;

import com.CAUCSD.MUTCHIGI.user.UserEntity;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/room")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @Autowired
    private RoomRepository roomRepository;

    @PostMapping("/create")
    public ResponseEntity<Long> createNewRoom(
            @RequestBody MakeRoomDTO makeRoomDTO
    ){
        long roomId = roomService.createRoom(makeRoomDTO);
        if(roomId == -1){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(roomId);
    }

    @GetMapping("/idList")
    @Operation(summary = "멀티플레이 방 Id List를 반환받는 API입니다.", description = """
            page 기본 : 1 (1부터 시작함, 0 시작 X)
            offset : 6 (페이지당 6 보여줌)
            modId 기본 : 0 (0 : 전체, 1 : 커스텀, 2: 플레이리스트)
            typeId 기본 : 0 (0 : 전체, 1 : 기본, 2 : 악기분리)
            quizTitle 기본 : ""(빈 String => 전체 조회가능)
            publicRoom 기본 : true(true면 공개방)
            """)
    public ResponseEntity<List<Long>> getRoomIdList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "6") int offset,
            @RequestParam(defaultValue = "0") int modId,
            @RequestParam(defaultValue = "0") int typeId,
            @RequestParam(defaultValue = "") String quizTitle,
            @RequestParam(defaultValue = "true") boolean publicRoom

    ){
        List<Long> roomIdList = roomService.getRoomIDList(page-1, offset, modId, typeId, quizTitle, publicRoom);
        if(roomIdList.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(roomIdList);
    }

    @GetMapping("/Entities")
    @Operation(summary = "전에 받은 idList를 RequsetParam에 담아서 보내주면 됩니다.")
    public ResponseEntity<List<RoomEntity>> getRoomEntities(
            @RequestParam List<Long> idList
    ){
        List<RoomEntity> roomEntities;
        try{
            roomEntities = roomRepository.findAllById(idList);
            if(roomEntities.isEmpty()){
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(roomEntities);
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/userList")
    @Operation(summary = "멀티플레이 플레이 환경에서 유저에 대한 정보를 불러오는 API")
    public ResponseEntity<List<UserEntity>> getUserList(
            @RequestParam long roomId
    ){
        return ResponseEntity
                .ok()
                .body(roomService.getUserListFromDB(roomId));
    }

    @GetMapping("/superUser")
    @Operation(summary = "해당 Room의 방장이 누구인지 확인하는 API")
    public ResponseEntity<Long> getSuperUserList(
            @RequestParam long roomId
    ){
        return ResponseEntity
                .ok(roomService.getSuperUserIDFromDB(roomId));
    }

    //강퇴기능 추후에 만들기
}
