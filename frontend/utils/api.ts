
// /utils/api.ts

import axios, { type AxiosResponse, type InternalAxiosRequestConfig } from "axios";
import { useAuthStore } from "../store/authStore";

// ✅ Refresh Token 응답 타입 정의
export interface RefreshTokenResponse {
    accessToken: string;
    refreshToken: string;
}

const API_URL = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080/api/v1";


const api = axios.create({
    baseURL: API_URL,
    withCredentials: true, // ✅ 모든 요청에 자동으로 쿠키 포함
});

// ✅ 요청 인터셉터: Access Token 만료 시 자동으로 Refresh Token 요청
api.interceptors.request.use(
    async (config: InternalAxiosRequestConfig) => {
        return config; // ✅ 쿠키 기반 인증이므로 별도 토큰 추가 필요 없음
    },

    (error) => Promise.reject(error)
);

// ✅ 응답 인터셉터: 401 또는 403 응답 시 Refresh Token으로 재요청
api.interceptors.response.use(
    async (response: AxiosResponse) => response,
    async (error) => {
        const originalRequest = error.config;
        if (!originalRequest) {
            return Promise.reject(error);
        }

        if (error.response?.status === 401 && !originalRequest._retry) {
            originalRequest._retry = true;

            const { refreshAccessToken, login, logout } = useAuthStore.getState();
            const refreshed = await refreshAccessToken();

            if (refreshed) {
                console.log("✅ Refresh Token으로 Access Token 갱신 성공, 요청 재시도");

                // ✅ localStorage에서 role 가져오기
                const storedRole = localStorage.getItem("role") as "user" | "admin";

                login(storedRole ?? "user");  // ✅ 로그인 상태 업데이트

                // ✅ 기존 요청을 재시도, withCredentials 유지
                return api({
                    ...originalRequest,
                    withCredentials: true, // ✅ 쿠키 포함 유지
                });
            } else {
                console.error("❌ Refresh Token 갱신 실패 → 로그아웃 처리");
                logout(); // ✅ 로그아웃 상태로 전환
                return Promise.reject(error);
            }
        }

        return Promise.reject(error);
    }
);

export default api;