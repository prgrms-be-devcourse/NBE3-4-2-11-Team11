// /utils/token.ts

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

export const isAccessTokenExpired = (): boolean => {
  const token = getAccessToken();
  if (!token) return true; // ✅ 토큰이 없으면 만료된 것으로 간주

  try {
    const payload = JSON.parse(atob(token.split(".")[1])); // ✅ JWT Payload 디코딩
    const exp = payload.exp * 1000; // ✅ 만료 시간 (초 → 밀리초 변환)
    return Date.now() >= exp; // ✅ 현재 시간과 비교하여 만료 여부 확인

  } catch (error) {
    console.error("❌ Access Token 디코딩 실패:", error);
    return true; // ✅ 오류 발생 시 만료된 것으로 간주
  }
};
