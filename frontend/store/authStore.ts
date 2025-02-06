// store 파일은 상태관리
// 로그인 상태관리

"use client";

import { create } from "zustand";

interface AuthState {
    isLoggedIn: boolean;
    login: (token: string) => void;
    logout: () => void;
}

// ✅ Zustand 스토어 생성
export const useAuthStore = create<AuthState>((set) => ({
    isLoggedIn: !!localStorage.getItem("accessToken"), // 초기 로그인 상태 확인
    login: (token) => {
        localStorage.setItem("accessToken", token);
        set({ isLoggedIn: true }); // Zustand 상태 업데이트
    },
    logout: () => {
        localStorage.removeItem("accessToken");
        set({ isLoggedIn: false }); // Zustand 상태 업데이트
    },
}));
