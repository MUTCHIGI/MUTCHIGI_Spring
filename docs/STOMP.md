# 다음은 STOMP 통신방식을 지정해둔 문서입니다.
- **stompTest폴더의 테스트용 HTML을 참고**하면 좋습니다.
- **<주의> : JSON Field명이 일치하지 않으면 전송도 안되고 받는 것도 안됨.**
**- 현재 구현은 1,2번, 3번(일부)만 되어 있습니다.**
- UserId를 각 세션에 Attirbute로 저장하였음. Key 값 : user-id

<br/>

## 구독해야 하는 domain
- '/topic/' + roomId => 메인 채팅
- '/userDisconnect/{userId}/queue/errors' => 개인 유저에게 오류 알림 및 DISCONNECT용
 
<br/>

## 1. 방 만들기(/app/joinRoom/' + chatRoomId)

> 1) 'http://localhost:8080/room/create' 로 방을 만듬 => chatRoomId(방 id)를 반환받음
> 2) 'http://localhost:8080/room/'** + chatRoomId 로 STOMP 연결
> 3) 연결 직후 이제는 stompClient의 domain임. **'/app/joinRoom/' + chatRoomId** 로 아래 JSON 형식 보냄
   - **<주의> : JSON Field명이 일치하지 않으면 전송도 안되고 받는 것도 안됨.**
   - 방 만들고 나서 해당 방의 Member에 포함되지 않으면 자동으로 방장으로 지정해주기에 위의 로직을 무조건 따라야 함.
```
/app/joinRoom/' + chatRoomId 의 JSON 형식
{
  roomId: chatRoomId(long),
  roomPassword : password(String)
}
```
- Return 구독 domain : '/topic/' + roomId
- Return 메시지 : 단순 시스템 메세지

- SimpSession에 "user-id"를 키로 한 userId값이 저장됨

<br/>

## 2. 접속하기(/app/joinRoom/' + chatRoomId) << 방 만들기와 사실 동일함.
> 아래는 모두 stompClient의 domain임.
> 1) '/room/' + chatRoomId 로 STOMP 연결
> 2) 연결 직후 '/app/joinRoom/' + chatRoomId 로 아래 JSON 형식 보냄
```
/app/joinRoom/' + chatRoomId 의 JSON 형식
{
  roomId: chatRoomId(long),
  roomPassword : password(String)
}
```
- Return 구독 domain : '/topic/' + roomId
- Return 메시지 : 단순 시스템 메세지

- SimpSession에 "user-id"를 키로 한 userId값이 저장됨

<br/>

## 3. 채팅보내기/ 받기('/app/send/' + chatRoomId)
보내는 것과 받는 것의 JSON 차이가 있음.

