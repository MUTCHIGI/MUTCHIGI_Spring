package com.CAUCSD.MUTCHIGI.user;

import com.CAUCSD.MUTCHIGI.user.provider.Provider;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {

    private String platformUserId;
    private String email;
    private String name;
    private String profileImageURL;
    private long providerId;
    private MemberRole role;

}
