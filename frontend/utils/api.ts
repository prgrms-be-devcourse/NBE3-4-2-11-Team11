// /utils/api.ts
import axios, { AxiosInstance, AxiosResponse, InternalAxiosRequestConfig } from "axios";
import { getAccessToken, getRefreshToken, setTokens, removeTokens, isAccessTokenExpired } from "./token";
import { useAuthStore } from "../store/authStore";

const API_URL = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080/api/v1";

const api = axios.create({
    baseURL: API_URL,
});

// ✅ 요청 인터셉터: Access Token 만료 시 자동으로 Refresh Token 요청
api.interceptors.request.use(
    async (config: InternalAxiosRequestConfig<any>) => {
        let token = getAccessToken();

        if (isAccessTokenExpired()) {  // ✅ Access Token 만료 여부 체크
            console.warn("🔄 Access Token 만료 감지 → Refresh Token 요청 실행");
            const refreshed = await useAuthStore.getState().refreshAccessToken();
            if (!refreshed) {
                console.error("❌ Access Token 갱신 실패 → 요청 중단");
                return Promise.reject(new Error("Access Token 갱신 실패"));
            }
            token = getAccessToken(); // ✅ 갱신된 토큰 가져오기
        }

        if (token && config.headers) {
            config.headers["Authorization"] = `Bearer ${token}`;
        }
        return config;
    },
    (error) => Promise.reject(error)
);

// ✅ 응답 인터셉터: 401 또는 403 응답 시 Refresh Token으로 재요청
api.interceptors.response.use(
    async (response: AxiosResponse<any, any>) => response,
    async (error) => {
        const originalRequest = error.config;
        if (!originalRequest) {
            return Promise.reject(error);
        }

        if (error.response?.status === 401 && !originalRequest._retry) {  // ✅ 401 에러 감지
            originalRequest._retry = true; // ✅ 재요청 방지 플래그 추가
            const refreshToken: string | null = getRefreshToken();
            if (refreshToken) {
                try {
                    // ✅ RefreshToken을 사용하여 새로운 AccessToken 요청
                    const refreshResponse = await axios.post(
                        `${API_URL}/auth/refresh-token`,
                        { refreshToken },
                        { headers: { "Content-Type": "application/json" } }
                    );

                    const { accessToken, refreshToken: newRefreshToken } = refreshResponse.data;
                    setTokens(accessToken, newRefreshToken);

                    // ✅ 새 AccessToken으로 요청 다시 보내기
                    originalRequest.headers["Authorization"] = `Bearer ${accessToken}`;
                    return api(originalRequest);
                } catch (refreshError) {
                    console.error("❌ RefreshToken 갱신 실패:", refreshError);
                    removeTokens();
                    return Promise.reject(refreshError);
                }
            }
        }

        return Promise.reject(error);
    }
);

export default api;
