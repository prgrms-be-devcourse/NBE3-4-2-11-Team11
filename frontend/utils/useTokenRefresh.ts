/*
 * 변경 사항:
 *
 * 1. 의존성 배열에 isLoggedIn만 포함했던 것을 [isLoggedIn, accessToken, refreshToken]으로 확장하여,
 *    토큰 값의 변경(예: 로그아웃 시 토큰 제거)이 즉시 반영되도록 했습니다.
 *
 * 2. 인터벌 내에서 매 실행 시 최신 refresh token을 재확인하고, refresh token이 없으면
 *    clearInterval()을 호출하여 인터벌을 종료함으로써 불필요한 갱신 요청 및 경고 메시지가 반복되지 않도록 했습니다.
 * 
 * 3.  localStorage에서 토큰을 체크하는 방식에서 Cookie에 있는 refreshToken을 직접 체크하기 때문에 LocalStorage 부분은
 *     걷어냄
 */

"use client";

import { useEffect } from "react";
import { useAuthStore } from "../store/authStore";

const useTokenRefresh = () => {
  const { isLoggedIn, refreshAccessToken } = useAuthStore(); // ✅ refreshAccessToken 가져오기

  useEffect(() => {
    if (!isLoggedIn) {
      return;
    }

    const interval = setInterval(async () => {
      console.log("🔍 Access Token 상태 주기적 확인...");

      // ✅ Access Token 만료 감지 → Refresh Token 요청 실행
      const success = await refreshAccessToken();

      if(!success) {
        //console.warn("❌ Refresh Token 갱신 실패, 인터벌 종료");
        clearInterval(interval); // ✅ Refresh 실패 시 인터벌 중지
        return;
      }

      await useAuthStore.getState().checkAuthStatus();

    }, 20000); // 20초마다 실행



    return () => clearInterval(interval);
  }, [isLoggedIn, refreshAccessToken]); // ✅ refreshAccessToken 추가하여 최신 상태 반영
};

export default useTokenRefresh;
