package com.CAUCSD.MUTCHIGI.room;

import com.CAUCSD.MUTCHIGI.room.Member.MemberEntity;
import com.CAUCSD.MUTCHIGI.room.Member.MemberRepository;
import com.CAUCSD.MUTCHIGI.user.UserEntity;
import com.CAUCSD.MUTCHIGI.user.UserRepository;
import com.CAUCSD.MUTCHIGI.user.security.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpAttributes;
import org.springframework.messaging.simp.SimpAttributesContextHolder;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.simp.user.SimpSession;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.List;

@Configuration
public class WebSocketAuthInterceptor  implements ChannelInterceptor {

    private final JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private RoomRepository roomRepository;
    
    @Autowired
    public WebSocketAuthInterceptor (JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;

    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);

        //System.out.println("여기는 진입하는가?"+headerAccessor);
        if (StompCommand.CONNECT.equals(headerAccessor.getCommand())) {
            String token = headerAccessor.getFirstNativeHeader("Authorization");

            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7); // "Bearer " 제거
                Authentication auth = authenticateUser(token);
                if (auth != null) {
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    headerAccessor.setUser(auth); // 인증된 사용자 설정

                    UserEntity userEntity = userRepository.findByPlatformUserId(auth.getName());

                    SimpAttributes simpAttributes = SimpAttributesContextHolder.currentAttributes();
                    simpAttributes.setAttribute("user-id", userEntity.getUserId());

                } else {
                    throw new RuntimeException("Unauthorized");
                }
            }

        }else if(StompCommand.DISCONNECT.equals(headerAccessor.getCommand())){
            SimpAttributes simpAttributes = SimpAttributesContextHolder.currentAttributes();
            Object userId = simpAttributes.getAttribute("user-id");
            long userIdLong = -1;
            if(userId != null) {
                userIdLong = Long.parseLong(String.valueOf(userId));
                long roomId = Long.parseLong(String.valueOf(simpAttributes.getAttribute("chatRoom-id")));
                List<MemberEntity> memberList = memberRepository.findByRoomEntity_RoomId(roomId);
                for(MemberEntity memberEntity : memberList){
                    if(memberEntity.getUserEntity().getUserId() == userIdLong){ // 해당 방에 User있는경우
                        memberRepository.delete(memberEntity);
                    }
                }
                
                // 멤버를 DB에서 삭제한 이후 다시 불러왔을 때 그 리스트가 비어있다면 방 삭제
                memberList = memberRepository.findByRoomEntity_RoomId(roomId);
                if(memberList.isEmpty()){
                    RoomEntity roomEntity = roomRepository.findById(roomId).orElse(null);
                    if(roomEntity != null){
                        roomRepository.delete(roomEntity);
                    }
                }
            }
        }
        System.out.println(message);
        return message;
    }


    private Authentication authenticateUser(String token) {
        // JWT 토큰 검증 로직
        if (jwtUtil.validateToken(token, jwtUtil.extractUsername(token))) { // 토큰과 사용자 이름 검증
            String username = jwtUtil.extractUsername(token); // JWT에서 사용자 이름 추출
            Claims claims = jwtUtil.extractAllClaims(token); // JWT에서 모든 클레임 추출
            System.out.println("여기 통과하는 중"+claims.getIssuer()+claims.getAudience());
            // 권한 추출 (예: roles 클레임에서 권한 가져오기)
            List<GrantedAuthority> authorities = jwtUtil.getAuthoritiesFromClaims(claims); // 클레임에서 권한 추출
            System.out.println("인증 확인" + authorities);
            // UsernamePasswordAuthenticationToken 생성
            return new UsernamePasswordAuthenticationToken(username, token, authorities);
        }
        return null; // 토큰이 유효하지 않은 경우 null 반환
    }
}
