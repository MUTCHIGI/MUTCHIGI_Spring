package com.CAUCSD.MUTCHIGI.room.Member;

import com.CAUCSD.MUTCHIGI.room.RoomEntity;
import com.CAUCSD.MUTCHIGI.user.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class MemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long memberId;

    @ManyToOne
    @JoinColumn(name = "roomId", referencedColumnName = "roomId")
    private RoomEntity roomEntity;

    @ManyToOne
    @JoinColumn(name =  "participateUserId", referencedColumnName = "userId")
    private UserEntity userEntity;

    private RoomAuthority roomAuthority;
}
