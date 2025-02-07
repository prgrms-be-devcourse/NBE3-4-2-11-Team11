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

            logout: async () => {
                if (typeof window === "undefined") return;

                const accessToken = localStorage.getItem("accessToken");
                if (!accessToken) {
                    console.warn("❌ 로그아웃 요청 실패: 저장된 accessToken 없음");
                    set({ isLoggedIn: false });
                    return;
                }

                try {
                    // ✅ 백엔드 로그아웃 API 호출 (토큰을 블랙리스트에 등록)
                    const response = await fetch("/api/v1/user/logout", {
                        method: "POST",
                        headers: {
                            "Content-Type": "application/json",
                            Authorization: `Bearer ${accessToken}`,
                        },
                        body: JSON.stringify({ token: accessToken })
                    });

                    if (!response.ok) {
                        throw new Error("❌ 로그아웃 API 요청 실패");
                    }

                    console.log("✅ 로그아웃 성공: 백엔드 블랙리스트 등록 완료");

                    // ✅ Local Storage에서 토큰 삭제
                    localStorage.removeItem("accessToken");
                    localStorage.removeItem("refreshToken");

                    // ✅ 로그인 상태 변경
                    set({ isLoggedIn: false });

                } catch (error) {
                    console.error("❌ 로그아웃 실패:", error);
                }
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
