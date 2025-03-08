package com.pofo.backend.domain.user.edit.controller;

import com.pofo.backend.common.rsData.RsData;
import com.pofo.backend.domain.user.edit.dto.UserProfileUpdateRequestDto;
import com.pofo.backend.domain.user.edit.dto.UserProfileUpdateResponseDto;
import com.pofo.backend.domain.user.edit.service.UserEditService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@AllArgsConstructor
public class UserEditController {
    UserEditService userEditService;

    @PatchMapping("/edit")
    public ResponseEntity<RsData<UserProfileUpdateResponseDto>> updateProfile(
            @RequestBody UserProfileUpdateRequestDto request,
            Authentication authentication) {
        String email = authentication.getName();

        userEditService.updateUserProfile(email, request);

        return ResponseEntity.ok(new RsData<>("200", "프로필 수정 완료"));
    }


}
