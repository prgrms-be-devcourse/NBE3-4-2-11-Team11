package com.pofo.backend.domain.user.join.service;

import com.pofo.backend.common.exception.MultipleAccountsFoundException;
import com.pofo.backend.domain.user.join.dto.UserJoinRequestDto;
import com.pofo.backend.domain.user.join.dto.UserJoinResponseDto;
import com.pofo.backend.domain.user.join.entity.Oauth;
import com.pofo.backend.domain.user.join.entity.User;
import com.pofo.backend.domain.user.join.repository.OauthsRepository;
import com.pofo.backend.domain.user.join.repository.UsersRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserJoinService {

    private final UsersRepository usersRepository;
    private final OauthsRepository oauthsRepository;

    //  유저 등록 매소드
    @Transactional
    public UserJoinResponseDto registerUser(UserJoinRequestDto userJoinRequestDto) {

        System.out.println("Debug - identify: " + userJoinRequestDto.getIdentify());
        System.out.println("Debug - name: " + userJoinRequestDto.getName());
        System.out.println("Debug - nickname: " + userJoinRequestDto.getNickname());
        System.out.println("Debug - sex: " + userJoinRequestDto.getSex());

        //  1.  oauth테이블의 provider, identify 항목 취득 : 2025-01-31 반영
        Optional<Oauth> existingOauths = oauthsRepository.findByProviderAndIdentify(
                userJoinRequestDto.getProvider(),
                userJoinRequestDto.getIdentify()
        );

        //  2.  users 테이블에 이메일이 존재 하는지 확인.
        Optional<User> existingUser = usersRepository.findByEmail(userJoinRequestDto.getEmail());

        //  3.  Users 테이블에서 같은 이름 + 생년월일 + 성별 + 닉네임이 있는지 확인
        List<User> possibleExistingUser = usersRepository.findByNameAndSexAndAgeAndNickname(
                userJoinRequestDto.getName(),
                userJoinRequestDto.getSex(),
                userJoinRequestDto.getAge(),
                userJoinRequestDto.getNickname()
        );

        //  Users 테이블에서 같은 이름 + 생년월일 + 성별 + 닉네임이 2건 이상 검출 시 에러 핸들러로 던짐
        if (possibleExistingUser.size() > 1) {
            throw new MultipleAccountsFoundException("동일 정보의 계정이 다수 존재합니다.");
        }

        /*
          1.  oauths 등록 정보 O && users에 등록된 email 정보 O >> 로그인 처리
          2.  oauths 등록 정보 X && users에 등록된 email 정보 O >> 기존 email 정보에 맵핑되는 oauth 정보 추가
          3.  oauths 등록 정보 X && users에 등록된 email 정보 X >>  email 뿐만 아니라, 이름, 성별, 생년월일, 닉네임을
              토대로 유저 검증.
          4.  위 케이스 이외는 신규가입
          : 2025-02-01 반영 */
        if (existingOauths.isPresent() && existingUser.isPresent()) {
            return UserJoinResponseDto.builder()
                    .message("로그인이 완료 되었습니다.")
                    .build();
        } else if (existingOauths.isEmpty() && existingUser.isPresent()) {
            Oauth newOauth = Oauth.builder()
                    .user(existingUser.get())  // 기존 사용자와 매핑
                    .provider(userJoinRequestDto.getProvider())
                    .identify(userJoinRequestDto.getIdentify())
                    .build();
            oauthsRepository.save(newOauth);

            return UserJoinResponseDto.builder()
                    .message("기존 유저에 새로운 소셜 로그인 연동 성공")
                    .build();
        } else if (possibleExistingUser.size() == 1) {
            return UserJoinResponseDto.builder()
                    .message("이전에 이용한 소셜 로그인이 있을 가능성이 있습니다. 연동을 진행하겠습니까? ")
                    .build();
        } else {
            //  소셜 로그인을 최초로 진행 하는 경우 : Users 테이블에 이메일, 이름, 닉네임, 성별, 나이대 입력
            User newUser = User.builder()
                    .email(userJoinRequestDto.getEmail())
                    .name(userJoinRequestDto.getName())
                    .nickname(userJoinRequestDto.getNickname())
                    .sex(userJoinRequestDto.getSex())
                    .age(userJoinRequestDto.getAge())
                    .build();
            usersRepository.save(newUser);

            //  소셜 로그인을 최초로 진행 하는 경우 : Users 테이블에 이메일, 이름, 닉네임, 성별, 나이대 입력
            Oauth oauth = Oauth.builder()
                    .user(newUser)
                    .provider(userJoinRequestDto.getProvider())
                    .identify(userJoinRequestDto.getIdentify())
                    .build();
            oauthsRepository.save(oauth);

            return UserJoinResponseDto.builder()
                    .message("회원 가입 성공")
                    .build();
        }
    }
}
