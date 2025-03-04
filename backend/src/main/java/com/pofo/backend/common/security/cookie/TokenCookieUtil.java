package com.pofo.backend.common.security.cookie;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TokenCookieUtil {

    @Value("${jwt.expiration.time}")
    private Long validationTime;

    @Value("${jwt.refresh-token.expiration-time}")
    private Long refreshTokenValidationTime;

    public void setTokenCookies(HttpServletResponse response, String accessToken, String refreshToken) {
        //  accessToken을 쿠키에 넣자!
        Cookie accessCookie = new Cookie("accessCookie", accessToken);
        accessCookie.setHttpOnly(true);
//        accessCookie.setSecure(true);
        accessCookie.setSecure(false);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(validationTime.intValue()/ 1000); // ms -> s로 변환

        //  refreshToken을 쿠키에 넣자!
        Cookie refreshCookie = new Cookie("refreshCookie", refreshToken);
        refreshCookie.setHttpOnly(true);
//        refreshCookie.setSecure(true);
        refreshCookie.setSecure(false);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(refreshTokenValidationTime.intValue()/ 1000); // ms -> s로 변환

        response.addCookie(accessCookie);  // accessToken정보 cookie에 등록
        response.addCookie(refreshCookie);  // refreshToken정보 cookie에 등록
    }

    public void cleanTokenCookies(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setHttpOnly(true);  // HttpOnly 속성 추가
        cookie.setSecure(true);  // Secure 속성 추가
        response.addCookie(cookie);


    }
}
