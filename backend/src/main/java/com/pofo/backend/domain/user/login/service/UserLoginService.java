package com.pofo.backend.domain.user.login.service;

import com.pofo.backend.common.exception.SocialLoginException;
import com.pofo.backend.common.security.dto.TokenDto;
import com.pofo.backend.common.security.jwt.TokenProvider;
import com.pofo.backend.domain.user.join.entity.Oauth;
import com.pofo.backend.domain.user.join.entity.User;
import com.pofo.backend.domain.user.join.repository.UsersRepository;
import com.pofo.backend.domain.user.login.dto.KakaoTokenResponse;
import com.pofo.backend.domain.user.login.dto.NaverTokenResponse;
import com.pofo.backend.domain.user.login.dto.UserLoginResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserLoginService {

    //  Users 테이블에 대한 레포지토리
    private final UsersRepository usersRepository;

    private final TokenProvider tokenProvider;

    //  Naver Oauths 정보 시작
    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String naverClientId;

    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String naverClientSecret;

    @Value("${spring.security.oauth2.client.registration.naver.redirect-uri}")
    private String naverRedirectUri;
    //  Naver Oauths 정보 끝

    //  Kakao Oauths 정보 시작
    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String kakaoRedirectUri;
    //  Kakao Oauths 정보 끝

    public UserLoginResponseDto processNaverLogin(Oauth.Provider provider,String code, String state) {
        try {
            log.info("🛠 네이버 로그인 처리 시작! code: {}, state: {}", code, state);

            // 1.  토큰 발급 : 네이버
            String naverAccessToken = getAccessToken(provider,code, state);

            // 2. 사용자 정보 가져오기 : 네이버
            UserLoginResponseDto naverUserInfo = getNaverUserInfo(naverAccessToken);

            // 3. 사용자 정보 처리 및 저장/업데이트  : 네이버
            UserLoginResponseDto naverUser = saveOrUpdateNaverUser(naverUserInfo);


            return naverUser;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("네이버 로그인 처리 중 오류 발생: " + e.getMessage());
        }
    }

/*    private String getAccessNaverToken(String code, String state) {
        String tokenRequestUrl = "https://nid.naver.com/oauth2.0/token?"
                + "grant_type=authorization_code"
                + "&client_id=" + naverClientId
                + "&client_secret=" + naverClientSecret
                + "&code=" + code
                + "&state=" + state;

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<NaverTokenResponse> tokenResponse = restTemplate.getForEntity(tokenRequestUrl, NaverTokenResponse.class);

        if (tokenResponse.getStatusCode() != HttpStatus.OK || tokenResponse.getBody() == null) {
            throw new SocialLoginException("소셜 로그인 실패 : 네이버, 사유 : 토큰 취득 실패. 응답코드 :" + tokenResponse.getStatusCode());
        }

        return tokenResponse.getBody().getAccessToken();
    }*/

    private String getAccessToken(Oauth.Provider provider, String code, String state) {
        String tokenRequestUrl;

        if(provider == Oauth.Provider.NAVER) {
            tokenRequestUrl = "https://nid.naver.com/oauth2.0/token?"
                    + "grant_type=authorization_code"
                    + "&client_id=" + naverClientId
                    + "&client_secret=" + naverClientSecret
                    + "&code=" + code
                    + "&state=" + state;

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<NaverTokenResponse> tokenResponse = restTemplate.getForEntity(tokenRequestUrl, NaverTokenResponse.class);

            if (tokenResponse.getStatusCode() != HttpStatus.OK || tokenResponse.getBody() == null) {
                throw new SocialLoginException("소셜 로그인 실패 : 네이버, 사유 : 토큰 취득 실패. 응답코드 :" + tokenResponse.getStatusCode());
            }

            return tokenResponse.getBody().getAccessToken();

        } else if (provider == Oauth.Provider.KAKAO) {
            tokenRequestUrl = "https://kauth.kakao.com/oauth/token?"
                    + "grant_type=authorization_code"
                    + "&client_id=" + kakaoClientId
                    + "&redirect_uri=" + kakaoRedirectUri
                    + "&code=" + code;

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<KakaoTokenResponse> tokenResponse = restTemplate.getForEntity(tokenRequestUrl, KakaoTokenResponse.class);

            if (tokenResponse.getStatusCode() != HttpStatus.OK || tokenResponse.getBody() == null) {
                throw new SocialLoginException("소셜 로그인 실패 : 네이버, 사유 : 토큰 취득 실패. 응답코드 :" + tokenResponse.getStatusCode());
            }

            return tokenResponse.getBody().getAccessToken();

        } else {
            throw new SocialLoginException("지원되지 않는 OAuth Provider입니다.");
        }
    }

    private UserLoginResponseDto getNaverUserInfo(String accessToken) {
        String userInfoUrl = "https://openapi.naver.com/v1/nid/me";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        HttpEntity<?> entity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<Map<String, Object>> userInfoResponse = restTemplate.exchange(
                userInfoUrl, HttpMethod.GET, entity, new ParameterizedTypeReference<>() {}
        );

        if (userInfoResponse.getStatusCode() != HttpStatus.OK || userInfoResponse.getBody() == null) {
            throw new SocialLoginException("소셜 네이버 실패 : 네이버, 사유 : 사용자 정보 요청 실패 , 응답코드 : " + userInfoResponse.getStatusCode());
        }

        Map<String, Object> responseMap = (Map<String, Object>) userInfoResponse.getBody().get("response");

        if (responseMap == null || !responseMap.containsKey("email")) {
            throw new SocialLoginException("소셜 네이버 실패 : email 정보가 없습니다.");
        }

        String email = (String) responseMap.get("email");
        String identify =  (String) responseMap.get("id");

        return UserLoginResponseDto.builder()
                .identify(identify)
                .email(email)
                .build();
    }

    private UserLoginResponseDto saveOrUpdateNaverUser(UserLoginResponseDto userInfo) {
        String naverId = userInfo.getIdentify();
        String email = userInfo.getEmail();


        Optional<User> existingUser = usersRepository.findByEmail(email);
        User naverUser;

        if (existingUser.isPresent()) {
            //  네이버 계정 통해 로그인 이력이 있으면 로그인 진행.
            User nowUser = existingUser.get();
            TokenDto jwtToken = authenticateUser(nowUser);
            log.info("✅ 기존 회원: 이메일({}) - 로그인 완료", email);

            return UserLoginResponseDto.builder()
                    .message("로그인이 완료 되었습니다.")
                    .resultCode("200")
                    .provide(Oauth.Provider.NAVER.name())
                    .identify(naverId)
                    .email(email)
                    .username(nowUser.name)
                    .token(jwtToken.getAccessToken())
                    .build();
        } else {
            //  네이버 계정을 통한 로그인을 최초로 진행하는 경우

            return UserLoginResponseDto.builder()
                    .message("소셜 로그인을 위한 네이버 계정 등록이 완료되었습니다. 나머지 정보를 입력해 주세요. ")
                    .resultCode("201")
                    .provide(Oauth.Provider.NAVER.name())
                    .identify(naverId)
                    .email(email)
                    .build();
        }
    }

    public UserLoginResponseDto processKakaoLogin(Oauth.Provider provider, String code, String state) {
        try {
            log.info("🛠 카카오 로그인 처리 시작! code: {}, state: {}", code, state);

            // 1.  토큰 발급 : 네이버
            String kakaoAccessToken = getAccessToken(provider, code, state);

            // 2. 사용자 정보 가져오기 : 네이버
            UserLoginResponseDto kakaoUserInfo = getKakaoUserInfo(kakaoAccessToken);

            // 3. 사용자 정보 처리 및 저장/업데이트  : 네이버
            UserLoginResponseDto kakaoUser = saveOrUpdateKakaoUser(kakaoUserInfo);


            return kakaoUser;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("카카오 로그인 처리 중 오류 발생: " + e.getMessage());
        }
    }

    private UserLoginResponseDto getKakaoUserInfo(String accessToken) {
        String userInfoUrl = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        HttpEntity<?> entity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<Map<String, Object>> userInfoResponse = restTemplate.exchange(
                userInfoUrl, HttpMethod.GET, entity, new ParameterizedTypeReference<>() {}
        );

        if (userInfoResponse.getStatusCode() != HttpStatus.OK || userInfoResponse.getBody() == null) {
            throw new SocialLoginException("소셜 네이버 실패 : 카카오, 사유 : 사용자 정보 요청 실패 , 응답코드 : " + userInfoResponse.getStatusCode());
        }

        // ✅ 카카오 응답 데이터 구조 확인 후 파싱
        Map<String, Object> responseMap = userInfoResponse.getBody();

        String identify = responseMap.get("id").toString(); // 카카오 유저 고유 ID

        // ✅ email 정보는 "kakao_account" 내부에 존재함.
        Map<String, Object> kakaoAccount = (Map<String, Object>) responseMap.get("kakao_account");


        if (kakaoAccount == null || !kakaoAccount.containsKey("email")) {
            throw new SocialLoginException("소셜 로그인 실패 : 카카오, 사유 : email 정보 없음");
        }


        String email = kakaoAccount.get("email").toString();

        return UserLoginResponseDto.builder()
                .identify(identify)
                .email(email)
                .build();
    }

    private UserLoginResponseDto saveOrUpdateKakaoUser(UserLoginResponseDto userInfo) {
        String kakoId = userInfo.getIdentify();
        String email = userInfo.getEmail();


        Optional<User> existingUser = usersRepository.findByEmail(email);
        User kakaoUser;

        if (existingUser.isPresent()) {
            //  카카오 계정 통해 로그인 이력이 있으면 로그인 진행.
            User nowUser = existingUser.get();
            TokenDto jwtToken = authenticateUser(nowUser);
            log.info("✅ 기존 회원: 이메일({}) - 로그인 완료", email);

            return UserLoginResponseDto.builder()
                    .message("로그인이 완료 되었습니다.")
                    .resultCode("200")
                    .provide(Oauth.Provider.KAKAO.name())
                    .identify(kakoId)
                    .email(email)
                    .username(nowUser.name)
                    .token(jwtToken.getAccessToken())
                    .build();
        } else {
            //  네이버 계정을 통한 로그인을 최초로 진행하는 경우

            return UserLoginResponseDto.builder()
                    .message("소셜 로그인을 위한 카카오 계정 등록이 완료되었습니다. 나머지 정보를 입력해 주세요. ")
                    .resultCode("201")
                    .provide(Oauth.Provider.KAKAO.name())
                    .identify(kakoId)
                    .email(email)
                    .build();
        }
    }

    private TokenDto authenticateUser(User userInfo) {
        // Spring Security 사용 시 SecurityContext에 인증 정보 설정
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userInfo, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        request.getSession().setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

        //  JWT 토큰 생성
        TokenDto jwtToken = tokenProvider.createToken(authentication);

        return jwtToken;
    }
}
