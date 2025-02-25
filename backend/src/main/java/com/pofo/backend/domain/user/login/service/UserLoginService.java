package com.pofo.backend.domain.user.login.service;

import com.pofo.backend.common.exception.SocialLoginException;
import com.pofo.backend.common.security.CustomUserDetails;
import com.pofo.backend.common.security.dto.TokenDto;
import com.pofo.backend.common.security.jwt.TokenProvider;
import com.pofo.backend.domain.user.join.entity.Oauth;
import com.pofo.backend.domain.user.join.entity.User;
import com.pofo.backend.domain.user.join.repository.OauthRepository;
import com.pofo.backend.domain.user.join.repository.UserRepository;
import com.pofo.backend.domain.user.login.dto.GoogleTokenResponse;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
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
    private final UserRepository userRepository;

    private  final OauthRepository oauthRepository;

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

    //  Google Oauths 정보 시작
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String googleRedirectUri;
    //  Google Oauths 정보 끝

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

    /*
    *  소스내용 변경 :
    *   기존에 네이버용 토큰만 뿌렸다면, 소셜로그인 provider가 증가함에 따라 provider 별로 분기 나눠 토큰
    *   발생
    * */
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

        } else if (provider == Oauth.Provider.GOOGLE) {
            tokenRequestUrl = "https://oauth2.googleapis.com/token";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
            requestBody.add("grant_type", "authorization_code");
            requestBody.add("client_id", googleClientId);
            requestBody.add("client_secret", googleClientSecret);
            requestBody.add("code", code);
            requestBody.add("redirect_uri", googleRedirectUri); // ✅ 필수 파라미터

            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);
            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<GoogleTokenResponse> tokenResponse = restTemplate.postForEntity(
                    tokenRequestUrl, requestEntity, GoogleTokenResponse.class
            );

            if (tokenResponse.getStatusCode() != HttpStatus.OK || tokenResponse.getBody() == null) {
                throw new SocialLoginException("소셜 로그인 실패: 구글, 사유: 토큰 취득 실패. 응답코드: " + tokenResponse.getStatusCode());
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

        // ✅ 1. 이메일을 기반으로 기존 사용자(User) 조회
        Optional<User> existingUser = userRepository.findByEmail(email);

        if (existingUser.isPresent()) {
            //  네이버 계정 통해 로그인 이력이 있으면 로그인 진행.
            User nowUser = existingUser.get();

            // ✅ 2. Oauths 테이블에서 동일한 유저 & Provider(NAVER) 정보 조회
            Optional<Oauth> existingOauth = oauthRepository.findByUserAndProvider(nowUser, Oauth.Provider.NAVER);

            if (existingOauth.isEmpty()) {
                // 🔹 3. Oauths 정보가 없다면 새로 추가 (네이버 계정으로 처음 로그인하는 경우)
                Oauth newOauth = Oauth.builder()
                        .user(nowUser)
                        .provider(Oauth.Provider.NAVER)
                        .identify(naverId)
                        .build();

                oauthRepository.save(newOauth);
                log.info("🔗 Oauths 테이블에 네이버 로그인 정보 추가 - 이메일({})", email);
            }

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
                    .refreshToken(jwtToken.getRefreshToken())
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

            // 1.  토큰 발급 : 카카오
            String kakaoAccessToken = getAccessToken(provider, code, state);

            // 2. 사용자 정보 가져오기 : 카카오
            UserLoginResponseDto kakaoUserInfo = getKakaoUserInfo(kakaoAccessToken);

            // 3. 사용자 정보 처리 및 저장/업데이트  : 카카오
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

        // ✅ 1. 이메일을 기반으로 기존 사용자(User) 조회
        Optional<User> existingUser = userRepository.findByEmail(email);

        if (existingUser.isPresent()) {
            //  카카오 계정 통해 로그인 이력이 있으면 로그인 진행.
            User nowUser = existingUser.get();

            // ✅ 2. Oauths 테이블에서 동일한 유저 & Provider(KAKAO) 정보 조회
            Optional<Oauth> existingOauth = oauthRepository.findByUserAndProvider(nowUser, Oauth.Provider.KAKAO);

            if (existingOauth.isEmpty()) {
                // 🔹 3. Oauths 정보가 없다면 새로 추가 (카카오 계정으로 처음 로그인하는 경우)
                Oauth newOauth = Oauth.builder()
                        .user(nowUser)
                        .provider(Oauth.Provider.KAKAO)
                        .identify(kakoId)
                        .build();

                oauthRepository.save(newOauth);
                log.info("🔗 Oauths 테이블에 카카오 로그인 정보 추가 - 이메일({})", email);
            }

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
                    .refreshToken(jwtToken.getRefreshToken())
                    .build();
        } else {
            //  카카오 계정을 통한 로그인을 최초로 진행하는 경우

            return UserLoginResponseDto.builder()
                    .message("소셜 로그인을 위한 카카오 계정 등록이 완료되었습니다. 나머지 정보를 입력해 주세요. ")
                    .resultCode("201")
                    .provide(Oauth.Provider.KAKAO.name())
                    .identify(kakoId)
                    .email(email)
                    .build();
        }
    }

    public UserLoginResponseDto processGoogleLogin(Oauth.Provider provider, String code) {
        try {
            log.info("🛠 구글 로그인 처리 시작! code: {}, state: {}", code);

            // 1.  토큰 발급 : 구글
            String googleAccessToken = getAccessToken(provider, code, null);

            // 2. 사용자 정보 가져오기 : 구글
            UserLoginResponseDto googleUserInfo = getGoogleUserInfo(googleAccessToken);

            // 3. 사용자 정보 처리 및 저장/업데이트  : 구글
            UserLoginResponseDto googleUser = saveOrUpdateGoogleUser(googleUserInfo);


            return googleUser;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("구글 로그인 처리 중 오류 발생: " + e.getMessage());
        }
    }

    private UserLoginResponseDto getGoogleUserInfo(String accessToken) {
        String userInfoUrl = "https://www.googleapis.com/oauth2/v2/userinfo";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        HttpEntity<?> entity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<Map<String, Object>> userInfoResponse = restTemplate.exchange(
                userInfoUrl, HttpMethod.GET, entity, new ParameterizedTypeReference<>() {}
        );

        if (userInfoResponse.getStatusCode() != HttpStatus.OK || userInfoResponse.getBody() == null) {
            throw new SocialLoginException("소셜 구글 실패 : 구글, 사유 : 사용자 정보 요청 실패 , 응답코드 : " + userInfoResponse.getStatusCode());
        }

        // ✅ 구글 응답 데이터 구조 확인 후 파싱
        Map<String, Object> responseMap = userInfoResponse.getBody();

        // ✅ 구글에서 유저 고유 ID는 "id" 또는 "sub" 키 사용
        String identify = null;
        if (responseMap.containsKey("id")) {
            identify = responseMap.get("id").toString();
        } else if (responseMap.containsKey("sub")) {
            identify = responseMap.get("sub").toString();
        }

        if (identify == null) {
            throw new SocialLoginException("소셜 로그인 실패: 구글, 사유: 사용자 ID 없음");
        }

        // ✅ 구글 응답에는 "google_account" 키가 없음 → 바로 email 필드 확인
        String email = (String) responseMap.get("email");

        if (email == null || email.isEmpty()) {
            throw new SocialLoginException("소셜 로그인 실패: 구글, 사유: email 정보 없음");
        }

        return UserLoginResponseDto.builder()
                .identify(identify)
                .email(email)
                .build();
    }

    private UserLoginResponseDto saveOrUpdateGoogleUser(UserLoginResponseDto userInfo) {
        String googleId = userInfo.getIdentify();
        String email = userInfo.getEmail();

        // ✅ 1. 이메일을 기반으로 기존 사용자(User) 조회
        Optional<User> existingUser = userRepository.findByEmail(email);

        if (existingUser.isPresent()) {
            User nowUser = existingUser.get();

            // ✅ 2. Oauths 테이블에서 동일한 유저 & Provider(GOOGLE) 정보 조회
            Optional<Oauth> existingOauth = oauthRepository.findByUserAndProvider(nowUser, Oauth.Provider.GOOGLE);

            if (existingOauth.isEmpty()) {
                // 🔹 3. Oauths 정보가 없다면 새로 추가 (구글 계정으로 처음 로그인하는 경우)
                Oauth newOauth = Oauth.builder()
                        .user(nowUser)
                        .provider(Oauth.Provider.GOOGLE)
                        .identify(googleId)
                        .build();

                oauthRepository.save(newOauth);
                log.info("🔗 Oauths 테이블에 GOOGLE 로그인 정보 추가 - 이메일({})", email);
            }

            // ✅ 4. 로그인 처리 (기존 계정 존재)
            TokenDto jwtToken = authenticateUser(nowUser);
            log.info("✅ 기존 회원: 이메일({}) - 로그인 완료", email);

            return UserLoginResponseDto.builder()
                    .message("로그인이 완료되었습니다.")
                    .resultCode("200")
                    .provide(Oauth.Provider.GOOGLE.name())
                    .identify(googleId)
                    .email(email)
                    .username(nowUser.getName())
                    .token(jwtToken.getAccessToken())
                    .refreshToken(jwtToken.getRefreshToken())
                    .build();
        } else {
            // ✅ 5. 신규 회원 → 추가 정보 입력 필요
            log.info("🆕 신규 회원: 이메일({}) - 구글 로그인 최초 시도, 추가 정보 입력 필요", email);

            return UserLoginResponseDto.builder()
                    .message("소셜 로그인을 위한 구글 계정 등록이 완료되었습니다. 나머지 정보를 입력해 주세요.")
                    .resultCode("201")
                    .provide(Oauth.Provider.GOOGLE.name())
                    .identify(googleId)
                    .email(email)
                    .build();
        }
    }


    private TokenDto authenticateUser(User userInfo) {
        // User 객체를 CustomUserDetails로 감싸기
        CustomUserDetails customUserDetails = new CustomUserDetails(userInfo);

        // Spring Security 사용 시 SecurityContext에 인증 정보 설정
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(customUserDetails, null, authorities);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        request.getSession().setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

        //  JWT 토큰 생성
        TokenDto jwtToken = tokenProvider.createToken(authentication);

        log.info("✅ JWT Access Token: {}", jwtToken.getAccessToken());
        log.info("✅ JWT Refresh Token: {}", jwtToken.getRefreshToken());

        return jwtToken;
    }


}
