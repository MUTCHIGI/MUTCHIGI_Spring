package com.CAUCSD.MUTCHIGI.room.chat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JoinMember {
    private long roomId;
    private long userId;
    private String roomPassword;
}
