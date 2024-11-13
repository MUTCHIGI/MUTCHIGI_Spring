package com.CAUCSD.MUTCHIGI.room;

import lombok.Data;

@Data
public class MakeRoomDTO {
    private String roomName;

    private boolean publicRoom;

    private boolean participateAllowed;

    private String password;

    private int maxPlayer;

    private long quizId;

    private long userId;
}
