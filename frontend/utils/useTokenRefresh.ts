/*
 * 변경 사항:
 *
 * 1. 의존성 배열에 isLoggedIn만 포함했던 것을 [isLoggedIn, accessToken, refreshToken]으로 확장하여,
 *    토큰 값의 변경(예: 로그아웃 시 토큰 제거)이 즉시 반영되도록 했습니다.
 *
 * 2. 인터벌 내에서 매 실행 시 최신 refresh token을 재확인하고, refresh token이 없으면
 *    clearInterval()을 호출하여 인터벌을 종료함으로써 불필요한 갱신 요청 및 경고 메시지가 반복되지 않도록 했습니다.
 */

"use client";

import { useEffect } from "react";
import { getAccessToken, getRefreshToken, isAccessTokenExpired } from "../utils/token";
import { useAuthStore } from "../store/authStore";
import { decodeJWT } from "@/utils/decodeJWT";

const useTokenRefresh = () => {
  // zustand 스토어에서 현재 로그인 상태와 토큰 값을 가져옴
  const isLoggedIn = useAuthStore((state) => state.isLoggedIn);
  const accessToken = useAuthStore((state) => state.accessToken);
  const refreshToken = useAuthStore((state) => state.refreshToken);

  useEffect(() => {
    // 로그인 상태가 아니거나 토큰들이 없으면 인터벌 설정하지 않음
    if (!isLoggedIn || !accessToken || !refreshToken) {
      return;
    }

    const interval = setInterval(async () => {
      // 매번 실행할 때 최신 refresh token 확인
      const currentRefreshToken = getRefreshToken();
      if (!currentRefreshToken) {
        console.warn("❌ Refresh Token이 없습니다. 인터벌 종료합니다.");
        clearInterval(interval);
        return;
      }

      const decoded = decodeJWT(currentRefreshToken);
      console.log("🔍 디코딩된 Refresh Token:", decoded);

      if (isAccessTokenExpired()) {
        console.log("🔄 Access Token 만료 감지 → Refresh Token 요청 실행");
        await useAuthStore.getState().refreshAccessToken();
      }
    }, 20000); // 20초마다 실행

    return () => clearInterval(interval);
  }, [isLoggedIn, accessToken, refreshToken]); // 토큰 값이 변경될 때마다 효과 재실행
};

export default useTokenRefresh;
