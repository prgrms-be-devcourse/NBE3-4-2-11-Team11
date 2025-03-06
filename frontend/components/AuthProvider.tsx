"use client";

import { createContext, useContext, useEffect, useState } from "react";
import axios from "axios";

// ✅ 응답 데이터 타입 정의
interface AuthResponse {
  isLoggedIn: boolean;
  role: string | null;
}

// ✅ 로그인 상태 Context 생성
interface AuthContextType {
  isLoggedIn: boolean;
  userRole: string | null;
}

const AuthContext = createContext<AuthContextType | null>(null);

export const AuthProvider = ({ children }: { children: React.ReactNode }) => {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [userRole, setUserRole] = useState<string | null>(null);

  useEffect(() => {
    axios.get<AuthResponse>("/api/v1/auth/status", { withCredentials: true }) // ✅ 응답 타입 지정
      .then((response) => {
        console.log("로그인 상태:", response.data);
        const { isLoggedIn, role } = response.data; // ✅ 구조 분해 할당으로 개선

        setIsLoggedIn(isLoggedIn);
        setUserRole(role);
      })
      .catch((error) => {
        console.error("로그인 상태 확인 오류:", error);
        setIsLoggedIn(false);
        setUserRole(null);
      });
  }, []);

  return (
    <AuthContext.Provider value={{ isLoggedIn, userRole }}>
      {children}
    </AuthContext.Provider>
  );
};

// ✅ 로그인 상태를 다른 컴포넌트에서 가져올 수 있도록 `useAuth` 훅 제공
export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth must be used within an AuthProvider");
  }
  return context;
};
