# 다음은 STOMP 통신방식을 지정해둔 문서입니다.
- **stompTest폴더의 테스트용 HTML을 참고**하면 좋습니다.
- **<주의> : JSON Field명이 일치하지 않으면 전송도 안되고 받는 것도 안됨.**

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
  userId : UserId(long),
  roomPassword : password(String)
}
```
- Return 구독 domain : '/topic/' + roomId
- Return 메시지 : 단순 시스템 메세지

<br/>

## 2. 접속하기(/app/joinRoom/' + chatRoomId) << 방 만들기와 사실 동일함.
> 아래는 모두 stompClient의 domain임.
> 1) '/room/' + chatRoomId 로 STOMP 연결
> 2) 연결 직후 '/app/joinRoom/' + chatRoomId 로 아래 JSON 형식 보냄
```
/app/joinRoom/' + chatRoomId 의 JSON 형식
{
  roomId: chatRoomId(long),
  userId : UserId(long),
  roomPassword : password(String)
}
```
- Return 구독 domain : '/topic/' + roomId
- Return 메시지 : 단순 시스템 메세지

<br/>

# 오류 구독 '/userDisconnect/{userId}/queue/errors'시 받는 메세지
## 1, 2 공통
- **INVALID_PASSWORD**(/app/joinRoom/' + chatRoomId) : 비밀번호 틀릴 시 받는 메세지 -> STOMP 연결 DISCONNECT해야함
- **ROOM_NOT_FOUND** : 해당 방은 존재하지 않는 방임 -> STOMP 연결 DISCONNECT해야함
