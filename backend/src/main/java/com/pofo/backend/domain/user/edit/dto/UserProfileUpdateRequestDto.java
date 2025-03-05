package com.pofo.backend.domain.user.edit.dto;

import com.pofo.backend.domain.user.join.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileUpdateRequestDto {
    private String nickname;
    private String jobInterest;
    private String userStatus;

    public User.UserStatus getUserStatusEnum() {
        return User.UserStatus.valueOf(userStatus.toUpperCase());
    }
}
