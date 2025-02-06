import { create } from "zustand";

interface AuthState {
    isLoggedIn: boolean;
    login: (token: string) => void;
    logout: () => void;
}

// ✅ SSR에서 localStorage 접근 방지 & 초기값 false 설정
export const useAuthStore = create<AuthState>((set) => ({
    isLoggedIn: false, // 🔥 서버에서 렌더링될 때는 무조건 false로 설정

    login: (token) => {
        if (typeof window !== "undefined") { // 🔥 브라우저 환경에서만 실행
            localStorage.setItem("accessToken", token);
        }
        set({ isLoggedIn: true });
    },

    logout: () => {
        if (typeof window !== "undefined") {
            localStorage.removeItem("accessToken");
        }
        set({ isLoggedIn: false });
    }
}));
