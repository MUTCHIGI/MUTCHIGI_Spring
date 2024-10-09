package com.CAUCSD.MUTCHIGI.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {

    private String platform_user_id;
    private String email;
    private String name;
    private String profileImageURL;
    private int provider_id;

}