### 3-1. 채팅 보내기 받기 (qsRelationId == -1) 구독 : ('/topic/' + roomId)
**qsRelationId는** 해당 퀴즈의 노래에 대한 Id인데 해당 노래를 맞추는 상황이 아님을 **qsRelationId == -1**임으로 명시한다.
- 보내는 메세지
```
'/app/send/' + chatRoomId 의 Send JSON 형식
{
  chatMessage: messageText(String),
  qsRelationId : qsRelationId(long) << -1
}
```
- 받는 메세지(qsRelationId == -1 인 경우 다음은 STOMP 통신방식을 지정해둔 문서입니다.
- **stompTest폴더의 테스트용 HTML을 참고**하면 좋습니다.
- **<주의> : JSON Field명이 일치하지 않으면 전송도 안되고 받는 것도 안됨.**

<br/>

### 3-2 정답 체크 (qsRelationId != -1) 구독 : ('/topic/' + roomId) & ('/topic/correct/' + roomId)
- 보내는 메세지(기존과 동일하다)
```
'/app/send/' + chatRoomId 의 Send JSON 형식
{
  chatMessage: messageText(String),
  qsRelationId : qsRelationId(long)
}
```
- 받는 메세지 (단순 채팅 반환) >> ('/topic/' + roomId)
```
{
  userName: userName(String),
  chatMessage: messageText(String)
}
```
- 받는 메세지 (해당 채팅이 정답일 시 추가로 반환됨.) >> ('/topic/correct/' + roomId)

```
{
  answerUserName : (String),
  answerSong : (String),
  answerSinger : (String)
}
```

## 4. 문제(노래) 제공 ('/app/getSong/' + chatRoomId + '/'+ songIndex) => 게임 시작도 포함되어 있음.
- 간단하게 서버로 부터 문제(노래)를 제공받도록 요청하는 도메인이다.
- songIndex는 서버에서 해당 Room이 가진 퀴즈의 노래 List중 몇 번째를 수행"할" 것인지를 포함한다.
- **songIndex가 0**인 경우 **"게임 시작"**과 동일한 의미이며 **첫 번째 노래를 요청**하는 것과 동일한 의미다.
   - songIndex가 5인 경우 6번째 노래를 요청하는 것과 동일한 의미다. 
- "게임 시작"(songIndex == 0)의 경우에는 "방장"이 이 Domain을 호출한다.
- 그 이후에는 5. Skip Vote기능과 연계하여 진행한다. (상세한 사항은 5번에 작성하였다.)
- **다음 노래를 미리 받아서 수행하는 경우 그냥 songIndex만 다음껄로 보내면 된다.**
- 보내는 메세지는 없다.
- 받는 메세지

``` 구독 : ('/topic/song/' + chatRoomId)
{
  qsRelationId : (long),
  songURL : (String), << 악기 분리는 빈 스트링으로 오고 그걸 이용해 REST API로 /GCP/DemucsSong/play/inRoom 으로 GET 요청을 보내면된다.
  originalSongURL : (String), << 악기 분리의 경우 YoutubeURL이 포함되고 기본인 경우 SongURL과 동일한 URL이 담긴다(Youtube)
  timeStamp : 00:00:00(String) << 노래 시작 시간이다.
}
```

## 5. Skip Vote ('/app/skipVote/' + chatRoomId)
- 해당 노래 문제를 Skip 투표를 하는 Domain으로 사전에 제공된 플레이어 숫자의 과반수가 되기 전까지 Vote한다.
   - 플레이어 숫자는 HTTP API에서 받은 User 객체의 갯수로 하면된다.
- 문제가 처음 시작되면 이 값은 모두 0으로 초기화되고 어떤 User가 Vote를 한다면 Server는 그 숫자를 받아 +1을 한 값을 반환한다.
- 그리고 그 값은 해당 멀티플레이 방에서 플레이 중인 다른 사람들도 받아서 확인할 수 있다.
- 그리고 특정 User가 Vote한 순간 과반수가 넘어간다면(Front에서 조건 분기문으로 미리 확인하여 처리) 4번 노래 제공을 실행하는 것으로 한다.
- 보내는 메세지
```
{
  voteNum : (int) <= 초기 숫자는 0이다.
}
```
- 받는 메세지 >> 구독 : ('/topic/vote/' + chatRoomId)
```
{
  voteNum : (int)
}
```

## 6. 힌트제공('/app/getHint/' + chatRoomId + '/' + qsRelationId)
- 4번을 받은 직후에 진행, 힌트 객체자체를 미리 제공해주고 클라이언트에서 진행 시간에 따라서 힌트를 Open하도록 함
- 서버 자체에서 다음 문제를 제공함과 동시에 Hint 객체들을 같이 받아서 처리 해주도록 클라이언트에서 구성하여야 함.
- 보내는 메세지는 없이 PathVariable을 이용해 처리한다.
- 받는 메세지 >> 구독 : ('/topic/hint/' + chatRoomId)
```
{
 hintNum : (int), // 몇 번째 힌트인가?
 hintType : (String), // 힌트 종류
 hintText : (String) // 힌트 내용용
}
```

## 7. 연결 끊기
- 연결은 프론트단에서 DISCONNECT를 해도 연결이 끊기고 연결중인 탭을 끊어도 연결이 끊긴다.
- 연결을 끊은 이후에 멤버 삭제, 방에 남은 인원이 없는 경우 방 삭제 같은 로직도 모두 구성을 완료 하였다.
- 만약 방장이 방에서 퇴장한다면 자동으로 새롭게 방장이 배정된다.

## 7-1 방장 새로 배정
- 앞에서 방장이 퇴장한다면 새롭게 방장을 서버에서 배정해준다.
- 받는 메세지 >> 구독 ('/topic/superUser/' + chatRoomId)
```
{
  userId : (long)
}
```

## 8. 강퇴하기('/app/kickMember/ + chatRoomId + '/' + userId)
- 방장 권한이 있는 사람만 이용이 가능하다. 방장권한이 없는자는 내부로직으로 걸러져 이용이 불가능하다.
- 방장 권한을 누가 가지고 있는 지는 기존 Rest API 중 /room/superUser로 알 수 있다.
- 일반적으로 방장은 방을 만든 사람에게 우선 부여된다.
- Rest API중 /room/userList로 얻은 UserId를 이용해서 강퇴한다.
- 실제로 Disconnect 되는 것은 아래 오류 구독에서 처리된다.
- 강퇴된 이후 서버차원에서 메세지를 보내준다.
- 보내는 메세지는 없고 PathVariable을 이용해 처리한다.
- 받는 메세지 >> 구독 ('/topic/kick/' + chatRoomId)
```
{
  userId : (long)
}
```

# 오류 구독 '/userDisconnect/{userId}/queue/errors'시 받는 메세지
## 1, 2 공통
- **INVALID_PASSWORD**(/app/joinRoom/' + chatRoomId) : 비밀번호 틀릴 시 받는 메세지 -> STOMP 연결 DISCONNECT해야함
- **ROOM_NOT_FOUND** : 해당 방은 존재하지 않는 방임 -> STOMP 연결 DISCONNECT해야함
- **KICKED_FROM_SERVER** : 강퇴되었음. -> DISCONNECT
