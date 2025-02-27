// import { create } from "zustand";
//
// type AuthState = {
//     isLoggedIn: boolean;
//     login: () => Promise<void>;
//     logout: () => Promise<void>;
//     refreshAccessToken: () => Promise<boolean>;
//     checkAuthStatus: () => Promise<void>;
// };
//
// export const useAuthStore = create<AuthState>()(
//     (set, get) => ({
//         isLoggedIn: false,
//
//         login: async () => {
//             set({ isLoggedIn: true });
//
//             await get().checkAuthStatus(); // ✅ 서버 상태 확인
//
//             if (typeof window !== "undefined") {
//                 localStorage.setItem("isLoggedIn", "true");
//                 window.dispatchEvent(new Event("authChange")); // ✅ 커스텀 이벤트 발생
//             }
//         },
//
//         logout: async () => {
//             if (typeof window === "undefined") return;
//
//             try {
//                 await fetch("/api/v1/user/logout", {
//                     method: "POST",
//                     credentials: "include",
//                 });
//             } catch (error) {
//                 console.error("로그아웃 API 호출 실패:", error);
//             } finally {
//                 set({ isLoggedIn: false });
//
//                 if (typeof window !== "undefined") {
//                     localStorage.removeItem("isLoggedIn");
//                     window.dispatchEvent(new Event("authChange")); // ✅ 커스텀 이벤트 발생
//                 }
//             }
//         },
//
//         refreshAccessToken: async () => {
//             try {
//                 const response = await fetch("/api/v1/token/refresh", {
//                     method: "POST",
//                     credentials: "include",
//                 });
//
//                 if (!response.ok) {
//                     console.warn("❌ Refresh Token 만료됨, 로그아웃 진행");
//                     get().logout(); // ✅ 실패 시 로그아웃 실행
//                     return false;
//                 }
//
//                 console.log("✅ Access Token 갱신 완료");
//
//                 // ✅ 갱신 후 로그인 상태 재확인 (불필요한 상태 변경 방지)
//                 await get().checkAuthStatus();
//                 return true;
//
//             } catch (error) {
//                 console.error("❌ Access Token 갱신 실패:", error);
//                 return false;
//             }
//         },
//
//         checkAuthStatus: async () => {
//             try {
//                 const response = await fetch("/api/v1/auth/status", {
//                     method: "GET",
//                     credentials: "include",
//                 });
//
//                 if (!response.ok) {
//                     console.warn("❌ 로그인 상태 확인 실패, 로그아웃 처리");
//                     return;
//                 }
//
//                 const data = await response.json();
//
//                 // ✅ 상태가 변경될 때만 `set()` 실행 (불필요한 UI 리렌더링 방지)
//                 if (get().isLoggedIn !== data.isLoggedIn) {
//                     set({ isLoggedIn: data.isLoggedIn });
//                 }
//
//                 if (typeof window !== "undefined") {
//                     localStorage.setItem("isLoggedIn", data.isLoggedIn ? "true" : "false");
//                     window.dispatchEvent(new Event("authChange")); // ✅ 커스텀 이벤트 발생
//                 }
//
//             } catch (error) {
//                 console.error("❌ 로그인 상태 확인 중 오류 발생:", error);
//                 set({ isLoggedIn: false });
//                 localStorage.removeItem("isLoggedIn");
//             }
//         },
//     })
// );

import { create } from "zustand";

type AuthState = {
    isLoggedIn: boolean;
    role: "user" | "admin" | null; // ✅ 역할 추가
    login: (role: "user" | "admin") => Promise<void>;
    logout: () => Promise<void>;
    checkAuthStatus: () => Promise<void>;
};

export const useAuthStore = create<AuthState>()(
    (set, get) => ({
        isLoggedIn: false,
        role: null, // ✅ 기본값: null

        login: async (role) => {
            set({ isLoggedIn: true, role }); // ✅ 역할 저장

            if (typeof window !== "undefined") {
                localStorage.setItem("isLoggedIn", "true");
                localStorage.setItem("role", role); // ✅ 역할 저장
                window.dispatchEvent(new Event("authChange"));
            }
        },

        logout: async () => {
            if (typeof window === "undefined") return;

            try {
                const role = get().role; // ✅ 현재 사용자 역할 가져오기
                const logoutEndpoint = role === "admin" ? "/api/v1/admin/logout" : "/api/v1/user/logout";

                await fetch(logoutEndpoint, {
                    method: "POST",
                    credentials: "include",
                });
            } catch (error) {
                console.error("로그아웃 API 호출 실패:", error);
            } finally {
                set({ isLoggedIn: false, role: null }); // ✅ 역할도 초기화

                if (typeof window !== "undefined") {
                    localStorage.removeItem("isLoggedIn");
                    localStorage.removeItem("role"); // ✅ 역할 삭제
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

                set({ isLoggedIn: data.isLoggedIn, role: data.role }); // ✅ role 정보도 업데이트

                if (typeof window !== "undefined") {
                    localStorage.setItem("isLoggedIn", data.isLoggedIn ? "true" : "false");
                    localStorage.setItem("role", data.role);
                    window.dispatchEvent(new Event("authChange"));
                }

            } catch (error) {
                console.error("❌ 로그인 상태 확인 중 오류 발생:", error);
                set({ isLoggedIn: false, role: null });
                localStorage.removeItem("isLoggedIn");
                localStorage.removeItem("role");
            }
        },
    })
);
