// import { create } from "zustand";
// import { persist, createJSONStorage } from "zustand/middleware";
//
// type AuthState = {
//     isLoggedIn: boolean;
//     login: (token: string) => void;
//     logout: () => void;
// };
//
// export const useAuthStore = create<AuthState>()(
//     persist(
//         (set) => ({
//             isLoggedIn: false,
//
//             login: (token) => {
//                 if (typeof window !== "undefined") {
//                     localStorage.setItem("accessToken", token);
//                 }
//                 set({ isLoggedIn: true });
//             },
//
//             logout: async () => {
//                 if (typeof window === "undefined") return;
//
//                 const accessToken = localStorage.getItem("accessToken");
//                 if (!accessToken) {
//                     console.warn("❌ 로그아웃 요청 실패: 저장된 accessToken 없음");
//                     set({ isLoggedIn: false });
//                     return;
//                 }
//
//                 try {
//                     // ✅ 백엔드 로그아웃 API 호출 (토큰을 블랙리스트에 등록)
//                     const response = await fetch("/api/v1/user/logout", {
//                         method: "POST",
//                         headers: {
//                             "Content-Type": "application/json",
//                             Authorization: `Bearer ${accessToken}`,
//                         },
//                         body: JSON.stringify({ token: accessToken })
//                     });
//
//                     if (!response.ok) {
//                         throw new Error("❌ 로그아웃 API 요청 실패");
//                     }
//
//                     console.log("✅ 로그아웃 성공: 백엔드 블랙리스트 등록 완료");
//
//                     // ✅ Local Storage에서 토큰 삭제
//                     localStorage.removeItem("accessToken");
//                     localStorage.removeItem("refreshToken");
//
//                     // ✅ 로그인 상태 변경
//                     set({ isLoggedIn: false });
//
//                 } catch (error) {
//                     console.error("❌ 로그아웃 실패:", error);
//                 }
//             },
//         }),
//         {
//             name: "auth-storage", // localStorage에 저장될 키 이름
//             storage: typeof window !== "undefined"
//                 ? createJSONStorage(() => localStorage)
//                 : undefined, // 서버 환경에서는 localStorage 사용 X
//         }
//     )
// );
// store/authStore.ts
import { create } from "zustand";
import { persist, createJSONStorage } from "zustand/middleware";

type AuthState = {
  isLoggedIn: boolean;
  // ✅ 현재 액세스 토큰, 리프레시 토큰
  accessToken: string | null;
  refreshToken: string | null;

  // 로그인/로그아웃
  login: (accessToken: string, refreshToken?: string) => void;
  logout: () => void;

  // ✅ Refresh Token을 이용해 새로운 토큰을 발급받는 함수
  refreshTokens: () => Promise<void>;
};

export const useAuthStore = create<AuthState>()(
  persist(
    (set, get) => ({
      isLoggedIn: false,
      accessToken: null,
      refreshToken: null,

      /**
       * 로그인 함수
       * - accessToken, refreshToken을 로컬 스토리지와 상태에 저장
       */
      login: (accessToken, refreshToken) => {
        if (typeof window !== "undefined") {
          localStorage.setItem("accessToken", accessToken);
          if (refreshToken) {
            localStorage.setItem("refreshToken", refreshToken);
          }
        }
        set({
          isLoggedIn: true,
          accessToken,
          refreshToken: refreshToken || null,
        });
      },

      /**
       * 로그아웃 함수
       */
      logout: async () => {
        if (typeof window === "undefined") return;

        const accessToken = localStorage.getItem("accessToken");
        if (!accessToken) {
          console.warn("❌ 로그아웃 요청 실패: 저장된 accessToken 없음");
          set({ isLoggedIn: false, accessToken: null, refreshToken: null });
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
            body: JSON.stringify({ token: accessToken }),
          });

          if (!response.ok) {
            throw new Error("❌ 로그아웃 API 요청 실패");
          }

          console.log("✅ 로그아웃 성공: 백엔드 블랙리스트 등록 완료");

          // ✅ LocalStorage에서 토큰 삭제
          localStorage.removeItem("accessToken");
          localStorage.removeItem("refreshToken");

          // ✅ Store 상태 초기화
          set({ isLoggedIn: false, accessToken: null, refreshToken: null });

        } catch (error) {
          console.error("❌ 로그아웃 실패:", error);
        }
      },

      /**
       * ✅ Refresh Token으로 새 토큰 발급받기
       */
      refreshTokens: async () => {
        if (typeof window === "undefined") return;

        // 저장된 refreshToken 확인
        const currentRefreshToken = localStorage.getItem("refreshToken");
        if (!currentRefreshToken) {
          console.warn("❌ refreshTokens 실패: 저장된 refreshToken이 없습니다.");
          return;
        }

        try {
          const response = await fetch("/api/v1/user/refresh-token", {
            method: "POST",
            headers: {
              // 서버에서 @RequestHeader("Refresh-Token")로 받는다고 하셨으므로
              "Refresh-Token": currentRefreshToken,
            },
          });

          if (!response.ok) {
            console.error("❌ RefreshToken API 요청 실패:", response.status);
            return;
          }

          /**
           * 서버 응답 구조 예시 (RsData<TokenDto>):
           * {
           *   "resultCode": "200",
           *   "message": "새로운 토큰이 발급되었습니다.",
           *   "data": {
           *       "accessToken": "...",
           *       "refreshToken": "...",
           *       "type": "Bearer",
           *       "accessTokenValidationTime": 1800000,
           *       "refreshTokenValidationTime": 604800000
           *   }
           * }
           */
          const result = await response.json();
          if (result.resultCode === "200") {
            const { accessToken, refreshToken } = result.data;

            // ✅ 새로운 토큰들을 로컬 스토리지와 Zustand에 저장
            localStorage.setItem("accessToken", accessToken);
            localStorage.setItem("refreshToken", refreshToken);

            set({
              isLoggedIn: true,
              accessToken,
              refreshToken,
            });

            console.log("✅ 토큰 갱신 성공:", result.data);
          } else {
            console.error("❌ 토큰 갱신 실패:", result);
          }

        } catch (error) {
          console.error("❌ 토큰 갱신 중 에러:", error);
        }
      },
    }),
    {
      name: "auth-storage", // localStorage에 저장될 key
      storage:
        typeof window !== "undefined"
          ? createJSONStorage(() => localStorage)
          : undefined, // 서버 환경에서는 localStorage 사용 X
    }
  )
);
