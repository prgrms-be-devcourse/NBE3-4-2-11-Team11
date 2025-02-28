// /utils/token.ts

import {useAuthStore} from "@/store/authStore";

export const getAccessToken = (): string | null => {
  if (typeof window !== 'undefined') {
    return localStorage.getItem('accessToken');
  }
  return null;
};

export const getRefreshToken = (): string | null => {
  if (typeof window !== 'undefined') {
    return localStorage.getItem('refreshToken');
  }
  return null;
};

//  LocalStorage >> Cookie 적용 방식으로 갈아 끼우기 위한 함수
export const refreshAccessToken = async (): Promise<boolean> => {
  console.log('refresh access token');
  try {
    const response = await fetch("/api/v1/token/refresh", {
      method: "POST",
      credentials: "include", // ✅ HttpOnly 쿠키 자동 포함!
    });

    if (!response.ok) {
      console.warn("❌ Refresh Token이 만료됨, 로그아웃 진행");
      return false;
    }

    console.log("✅ Access Token 갱신 완료");

    // ✅ 강제 동기화 추가 (상태 반영이 안 되는 경우 대비)
    await useAuthStore.getState().checkAuthStatus();
    window.dispatchEvent(new Event("storage"));

    return true;
  } catch (error) {
    console.error("❌ Access Token 갱신 실패:", error);
    return false;
  }
};

//  토큰초기화용 함수
export const clearTokens = (): void => {
  if (typeof document !== 'undefined') {
    document.cookie = 'accessCookie=; Max-Age=0; path=/';
    document.cookie = 'refreshCookie=; Max-Age=0; path=/';
  }
};

export const setTokens = (accessToken: string, refreshToken?: string): void => {
  if (typeof window !== 'undefined') {
    localStorage.setItem('accessToken', accessToken);
    if (refreshToken) {
      localStorage.setItem('refreshToken', refreshToken);
    }
  }
};

export const removeTokens = (): void => {
  if (typeof window !== 'undefined') {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
  }
};
