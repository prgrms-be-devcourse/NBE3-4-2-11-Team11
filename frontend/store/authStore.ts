import { create } from "zustand";
import { persist, createJSONStorage } from "zustand/middleware";

type AuthState = {
    isLoggedIn: boolean;
    login: (token: string) => void;
    logout: () => void;
};

export const useAuthStore = create<AuthState>()(
    persist(
        (set) => ({
            isLoggedIn: false,

            login: (token) => {
                if (typeof window !== "undefined") {
                    localStorage.setItem("accessToken", token);
                }
                set({ isLoggedIn: true });
            },

            logout: () => {
                if (typeof window !== "undefined") {
                    localStorage.removeItem("accessToken");
                }
                set({ isLoggedIn: false });
            },
        }),
        {
            name: "auth-storage", // localStorage에 저장될 키 이름
            storage: typeof window !== "undefined"
                ? createJSONStorage(() => localStorage)
                : undefined, // 서버 환경에서는 localStorage 사용 X
        }
    )
);
