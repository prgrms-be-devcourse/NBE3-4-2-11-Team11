// 

import {jwtDecode} from "jwt-decode"; // ✅ 라이브러리 활용

// 🔥 JWT의 Payload 구조를 인터페이스로 정의
interface DecodedJWT {
  exp?: number;
  sub?: string;  // ✅ 일반적으로 `sub`에 유저 ID가 저장됨
  userId?: number;
  email?: string;
}

// 🔥 JWT를 안전하게 디코딩하는 함수
export const decodeJWT = (token: string): DecodedJWT | null => {
  try {
    return jwtDecode<DecodedJWT>(token);  // ✅ 타입 지정하여 안전하게 반환
  } catch (error) {
    console.error("❌ JWT 디코딩 실패:", error);
    return null;
  }
};
