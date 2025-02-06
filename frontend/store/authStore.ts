"use client"; // ✅ 클라이언트 컴포넌트에서만 실행

import { create } from "zustand";

interface AuthState {
    isLoggedIn: boolean;
    login: () => void;
    logout: () => void;
}

// ✅ localStorage 제거 & 클라이언트에서만 상태 관리
export const useAuthStore = create<AuthState>((set) => ({
    isLoggedIn: false, // 기본값은 false (서버에서는 항상 false)

    login: () => {
        set({ isLoggedIn: true }); // 로그인 상태 true로 변경
    },

    logout: () => {
        set({ isLoggedIn: false }); // 로그아웃 시 false
    },
}));
