package com.pofo.backend.domain.user.join.service;

import com.pofo.backend.domain.user.join.dto.UserJoinRequestDto;
import com.pofo.backend.domain.user.join.dto.UserJoinResponseDto;
import com.pofo.backend.domain.user.join.entity.Oauths;
import com.pofo.backend.domain.user.join.entity.Users;
import com.pofo.backend.domain.user.join.repository.OauthsRepository;
import com.pofo.backend.domain.user.join.repository.UsersRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserJoinService {

    private final UsersRepository usersRepository;
    private final OauthsRepository oauthsRepository;

    //  유저 등록 매소드
    @Transactional
    public UserJoinResponseDto registerUser(UserJoinRequestDto userJoinRequestDto) {

        //  users 테이블에 이메일이 존재 하는지 확인.
        Optional<Users> existingUser = usersRepository.findByEmail(userJoinRequestDto.getEmail());

        //  기존유저의 경우 200 : 로그인 성공
        if (existingUser.isPresent()) {
            return UserJoinResponseDto.builder()
                    .message("로그인이 완료되었습니다.")
                    .resultCode("200")
                    .build();
        }

        //  소셜 로그인을 최초로 진행 하는 경우 : Users 테이블에 이메일, 이름, 닉네임, 성별, 나이대 입력
        Users newUser = Users.builder()
                .email(userJoinRequestDto.getEmail())
                .name(userJoinRequestDto.getName())
                .nickname(userJoinRequestDto.getNickname())
                .sex(userJoinRequestDto.getSex())
                .age(userJoinRequestDto.getAge())
                .build();
        usersRepository.save(newUser);

        //  소셜 로그인을 최초로 진행 하는 경우 : Users 테이블에 이메일, 이름, 닉네임, 성별, 나이대 입력
        Oauths oauths = Oauths.builder()
                .user(newUser)
                .provider(userJoinRequestDto.getProvider())
                .identify(userJoinRequestDto.getIdentify())
                .build();
        oauthsRepository.save(oauths);

        return UserJoinResponseDto.builder()
                .message("회원가입이 완료되었습니다.")
                .resultCode("200")
                .build();
    }
}
