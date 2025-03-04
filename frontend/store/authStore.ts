import { create } from "zustand";

type AuthState = {
    isLoggedIn: boolean;
    role: "user" | "admin" | null;
    login: (role: "user" | "admin") => Promise<void>;
    logout: () => Promise<void>;
    checkAuthStatus: () => Promise<void>;
    refreshAccessToken: () => Promise<boolean>; // ✅ refreshAccessToken 추가
};

export const useAuthStore = create<AuthState>()(
    (set, get) => ({
        isLoggedIn: false,
        role: null,

        login: async (role) => {
            set({ isLoggedIn: true, role });

            if (typeof window !== "undefined") {
                localStorage.setItem("isLoggedIn", "true");
                localStorage.setItem("role", role);
                window.dispatchEvent(new Event("authChange"));
            }
        },

        logout: async () => {
            if (typeof window === "undefined") return;

            try {
                const role = get().role;
                const logoutEndpoint = role === "admin" ? "/api/v1/admin/logout" : "/api/v1/user/logout";

                await fetch(logoutEndpoint, {
                    method: "POST",
                    credentials: "include",
                });
            } catch (error) {
                console.error("로그아웃 API 호출 실패:", error);
            } finally {
                set({ isLoggedIn: false, role: null });

                if (typeof window !== "undefined") {
                    localStorage.removeItem("isLoggedIn");
                    localStorage.removeItem("role");
                    window.dispatchEvent(new Event("authChange"));
                }
            }
        },

        checkAuthStatus: async () => {
            try {
                const response = await fetch("/api/v1/auth/status", {
                    method: "GET",
                    credentials: "include",
                });

                if (!response.ok) {
                    console.warn("❌ 로그인 상태 확인 실패, 로그아웃 처리");
                    return;
                }

                const data = await response.json();

                set({ isLoggedIn: data.isLoggedIn, role: data.role });

                if (typeof window !== "undefined") {
                    localStorage.setItem("isLoggedIn", data.isLoggedIn ? "true" : "false");
                    localStorage.setItem("role", data.role);
//                     window.dispatchEvent(new Event("authChange"));
                }

            } catch (error) {
                console.error("❌ 로그인 상태 확인 중 오류 발생:", error);
                set({ isLoggedIn: false, role: null });
                localStorage.removeItem("isLoggedIn");
                localStorage.removeItem("role");
            }
        },

        // ✅ refreshAccessToken 추가 (기존 코드에서 누락됨)
        refreshAccessToken: async () => {
            try {
                const response = await fetch("/api/v1/token/refresh", {
                    method: "POST",
                    credentials: "include",
                });

                if (!response.ok) {
                    console.warn("❌ Refresh Token 만료됨, 로그아웃 진행");
                    get().logout();
                    return false;
                }

                console.log("✅ Access Token 갱신 완료");

                await get().checkAuthStatus(); // ✅ 갱신 후 로그인 상태 재확인
                return true;

            } catch (error) {
                console.error("❌ Access Token 갱신 실패:", error);
                return false;
            }
        },
    })
);

